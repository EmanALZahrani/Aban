from flask import Flask, request, jsonify
import joblib
import librosa
import numpy as np

app = Flask(__name__)

# Load the trained SVM model
model_filename = 'classifier.pkl'
svm_classifier = joblib.load(model_filename)

def features_extractor(audio, sample_rate):
    mfccs_scaled_features = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=13).T, axis=0)
    return mfccs_scaled_features

def contains_sound(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    return energy > threshold

def remove_noise(audio, threshold=0.02):
    energy = np.sum(audio ** 2)
    if energy > threshold:
        return audio
    else:
        return np.zeros_like(audio)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Get the uploaded audio file from the request
        audio_file = request.files['audio']
        audio, sample_rate = librosa.load(audio_file, sr=None)

        if not contains_sound(audio):
            return jsonify({"error": "لا يوجد صوت سجل مره أخرى"})
            
        cleaned_audio = remove_noise(audio)

        # Extract features from the audio
        features = features_extractor(cleaned_audio, sample_rate)

        # Reshape features for prediction
        features = features.reshape(1, -1)

        # Make predictions using the SVM classifier
        prediction = classifier.predict(features)


        return jsonify({"الحالة": int(prediction[0])})
    except Exception as e:
        return jsonify({"حدث خطأ": str(e)})

@app.route('/test', methods=['GET'])
def test_endpoint():
    return "Server is up and running!"

if __name__ == '__main__':
    app.run(debug=True)
