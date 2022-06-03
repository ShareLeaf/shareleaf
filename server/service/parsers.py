import uuid
import json
import requests
import subprocess
import sys
from os.path import dirname
import datetime

sys.path.append(dirname(__file__).split("/server")[0])

from server.service.aws import S3
from server.models.models import Metadata
from server.utils import utils

headers = {'User-Agent': utils.USER_AGENT}


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

    def _parse_video_audio_urls(self):
        post_id = self.url[self.url.find('comments/') + 9:]
        post_id = f"t3_{post_id[:post_id.find('/')]}"
        required_js = self.soup.find('script', id='data')
        json_data = json.loads(required_js.text.replace('window.___r = ', '')[:-1])
        title = json_data['posts']['models'][post_id]['title']
        dash_url = json_data['posts']['models'][post_id]['media']['dashUrl']
        height = json_data['posts']['models'][post_id]['media']['height']
        dash_url = dash_url[:int(dash_url.find('DASH')) + 4]
        video_url = f'{dash_url}_{height}.mp4'
        audio_url = f'{dash_url}_audio.mp4'
        with self.app.app_context():
            records_to_update = [{
                "id": self.uid,
                'title': title,
                'encoding': 'video/webm',
                'media_type': 'video',
                'processed': True,
                'updated_at': datetime.datetime.now()
            }]
            self.db.session.bulk_update_mappings(Metadata, records_to_update)
            utils.save(self.db)
        return {
            "title": title,
            "video_url": video_url,
            "audio_url": audio_url
        }

    def _download_media(self, url, file_name):
        print("Downloading Reddit media content at ", url)
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            with open(file_name, 'wb') as file:
                file.write(response.content)
        else:
            print("Failed to download content")
            self._invalid_url()

    def process_soup(self):
        print("Processing Reddit soup")
        try:
            metadata = self._parse_video_audio_urls()
            filename = str(uuid.uuid4())
            merged_filename = str(uuid.uuid4()) + ".mp4"
            v_filename = filename + ".mp4"
            a_filename = filename + ".mp3"
            self._download_media(metadata.get("video_url"), v_filename)
            self._download_media(metadata.get("audio_url"), a_filename)

            # merge the audio and video streams into one
            subprocess.call(['ffmpeg', '-i', v_filename, '-i', a_filename, '-map', '0:v', '-map', '1:a', '-c:v', 'copy',
                             merged_filename])

            # Upload to S3 for CDN distribution
            self.upload_file(merged_filename, self.uid + ".mp4")

            # Delete the files
            subprocess.call(['rm', v_filename, a_filename, merged_filename])
            print("Done with processing Reddit soup", metadata)
        except Exception as e:
            self._invalid_url()
            print(e)
