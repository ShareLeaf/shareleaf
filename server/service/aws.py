import boto3
import sys
from os.path import dirname

sys.path.append(dirname(__file__).split("/server")[0])
from server.props.aws import AWSProps


class S3:
    """Uploads media content to S3"""

    def __init__(self):
        self.props = AWSProps()
        self.session = boto3.Session(
            aws_access_key_id=self.props.access_key_id,
            aws_secret_access_key=self.props.secret_access_key
        )
        self.s3 = self.session.resource('s3')
        # self.key

    def upload_file(self, filename, uid):
        _object = self.s3.Object(self.props.bucket_name, uid)
        with open(filename, "rb") as f:
            _object.put(Body=f.read())
            print("Successfully uploaded ", filename)
