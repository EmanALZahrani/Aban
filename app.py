from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os

app = Flask(__name__)

# Load the trained Logistic Regression model
model_filename = 'Log_Reg_model.pkl'
log_reg = joblib.load(model_filename)

def contains_sound(audio, threshold=0.05):
    energy = np.sum(audio ** 2)
    return energy > threshold


def features_extractor(audio, sample_rate):
    # Check if the audio contains sound
    if not contains_sound(audio):
        return None, 'الملف الصوتي المقدم صامت أو الصوت غير مسموع'  # Changed return type
        
    # MFCC
    mfccs = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)

    return mfccs, None  # Changed return type

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Check if the post request has the file part
        if 'audio' not in request.files:
            return jsonify({'error': 'No file part in the request'}), 400  # Bad request error

        audio_file = request.files['audio']

        # Save the uploaded file temporarily
        path_to_write = "/tmp/" + audio_file.filename
        audio_file.save(path_to_write)

        # Use pydub to convert the audio file to .wav format
        audio = AudioSegment.from_file(path_to_write, format="m4a")
        converted_file_path = "/tmp/converted_" + os.path.splitext(audio_file.filename)[0] + ".wav"
        audio.export(converted_file_path, format="wav")

        # Load the audio file with Librosa
        audio_data, sample_rate = librosa.load(converted_file_path, sr=None)

        # Check if the audio file contains sound
        if not contains_sound(audio_data):
            return jsonify({'error': 'الملف الصوتي المقدم صامت أو الصوت غير مسموع'})

        # Extract features from the audio file
        extracted_features, error = features_extractor(audio_data, sample_rate)
        if error:
            return jsonify({'error': error}), 400

        # Reshape features for the model prediction
        features_reshaped = extracted_features.reshape(1, -1)

        # Make predictions using the loaded model
        probabilities = log_reg.predict_proba(features_reshaped)
        normal_prob ,stutter_prob  = probabilities[i]

        # Return the prediction results as JSON
        return jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"}), 200

    except Exception as e:
        # For debugging purposes, consider printing the exception stack trace or logging it here.
        print(e)  # Or log the exception
        return jsonify({'error': 'An error occurred while processing. Please try again.'}), 500

if __name__ == '__main__':
    app.run(debug=True) 

