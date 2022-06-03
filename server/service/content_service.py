import sys
from os.path import dirname
import random
import requests
from bs4 import BeautifulSoup
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import func

sys.path.append(dirname(__file__).split("/server")[0])

from server.service.parsers import Reddit
from server.models.models import  Metadata
from server.utils.utils import USER_AGENT

headers = {'User-Agent': USER_AGENT}

headers = {'User-Agent': USER_AGENT}

def get_random_character() -> str:
    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqprstuvwxyz0123456789"
    return random.choice(list(alphabet))


def generate_uid() -> str:
    length = 7
    uid = ""
    for i in range(length):
        uid += get_random_character()
    return uid


def generate_content_id(src: str, app: Flask, db: SQLAlchemy) -> str:
    """Return content id if one exists or generate a unique one otherwise"""
    with app[0].app_context():
        query = db.session.query(Metadata).filter(Metadata.canonical_url.in_([src]))
        records = query.all()
        if records:
            print("returning from the database")
            return records[0].id
        print("generating a new one")
        _generated_id = generate_uid()
        record = Metadata(
            id=_generated_id,
            canonical_url=src,
            view_count=0,
            like_count=0,
            dislike_count=0,
            created_dt=func.now(),
            updated_at=func.now())
        db.session.add(record)


def download_media(src, uid):
    response = requests.get(src, headers=headers)
    handler = None
    if response.status_code == 200:
        soup = BeautifulSoup(response.text)
        if soup:
            if "reddit.com" in src:
                handler = Reddit(soup, src, uid)
        else:
            print("Unable to parse ", src)
    if handler:
        handler.process_soup()
