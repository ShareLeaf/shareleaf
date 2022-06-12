
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

class Instagram(S3):
    """Parses Instagram content"""

    def __init__(self, soup, url, uid, db, app):
        super(Instagram, self).__init__()
        self.soup = soup
        self.url = url
        self.uid = uid
        self.db = db
        self.app = app