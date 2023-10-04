from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np

app = Flask(__name__)

# Load the trained SVM model
model_filename = 'svm_classifier_model.pkl'
svm_classifier = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return mfccs_scaled_features

def contains_sound(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    return energy > threshold

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Get the uploaded audio file from the request
        audio_file = request.files['audio']
        audio, sample_rate = librosa.load(audio_file, sr=None)

        if not contains_sound(audio):
            return jsonify({"error": "Audio does not contain sound."})

        # Extract features from the audio
        features = features_extractor(audio, sample_rate)

        # Reshape features for prediction
        features = features.reshape(1, -1)

        # Make predictions using the SVM classifier
        prediction = svm_classifier.predict(features)

        return jsonify({"prediction": int(prediction[0])})
    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
