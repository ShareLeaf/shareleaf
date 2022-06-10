import uuid
import json
import requests
import subprocess
import sys
from os.path import dirname
import datetime
import ffmpeg
import time

sys.path.append(dirname(__file__).split("/server")[0])

from server.service.aws import S3
from server.models.models import Metadata
from server.utils import utils

headers = {'User-Agent': utils.USER_AGENT}


def _generate_thumbnail(v_filename, thumbnail_filename):
    """Generate a thumbnail from a given video file"""
    try:
        (
            ffmpeg
                .input(v_filename, ss=0.1)
                .filter('scale', 256, -1)
                .output(thumbnail_filename, vframes=1)
                .overwrite_output()
                .run(capture_stdout=True, capture_stderr=True)
        )
        print("Thumbnail generated: ", thumbnail_filename)
    except ffmpeg.Error as e:
        print("Error while generating a thumbnail: ", thumbnail_filename)


class Reddit(S3):
    """Parses Reddit content"""

    def __init__(self, soup, url, uid, db, app):
        super(Reddit, self).__init__()
        self.soup = soup
        self.url = url
        self.uid = uid
        self.db = db
        self.app = app

    def _invalid_url(self):
        with self.app.app_context():
            records_to_update = [{
                "id": self.uid,
                'invalid_url': True,
                'updated_at': datetime.datetime.now()
            }]
            self.db.session.bulk_update_mappings(Metadata, records_to_update)
            utils.save(self.db)

    def _parse_media_urls(self):
        post_id = self.url[self.url.find('comments/') + 9:]
        post_id = f"t3_{post_id[:post_id.find('/')]}"
        required_js = self.soup.find('script', id='data')
        json_data = json.loads(required_js.text.replace('window.___r = ', '')[:-1])
        media_type = self._get_media_type(json_data, post_id)
        title = json_data['posts']['models'][post_id]['title']
        permalink = json_data['posts']['models'][post_id]['permalink']
        records_to_update = {
            "id": self.uid,
            'title': title,
            'canonical_url': permalink,
            'updated_at': datetime.datetime.now()
        }
        image_url = ""
        video_url = ""
        audio_url = ""
        if media_type.get("is_image"):
            records_to_update["media_type"] = "image"
            records_to_update["encoding"] = "jpg"
            image_url = json_data['posts']['models'][post_id]['media'].get('content')
        elif media_type.get("is_gifvideo"):
            records_to_update["media_type"] = "video"
            records_to_update["encoding"] = "video/mp4"
            video_url = json_data['posts']['models'][post_id]['media'].get('content')
        elif media_type.get("is_gif") or \
                media_type.get("is_video") or \
                media_type.get("is_video_preview"):
            dash_url = json_data['posts']['models'][post_id]['media'].get('dashUrl')
            dash_url = dash_url[:int(dash_url.find('DASH')) + 4]
            height = json_data['posts']['models'][post_id]['media'].get('height')
            audio_url = f'{dash_url}_audio.mp4'  # Yes, it is mp4 and not mp3
            if media_type.get("is_video_preview"):
                audio_url = ''
                dash_url = json_data['posts']['models'][post_id]['media'].get('videoPreview').get('dashUrl')
                height = json_data['posts']['models'][post_id]['media'].get('videoPreview').get('height')
            video_url = f'{dash_url}_{height}.mp4'
            records_to_update["encoding"] = "video/mp4"
            if media_type.get("is_gif"):
                audio_url = ''
                records_to_update["media_type"] = "gif"
            else:
                records_to_update["media_type"] = "video"

        with self.app.app_context():
            self.db.session.bulk_update_mappings(Metadata, [records_to_update])
            utils.save(self.db)
        print("Metadata processed for {}: video={} audio={} image={}".format(
            self.uid,
            video_url,
            audio_url,
            image_url))
        return {
            "title": title,
            "video_url": video_url,
            "audio_url": audio_url,
            "image_url": image_url
        }

    def _download_media(self, url, file_name):
        print("Downloading Reddit media content at ", url)
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            with open(file_name, 'wb') as file:
                file.write(response.content)
                print("Content download success for ", url, file_name)
                return True
        else:
            print("Failed to download content")
            self._invalid_url()
            return False

    def process_soup(self):
        print("Processing Reddit soup")
        try:
            metadata = self._parse_media_urls()
            filename = str(uuid.uuid4())
            merged_video_filename = str(uuid.uuid4()) + "_merged.mp4"
            thumbnail_filename = str(uuid.uuid4()) + "_thumbnail.jpg"
            image_filename = str(uuid.uuid4()) + "_image.jpg"
            v_filename = filename + "_video.mp4"
            a_filename = filename + "_audio.mp4"
            print("metadata: ", metadata)
            v_downloaded = False
            a_downloaded = False
            i_downloaded = False
            if metadata.get("video_url"):
                v_downloaded = self._download_media(metadata.get("video_url"), v_filename)
            if metadata.get("audio_url"):
                a_downloaded = self._download_media(metadata.get("audio_url"), a_filename)
            if metadata.get("image_url"):
                i_downloaded = self._download_media(metadata.get("image_url"), image_filename)

                # merge the audio and video streams into one if both are present
            if v_downloaded and a_downloaded:
                _generate_thumbnail(v_filename, thumbnail_filename)
                video_stream = ffmpeg.input(v_filename)
                audio_stream = ffmpeg.input(a_filename)
                start = int(time.time() * 1000)
                try:
                    print("ffmpeg Processing has started...")
                    ffmpeg.output(audio_stream, video_stream, merged_video_filename, vcodec='copy').run()
                    end = int(time.time() * 1000)
                    print("ffmpeg Processing has ended...Time(ms): ", end - start)
                except Exception as e:
                    print("Encountered an error while processing video")
            elif v_downloaded:
                # this is the case if it's a GIF
                merged_video_filename = v_filename
                _generate_thumbnail(v_filename, thumbnail_filename)

            if v_downloaded:
                # Upload the video S3 for CDN distribution
                print("about to upload files: ", merged_video_filename, thumbnail_filename)
                start = int(time.time() * 1000)
                self.upload_file(merged_video_filename, self.uid + ".mp4")
                self.upload_file(thumbnail_filename, self.uid + ".jpg")
                end = int(time.time() * 1000)
                print("video upload has ended...Time(ms): ", end - start)
            elif i_downloaded:
                # Upload the image but use it as the thumbnail as well
                self.upload_file(image_filename, self.uid + ".jpg")

            # Delete the files
            print("Deleting all files")
            subprocess.call(['rm', '-rf',
                             v_filename,
                             a_filename,
                             merged_video_filename,
                             thumbnail_filename,
                             image_filename])
            print("Done with processing Reddit soup", metadata)

            with self.app.app_context():
                records_to_update = [{
                    "id": self.uid,
                    "processed": True,
                    'updated_at': datetime.datetime.now()
                }]
                self.db.session.bulk_update_mappings(Metadata, records_to_update)
                utils.save(self.db)
        except Exception as e:
            self._invalid_url()
            print("Encountered an error: ", e)

    def _get_media_type(self, json_data, post_id):
        # with open(self.uid + ".json", "w") as f:
        #     json.dump(json_data, f)
        try:
            return {
                "is_gif": json_data['posts']['models'][post_id]['media'].get('isGif'),
                "is_video": json_data['posts']['models'][post_id]['media'].get('type') == "video",
                "is_video_preview": json_data['posts']['models'][post_id]['media'].get('videoPreview') is not None,
                "is_image": json_data['posts']['models'][post_id]['media'].get('type') == "image",
                "is_gifvideo": json_data['posts']['models'][post_id]['media'].get('type') == "gifvideo",
            }
        except Exception as e:
            return {}
