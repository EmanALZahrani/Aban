from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np

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

def contains_sound(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    return energy > threshold


@app.route('/predict', methods=['POST'])
def predict():
        # Get the uploaded audio file from the request
        audio_file = request.files['audio']
        audio, sample_rate = librosa.load(audio_file, sr=None)


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
