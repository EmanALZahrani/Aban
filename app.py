from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np
import requests  # Import the requests library to download files

app = Flask(__name__)

# Load the trained Logistic Regression model
model_filename = 'Rmodel.pkl'
log_reg = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    # existing feature extraction logic...
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return mfccs_scaled_features

def contains_sound(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    return energy > threshold

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Parse the JSON for the URL
        data = request.get_json()
        audio_url = data.get('audioUrl')  # This is the new part, getting a URL instead of a file.

        if not audio_url:
            return jsonify({"error": "No audio URL provided"})

        # Download the audio file from the URL
        response = requests.get(audio_url)
        if response.status_code != 200:
            return jsonify({"error": f"Failed to download audio file, status code: {response.status_code}"})

        # You now have the audio data in `response.content`. 
        # Depending on your audio file format, the following part might vary:
        # You need to save it temporarily before processing, or you can use an in-memory buffer.

        audio_path = 'temp_audio_file.wav'  # Adjust the extension if needed
        with open(audio_path, 'wb') as audio_file:
            audio_file.write(response.content)

        # Load and process the audio file
        audio, sample_rate = librosa.load(audio_path, sr=None)  # or another appropriate function depending on the format

        if not contains_sound(audio):
            return jsonify({"error": "The audio file is too silent. Please try again."})

        # Extract features and predict
        features = features_extractor(audio, sample_rate)
        features = features.reshape(1, -1)
        probabilities = log_reg.predict_proba(features)

        # Assuming class 0 is "Normal" and class 1 is "Stutter"
        normal_prob = probabilities[0][0]
        stutter_prob = probabilities[0][1]

        return jsonify({"Normal": f"{normal_prob * 100:.2f}%", "Stutter": f"{stutter_prob * 100:.2f}%"})

    except Exception as e:
        return jsonify({"error": f"Something went wrong: {str(e)}"})

if __name__ == '__main__':
    app.run(debug=True)

