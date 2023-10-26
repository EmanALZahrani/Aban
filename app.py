from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os
from scipy.signal import butter, lfilter
import pandas as pd


app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello, World!'

# Load the trained Logistic Regression model
model_filename = 'RegressionModel.pkl'
scaler_filename = 'scaler.pkl'
log_reg = joblib.load(model_filename)
scaler = joblib.load(scaler_filename)

def reduce_noise(audio):
    n = 2
    B, A = butter(n, 0.05, output='ba')
    audio = lfilter(B, A, audio)
    return audio

def contains_sound(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    return energy > threshold

def features_extractor(audio, sample_rate):
    mfccs_features = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13)
    mfccs_scaled_features = np.mean(mfccs_features.T, axis=0)
    zero_crossing_rate = np.mean(librosa.feature.zero_crossing_rate(y=audio))
    extracted_features = np.append(mfccs_scaled_features, zero_crossing_rate)
    return pd.Series(extracted_features)


@app.route('/predict', methods=['POST'])
def predict():

    try:
        audio_file = request.files['audio']

        # Check if the post request has the file part
        if audio_file is None:
            return jsonify({'error': 'No file part in the request'})
            
        # Save the uploaded file temporarily
        path_to_write = "/tmp/" + audio_file.filename
        audio_file.save(path_to_write)

# Define the path for the converted file
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"

# Use pydub to convert the audio file to .wav format
        bit_depth = 16
        sample_rate = 44100  # or the sample rate used during model training
        channels = 1  # mono; use 2 for stereo if that's what your model was trained with

        audio = AudioSegment.from_file(path_to_write, format="m4a")
        audio = audio.set_frame_rate(sample_rate).set_channels(channels).set_sample_width(bit_depth)
        audio.export(converted_file_path, format="wav")

# Load the converted audio file for feature extraction
        audio_data, sample_rate = librosa.load(converted_file_path, sr=None)
        audio_data = reduce_noise(audio_data)a)

        if not contains_sound(audio_data):
            os.remove(path_to_write)  # Cleanup
            os.remove(converted_file_path)
            return jsonify({'error': 'الملف الصوتي صامت أو غير مسموع'})

        # Extract features and scale them
        features = features_extractor(audio_data, sample_rate)
        features = features.values.reshape(1, -1)  
        features_scaled = scaler.transform(features)

        # Predict using the loaded model
        probabilities = log_reg.predict_proba(features_scaled)
        stutter_prob = probabilities[0][1]
        normal_prob = probabilities[0][0]

        # Cleanup the temporary files
        os.remove(path_to_write)
        os.remove(converted_file_path)

        # Return prediction results
        return jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"}), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500

      
if __name__ == '__main__':
    app.run(debug=True)
