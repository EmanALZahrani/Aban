from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np
import os
import pandas as pd
import subprocess  # Import the subprocess module

app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello, World!'

# Load the trained Logistic Regression model
model_filename = 'R_Model.pkl'
log_reg = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return pd.Series(mfccs_scaled_features)



@app.route('/predict', methods=['POST'])
def predict():
    try:
        audio_file = request.files['audio']

        # Check if the post request has the file part
        if audio_file is None:
            return jsonify({'error': 'No file part in the request'})

        # Save the uploaded file temporarily, ensure it's saved as .3gp
        path_to_write = "/tmp/" + os.path.splitext(audio_file.filename)[0] + ".3gp"
        audio_file.save(path_to_write)

        # Define the path for the converted file
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"

        # Convert the 3gp audio file to WAV format using ffmpeg
        subprocess.run(["ffmpeg", "-i", path_to_write, converted_file_path], check=True)

        # Load the audio data from the converted file
        audio_data, sample_rate = librosa.load(converted_file_path, sr=None)

        # Extract features and scale them
        features = features_extractor(audio_data, sample_rate)
        features = features.values.reshape(1, -1)
        

        # Predict using the loaded model
        probabilities = log_reg.predict_proba(features)
        stutter_prob = probabilities[0][1]
        normal_prob = probabilities[0][0]

        # Cleanup the temporary files
        os.remove(path_to_write)
        os.remove(converted_file_path)

        # Return prediction results
        return jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"}), 200

    except subprocess.CalledProcessError as e:
        # Handle exceptions that occur during the conversion process
        return jsonify({'error': 'Error occurred in converting audio file'}), 500
    except Exception as e:
        # Generic exception handling, consider specifying for better error handling
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)

