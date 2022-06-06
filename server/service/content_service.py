import sys
from os.path import dirname
import random
import requests
from bs4 import BeautifulSoup
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import datetime

sys.path.append(dirname(__file__).split("/server")[0])

from server.service.parsers import Reddit
from server.models.models import Metadata
from server.utils import utils
from server.props.aws import  AWSProps

headers = {'User-Agent': utils.USER_AGENT}


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
    with app.app_context():
        query = db.session.query(Metadata).filter(Metadata.canonical_url.in_(tuple([src])))
        records = query.all()
        if records:
            return records[0].id
        _generated_id = generate_uid()
        record = Metadata(
            id=_generated_id,
            processed=False,
            canonical_url=src,
            view_count=0,
            like_count=0,
            dislike_count=0,
            created_dt=datetime.datetime.now(),
            updated_at=datetime.datetime.now())
        db.session.add(record)
        utils.save(db)
        return _generated_id


def download_media(src, uid, app, db):
    # TODO use content metadata to check if it should be downloaded again or not
    response = requests.get(src, headers=headers)
    handler = None
    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'lxml')
        if soup:
            if "reddit.com" in src:
                handler = Reddit(soup, src, uid, db, app)
        else:
            print("Unable to parse ", src)
    if handler:
        handler.process_soup()


def get_metadata(uid: str, app: Flask, db: SQLAlchemy) -> {}:
    with app.app_context():
        query = db.session.query(Metadata).filter(Metadata.id.in_(tuple([uid])))
        records = query.all()
        if records:
            if records[0].processed:
                return {
                    "url": f'{AWSProps().cdn}/{records[0].id}.mp4',
                    "thumbnail": f'{AWSProps().cdn}/{records[0].id}.jpeg',
                    "media_type": records[0].media_type,
                    "processed": records[0].processed,
                    "encoding": records[0].encoding,
                    "title": records[0].title,
                    "description": records[0].description,
                    "category": records[0].category,
                    "view_count": records[0].view_count,
                    "like_count": records[0].like_count,
                    "dislike_count": records[0].dislike_count,
                    "created_dt": records[0].created_dt
                }
            else:
                return {
                    "invalid_url": records[0].invalid_url,
                }
        return {
            "error": True
        }
