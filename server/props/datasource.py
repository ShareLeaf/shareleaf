import sys
from os.path import dirname
sys.path.append(dirname(__file__).split("/server")[0])
import os


class DatasourceProps:
    def __init__(self):
        self.url = os.getenv('SHARELEAF_DATASOURCE_URL')
        self.username = os.getenv('SHARELEAF_DATASOURCE_USERNAME')
        self.password = os.getenv('SHARELEAF_DATASOURCE_PASSWORD')
        self.bind_key = "primary"

    def get_full_url(self):
        return "postgresql://{}:{}@{}".format(self.username,
                                              self.password,
                                              self.url)