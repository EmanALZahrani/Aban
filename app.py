from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np
import subprocess
import os

app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello, World!'

# Load the trained Logistic Regression model
model_filename = 'Rmodel.pkl'
log_reg = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return mfccs_scaled_features

@app.route('/predict', methods=['POST'])
def predict():
    # Check if the post request has the file part
    if 'audio' not in request.files:
        return jsonify({'error': 'No file part in the request'}), 400

    audio_file = request.files['audio']

    # If the user does not select a file, the browser may submit an empty part without a filename
    if audio_file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    try:
        # Save the uploaded file
        path_to_write = "/tmp/" + audio_file.filename
        audio_file.save(path_to_write)

        # Define a path for the converted file
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"

        # Convert the audio file to WAV format using ffmpeg
        subprocess.run(["ffmpeg", "-i", path_to_write, converted_file_path], check=True)

        # Load the converted audio file with Librosa
        audio, sample_rate = librosa.load(converted_file_path, sr=None)

        # Extract features from the audio file
        features = features_extractor(audio, sample_rate)

        # Reshape features for the model prediction
        features_reshaped = features.reshape(1, -1)

        # Make predictions using the loaded model
        probabilities = log_reg.predict_proba(features_reshaped)
        normal_prob = probabilities[0][0]
        stutter_prob = probabilities[0][1]

        # Return the prediction results as JSON
        return jsonify({"Normal": f"{normal_prob * 100:.2f}%", "Stutter": f"{stutter_prob * 100:.2f}%"})

    except subprocess.CalledProcessError as e:
        return jsonify({'error': 'Error occurred in converting audio file'}), 500
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)


