from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os
from scipy.signal import butter, lfilter
import pandas as pd

app = Flask(__name__)

# Constants for audio processing (should match your training setup)
SAMPLE_RATE = 16000  # Example: 16000
CHANNELS = 1  # Mono
BIT_RATE = "256k"  # Adjust based on your preference or training setup

# Load the trained Logistic Regression model and scaler
model_filename = 'RegressionModel.pkl'
scaler_filename = 'scaler.pkl'
log_reg = joblib.load(model_filename)
scaler = joblib.load(scaler_filename)

@app.route('/')
def index():
    return 'Hello, World!'

def reduce_noise(audio):
    n = 2
    B, A = butter(n, 0.05, output='ba')
    audio = lfilter(B, A, audio)
    return audio

def features_extractor(audio, sample_rate):
    mfccs_features = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13)
    mfccs_scaled_features = np.mean(mfccs_features.T, axis=0)
    zero_crossing_rate = np.mean(librosa.feature.zero_crossing_rate(y=audio))
    extracted_features = np.append(mfccs_scaled_features, zero_crossing_rate)
    return pd.Series(extracted_features)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Check if an audio file part is in the request
        if 'audio' not in request.files:
            return jsonify({'error': 'No file part in the request'}), 400

        audio_file = request.files['audio']
        if audio_file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        # Save the uploaded file temporarily
        path_to_write = "/tmp/" + audio_file.filename
        audio_file.save(path_to_write)

        # Convert the audio file to .wav format and adjust parameters
        audio = AudioSegment.from_file(path_to_write, format="m4a")
        audio = audio.set_frame_rate(SAMPLE_RATE).set_channels(CHANNELS)
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"
        audio.export(converted_file_path, format="wav", bitrate=BIT_RATE)

        # Load and process the audio data with Librosa
        audio_data, sample_rate = librosa.load(converted_file_path, sr=SAMPLE_RATE)
        audio_data = reduce_noise(audio_data)

        # Extract features, scale them and predict using the loaded model
        features = features_extractor(audio_data, sample_rate)
        features = features.values.reshape(1, -1)
        features_scaled = scaler.transform(features)
        probabilities = log_reg.predict_proba(features_scaled)
        stutter_prob = probabilities[0][1]
        normal_prob = probabilities[0][0]

        # Cleanup temporary files
        if os.path.exists(path_to_write):
            os.remove(path_to_write)
        if os.path.exists(converted_file_path):
            os.remove(converted_file_path)

        return jsonify({
            "Stutter": f"{stutter_prob * 100:.2f}%",
            "Normal": f"{normal_prob * 100:.2f}%"
        }), 200

    except Exception as e:
        # In case of unexpected error, log it and return an error message
        print(f"An error occurred: {e}")
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)

