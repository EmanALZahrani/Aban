from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os
from scipy.signal import butter, lfilter
import pandas as pd

app = Flask(__name__)

# Load the trained Logistic Regression model
model_filename = 'Regression_Model111.pkl'
scaler_filename = 'scaler111.pkl'
log_reg = joblib.load(model_filename)
scaler = joblib.load(scaler_filename)



def features_extractor(audio, sample_rate):
    mfccs_features = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13)
    mfccs_scaled_features = np.mean(mfccs_features.T, axis=0)
    
    # removed the zero_crossing_rate and its appending to the extracted features.
    return pd.Series(mfccs_scaled_features)


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

        # Use pydub to convert the audio file to .wav format
        audio = AudioSegment.from_file(path_to_write, format="m4a")
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"
        audio.export(converted_file_path, format="wav")

        audio_data, sample_rate = librosa.load(converted_file_path, sr=None)
        
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


