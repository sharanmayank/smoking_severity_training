# Instructions to setup and run

For Training Classifiers and the Regression presented in the paper

1. Please use python 3.6
2. After downloading the code run pip install -r requirements.txt
3. For training classifiers use final_analysis/train_classifier.py
4. For training regression on the task 2 feature of num switches use final_analysis/train_task_2_feat_reg.py

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

1. Please use python 3.6
2. After downloading the code run pip install -r requirements.txt
3. Files can be found in extract_features
4. task\<i\>analysis.py contains the code to generate the features for task i
5. The raw data is available in data folder that can be used to extract more features

For Visualising task data

1. Please use python 3.6
2. After downloading the code run pip install -r requirements.txt
3. In case tkinter is not installed please install using sudo apt-get install python3-tk
4. Code can be found in extract_features
5. task\<i\>vis.py contains the code to generate an animation representing the task for a given subject
