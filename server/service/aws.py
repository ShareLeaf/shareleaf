import boto3
import os


class S3:
    """Uploads media content to S3"""

    def __init__(self):
        self.session = boto3.Session(
            aws_access_key_id='AKIAX2IAEH45BPGE7RU6',
            aws_secret_access_key='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'
        )
        self.s3 = self.session.resource('s3')
        # self.key

    def upload_file(self, filename, uid):
        object = self.s3.Object("shareleaf", uid)
        with open(filename, "rb") as f:
            object.put(Body=f.read())
