from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os

app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello, World!'

# Load the trained Logistic Regression model
model_filename = 'Reg_model.pkl'
log_reg = joblib.load(model_filename)

def contains_sound(audio, threshold=0.05):
    energy = np.sum(audio ** 2)
    return energy > threshold

def reduce_noise(audio):
    n = 2
    B, A = butter(n, 0.05, output='ba')
    audio = lfilter(B, A, audio)
    return audio

def features_extractor(audio, sample_rate):
    # Check if the audio contains sound
    if not contains_sound(audio):
        return jsonify({'error': 'التسجيل لا يحتوي على صوت، حاول مرة أخرى'}), 400

    # Noise Reduction
    audio = reduce_noise(audio)

    # MFCC
    mfccs = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)

    return mfccs

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
            return jsonify({'error': 'الملف الصوتي المقدم صامت أو الصوت غير مسموع'}), 400

        # Extract features from the audio file
        extracted_features = features_extractor(audio_data, sample_rate)

        # Reshape features for the model prediction
        features_reshaped = extracted_features.reshape(1, -1)

        # Make predictions using the loaded model
        probabilities = log_reg.predict_proba(features_reshaped)
        stutter_prob = probabilities[0][0]
        normal_prob= probabilities[0][1]

        # Return the prediction results as JSON
        return jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"}), 200

    except Exception as e:
        # Generic exception handling, consider specifying possible exceptions for better debugging
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)



