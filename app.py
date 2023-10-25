from flask import Flask, request, jsonify
from pydub import AudioSegment
import joblib
import librosa
import numpy as np
import os
from scipy.signal import butter, lfilter


app = Flask(__name__)

@app.route('/')
def index():
    return 'Hello, World!'

# Load the trained Logistic Regression model
model_filename = 'Regression.pkl'
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
  audio = reduce_noise(audio)

    # MFCCs and Zero Crossing Rate
    mfccs_features = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13)
    mfccs_scaled_features = np.mean(mfccs_features.T, axis=0)
    zero_crossing_rate = np.mean(librosa.feature.zero_crossing_rate(y=audio))
    extracted_features = np.append(mfccs_scaled_features, zero_crossing_rate)

    return extracted_features


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

        # Load the audio file with Librosa
        audio_data, sample_rate = librosa.load(converted_file_path, sr=None)

        extracted_features = features_extractor(audio_data, sample_rate)
        if not contains_sound(audio_data):
            return jsonify({'error': 'الملف الصوتي المقدم صامت أو الصوت غير مسموع'})

        # Reshape and scale the features
        features_reshaped = extracted_features.reshape(1, -1)
        

        # Predict using the loaded model
        probabilities = log_reg.predict_proba(features_reshaped)
        stutter_prob = probabilities[0][1]
        normal_prob = probabilities[0][0]

        # Return the prediction results as JSON
        return jsonify({"Stutter": f"{stutter_prob * 100:.2f}%", "Normal": f"{normal_prob * 100:.2f}%"}), 200

    except Exception as e:
        # Generic exception handling, consider specifying possible exceptions for better debugging
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
