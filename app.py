from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np
import os
import subprocess

app = Flask(__name__)

# Load the trained Logistic Regression model
model_filename = 'Rmodel.pkl'
log_reg = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return mfccs_scaled_features

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file part in the request'}), 400

    audio_file = request.files['file']

    if audio_file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    # Define a path for the uploaded file
    path_to_write = "/tmp/" + audio_file.filename
    audio_file.save(path_to_write)

    # Define a path for the converted file
    converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"

    # Convert the audio file to WAV using ffmpeg
    subprocess.run(["ffmpeg", "-i", path_to_write, converted_file_path])

    # Now, load the converted file with librosa
    audio, sample_rate = librosa.load(converted_file_path, sr=None)

    # Validate if audio is proper
    # You can add more validations here based on your requirements
    if audio is None or len(audio) == 0:
        return jsonify({'error': 'Invalid audio file'}), 400

    # Extract features from the audio
    features = features_extractor(audio, sample_rate)

    # Reshape features for prediction
    features = features.reshape(1, -1)

    # Make predictions using the Logistic Regression model
    probabilities = log_reg.predict_proba(features)

    # Assuming class 0 is "Normal" and class 1 is "Stutter"
    normal_prob = probabilities[0][0]
    stutter_prob = probabilities[0][1]

    return jsonify({"Normal": f"{normal_prob * 100:.2f}%", "Stutter": f"{stutter_prob * 100:.2f}%"})

if __name__ == '__main__':
    app.run(debug=True)

