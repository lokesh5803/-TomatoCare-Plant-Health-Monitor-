TomatoCare: Plant Health Monitor
TomatoCare is an AI-powered mobile application for automated detection of tomato plant diseases to support efficient and sustainable agriculture. Developed as part of the Mobile Application Development course at Amrita School of Computing, this app leverages deep learning models for disease identification and assessment, aiding farmers and agricultural specialists in managing plant health and optimizing yield.

Table of Contents
Features
Demo
Dataset
Model Architecture
Installation
Usage
Results
References
Features
Disease Detection: Detects multiple tomato leaf diseases through an easy-to-use interface.
Severity Assessment: Assesses disease severity to help guide treatment decisions.
Treatment Progress Monitoring: Tracks and monitors treatment progress over time.
Accessible and Intuitive Design: Designed for ease of use in the agricultural field with a focus on Accessibility & Assistive Technology.


Dataset
TomatoCare uses the Tomato Leaf Diseases dataset from Kaggle for training and testing models. This dataset includes images for various tomato diseases, allowing for robust and accurate model training. Dataset Link
https://www.kaggle.com/datasets/kaustubhb999/tomatoleaf

Model Architecture
This project explores multiple deep learning architectures:
MobileNetV2: Provides lightweight and efficient disease detection, achieving ~90% accuracy.
EfficientNet: Strikes a balance between model complexity and accuracy.
DETR and Swin Transformer: Advanced architectures for improved feature extraction and classification.
Vision Transformers (ViTs): Enables high-level feature extraction for disease differentiation.
For an overview of the system architecture, refer to our System Architecture Diagram.


Load the Model: Download the pre-trained model_unquant.tflite file and place it in the assets folder for direct access by the app.

Usage
Open the App: Launch TomatoCare on your mobile device.
Image Capture or Upload: Use the camera to capture or upload an image of a tomato leaf.
Disease Detection: Run the analysis, and the app will display the detected disease along with severity details.
Treatment Progress: Monitor disease management progress over time for comprehensive tracking.

Results
Testing on the Tomato Leaf Diseases dataset showed the following model accuracies:
MobileNetV2: ~90%
EfficientNet: ~85%
DETR & Swin Transformer: 75-85%
