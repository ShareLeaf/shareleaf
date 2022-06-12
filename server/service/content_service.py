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


def get_content_id(src: str, app: Flask, db: SQLAlchemy):
    with app.app_context():
        query = db.session.query(Metadata).filter(Metadata.canonical_url.in_(tuple([src])))
        records = query.all()
        if records:
            return records[0]


def generate_content_id(src: str, app: Flask, db: SQLAlchemy) -> str:
    """Return content id if one exists or generate a unique one otherwise"""
    record = get_content_id(src, app, db)
    if record:
        return record.id

    # Generate a new id since the content wasn't previously downloaded
    _generated_id = generate_uid()
    record = Metadata(
        id=_generated_id,
        processed=False,
        canonical_url=src,
        view_count=0,
        share_count=0,
        like_count=0,
        dislike_count=0,
        created_dt=datetime.datetime.now(),
        updated_at=datetime.datetime.now())
    db.session.add(record)
    utils.save(db)
    return _generated_id


def download_media(src, uid, app, db):
    record = get_content_id(src, app, db)
    if not record.processed:
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


def update_share_count(uid: str, app: Flask, db: SQLAlchemy) -> {}:
    with app.app_context():
        query = db.session.query(Metadata).filter(Metadata.id.in_(tuple([uid])))
        records = query.all()
        if records:
            if records[0].processed:
                share_count = records[0].share_count + 1
                records_to_update = [{
                    "id": uid,
                    "share_count": share_count,
                    'updated_at': datetime.datetime.now()
                }]
                db.session.bulk_update_mappings(Metadata, records_to_update)
                utils.save(db)
                return {"status": "ok"}
        return {
            "error": True
        }


def get_metadata(uid: str, app: Flask, db: SQLAlchemy) -> {}:
    with app.app_context():
        query = db.session.query(Metadata).filter(Metadata.id.in_(tuple([uid])))
        records = query.all()
        if records:
            if records[0].processed:
                url = f'{AWSProps().cdn}/{records[0].id}.mp4',
                if records[0].media_type == "image":
                    url = f'{AWSProps().cdn}/{records[0].id}.jpg',
                view_count = records[0].view_count
                if view_count == 0:
                    view_count = 1
                response = {
                    "url": url,
                    "shareable_link": "https://shareleaf.co/" + uid,
                    "thumbnail": f'{AWSProps().cdn}/{records[0].id}.jpg',
                    "media_type": records[0].media_type,
                    "processed": records[0].processed,
                    "encoding": records[0].encoding,
                    "title": records[0].title,
                    "description": records[0].description,
                    "category": records[0].category,
                    "view_count": view_count,
                    "like_count": records[0].like_count,
                    "share_count": records[0].share_count,
                    "dislike_count": records[0].dislike_count,
                    "created_dt": records[0].created_dt
                }
                records_to_update = [{
                    "id": uid,
                    "view_count": records[0].view_count + 1,
                    'updated_at': datetime.datetime.now()
                }]
                db.session.bulk_update_mappings(Metadata, records_to_update)
                utils.save(db)
                return response
            else:
                return {
                    "invalid_url": records[0].invalid_url,
                }
        return {
            "error": True
        }
