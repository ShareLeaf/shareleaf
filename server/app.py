from flask import Flask, request, jsonify
from flask_cors import CORS
from service import content_service as cs

app = Flask(__name__)
CORS(app)

@app.route("/generate-link", methods = ['POST'])
def generate_link():
    data = request.get_json()
    if data and data.get("raw_url"):
        print("Map this to the database: ", data.get("raw_url"))
    uid = cs.generate_uid()
    return jsonify(
        url="http://localhost:5000/{}".format(uid),
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