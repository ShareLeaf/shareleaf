import os


class AWSProps:
    def __init__(self):
        self.access_key_id = ""
        self.secret_access_key = ""
        self.bucket_name = ""
        self.cdn = ""
        try:
            self.access_key_id = os.getenv('SHARELEAF_AWS_ACCESS_KEY_ID')
            self.secret_access_key = os.getenv('SHARELEAF_AWS_SECRET_KEY_ID')
            self.bucket_name = os.getenv('SHARELEAF_AWS_BUCKET_NAME')
            self.cdn = os.getenv('SHARELEAF_AWS_CDN')
        except AttributeError as e:
            # It's possible that the configs may not have been provided for the profile
            pass
