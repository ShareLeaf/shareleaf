from flask import Flask, request, jsonify
from flask_cors import CORS
from service import content_service as cs

app = Flask(__name__)
CORS(app)

@app.route("/generate-link", methods = ['POST'])
def generate_link():
    data = request.get_json()
    print("Data received: ", data)
    return jsonify(
        url="http://localhost:5000/{}".format(cs.generate_uid()),
        status=200
    )

@app.route("/", methods = ['POST'])
def home():
    return jsonify(
        message="success",
        status=200
    )


if __name__ == "__main__":
    app.run(host='0.0.0.0')