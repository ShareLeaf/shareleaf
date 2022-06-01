import boto3
import os


class S3:
    """Uploads media content to S3"""

    def __init__(self):
        self.s3_bucket = os.getenv('S3_BUCKET_ARTIFACTS')
        # self.key

    def upload_file(self, filename):
        s3 = boto3.resource('s3')
        s3.meta.client.upload_file('/tmp/hello.txt', 'mybucket', 'hello.txt')
