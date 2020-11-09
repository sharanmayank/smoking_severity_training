# Instructions to setup and run

For Running the test

1. Please ensure Java 8 is installed
2. Files can be found in run_test
3. Connect the eye tribe eye tracker and initialize and calibrate it
4. The test can be started by running the file run_test/src/Test.java

For Random Forest, SVR and Ridge

1. Please use python 3.6
2. After downloading the code run pip install -r requirements.txt
3. The code to fit individual models can be found in train_models directory

For Lasso

1. Open the code in MATLAB R2020a
2. The code to fit lasso model can be found in train_lasso directory in the file run_lasso.m


For Generating features

1. Files can be found in extract_features
2. task\<i\>analysis.py contains the code to generate the features for task i
3. The raw data is available in data folder that can be used to extract more features

For Visualising task data

1. Code can be found in extract_features
2. task\<i\>vis.py contains the code to generate an animation representing the task for a given subject
