from flask import Flask, jsonify, request
import validators
import requests
from flask_cors import CORS
import datetime
import sys
from os.path import dirname

sys.path.append(dirname(__file__).split("/server")[0])

from server.service import content_service as cs
from server.models.models import db
from server.migrations import migrate
from server.props.aws import AWSProps
from server.props.datasource import DatasourceProps


def configs() -> {}:
    parsed_configs = {
        "datasource": DatasourceProps(),
        "aws": AWSProps()
    }
    return parsed_configs


configurations = configs()

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": [
    "*"
]}})
app.config['SQLALCHEMY_DATABASES_URI'] = configurations.get("datasource").get_full_url()
app.config['SQLALCHEMY_BINDS'] = {
    configurations.get("datasource").bind_key: configurations.get("datasource").get_full_url()
}
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db.init_app(app)

# Perform schema migration
migrate(app, configurations.get("datasource"))


@app.route("/generate-content-id", methods=['POST'])
def generate_content_id():
    data = request.get_json()
    src = data.get("src")
    return jsonify({
        "uid": cs.generate_content_id(src, app, db)
    }), 200


@app.route("/process-url", methods=['POST'])
def generate_link():
    # uid = cs.generate_uid()
    data = request.get_json()
    src = data.get("src")
    uid = data.get("uid")
    if src and uid and validators.url(src):
        src = data.get("src")
        uid = data.get("uid")
        print("Map this to the database: ", src)
        print("Attempting to process ", src, uid)
        cs.download_media(src, uid)
    else:
        print("Unable to process url ", src)
    return jsonify(status=200)


@app.route("/metadata", methods=['GET'])
def get_metadata():
    key = request.args.get("key")
    if key:
        return jsonify(
            encoding="video/webm",
            cdn="http://media.w3.org",
            caption="Hello, World! Complete with reusable components, all pages and sections are available in the Figma ecosystem.",
            type="video",
            status=200
        )
    return jsonify(
        error="Key not found",
        status=200
    )


@app.route('/', defaults={'u_path': ''})
@app.route('/<path:u_path>')
def get_content(u_path):
    return jsonify(
        message=u_path,
        status=200
    )


@app.route('/health', methods=["GET"])
def health():
    now = datetime.datetime.now()
    response = {
        "status": "ShareLeaf is UP",
        "time": now.strftime('%Y-%m-%dT%H:%M:%S') + ('.%03d' % (now.microsecond / 10000))
    }
    return jsonify(response), 200


def create_app():
    return app


if __name__ == "__main__":
    app = create_app()
    app.run(host='0.0.0.0', port=configurations.get("server").port)
