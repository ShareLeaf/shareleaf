import sys
from os.path import dirname
from flask import Flask, request, jsonify
from flask_cors import CORS
import validators
sys.path.append(dirname(__file__).split("/python")[0])

from service import content_service as cs

app = Flask(__name__)
CORS(app)

# THREAD_COUNT = 10


@app.route("/process-url", methods = ['POST'])
def generate_link():
    # uid = cs.generate_uid()
    data = request.get_json()
    src = data.get("src")
    uid = data.get("uid")
    if src and uid and validators.url(src):
        src = data.get("src")
        uid = data.get("uid")
        print("Map this to the database: ", src)
        print("Attempting to process ", src)
        cs.download_media(src, uid)
    return jsonify(status=200)

@app.route("/metadata", methods = ['GET'])
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


if __name__ == "__main__":
    app.run(host='0.0.0.0')