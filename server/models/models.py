from sqlalchemy.sql import func
import sys
from os.path import dirname
from flask_sqlalchemy import SQLAlchemy

sys.path.append(dirname(__file__).split("/server")[0])

db = SQLAlchemy()


class Metadata(db.Model):
    __tablename__ = 'metadata'
    __bind_key__ = 'primary'

    id = db.Column(db.String, primary_key=True)
    invalid_url = db.Column(db.Boolean)
    processed = db.Column(db.Boolean)
    encoding = db.Column(db.String)
    media_type = db.Column(db.String)
    title = db.Column(db.String)
    description = db.Column(db.String)
    category = db.Column(db.String)
    canonical_url = db.Column(db.String, unique=True)
    view_count = db.Column(db.BigInteger)
    share_count = db.Column(db.BigInteger)
    like_count = db.Column(db.BigInteger)
    dislike_count = db.Column(db.BigInteger)
    created_dt = db.Column(db.DateTime, default=func.now())
    updated_at = db.Column(db.DateTime, default=func.now())

    def __repr__(self):
        return '<Metadata %r>' % self.id
