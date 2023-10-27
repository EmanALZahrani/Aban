from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os
import pandas as pd
from werkzeug.utils import secure_filename


app = Flask(__name__)



# Constants for allowed extensions and model files
ALLOWED_EXTENSIONS = {'mp3', 'm4a', 'wav', 'flac'}
MODEL_FILENAME = 'Regression_Model111.pkl'
SCALER_FILENAME = 'scaler111.pkl'

# Load the trained model and scaler
log_reg = joblib.load(MODEL_FILENAME)
scaler = joblib.load(SCALER_FILENAME)


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def features_extractor(file_path):
    audio, sample_rate = librosa.load(file_path, sr=None)
    mfccs_features = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13)
    mfccs_scaled_features = np.mean(mfccs_features.T, axis=0)

    return pd.Series(mfccs_scaled_features)


@app.route('/predict', methods=['POST'])
def predict():
   file = request.files.get('audio')  # Using .get is safer, it won't throw an exception if 'audio' doesn't exist.

    if file is None:
        return jsonify({'error': 'No audio file part'})

    if not allowed_file(file.filename):
        return jsonify({'error': 'Format not supported'})

    filename_secure = secure_filename(file.filename)
    path_to_write = os.path.join("/tmp", filename_secure)
    converted_file_path = ""

    try:
        # Save file
        file.save(path_to_write)

        # Convert audio file
        audio = AudioSegment.from_file(path_to_write)
        converted_file_path = "/tmp/converted_" + os.path.splitext(filename_secure)[0] + ".wav"
        audio.export(converted_file_path, format="wav")

        # Extract features
        features = features_extractor(converted_file_path).values.reshape(1, -1)
        features_scaled = scaler.transform(features)

        # Make prediction
        probabilities = log_reg.predict_proba(features_scaled)
        stutter_prob = probabilities[0][1]
        normal_prob = probabilities[0][0]

        # Create response
        response = jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"})
        response.content_type = "application/json"
        return response, 200

    except Exception as e:
        logger.error(f"An error occurred: {str(e)}")
        return jsonify({'error': str(e)}), 500

    finally:
        # Clean up
        if os.path.exists(path_to_write):
            os.remove(path_to_write)
        if os.path.exists(converted_file_path):
            os.remove(converted_file_path)


if __name__ == '__main__':
    app.run(debug=True)


