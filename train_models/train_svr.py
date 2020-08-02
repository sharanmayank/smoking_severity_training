"""
Train a support vector regression model on the data.
"""

# Import libraries needed

import os
import pickle
import numpy as np
import pandas as pd

from sklearn.svm import SVR

# Import functions from the project

from train_models.utils import fetch_data
from train_models.utils import compute_metrics

from train_models.constants import PathConstants


def train_svr(task_name, save_models_bool, save_results_bool):

    # Create the models directory if does not exist

    models_dir_path = PathConstants.svr_models_dir + task_name + '/'

    if save_models_bool and not(os.path.exists(models_dir_path)):
        os.makedirs(models_dir_path)

    # Fetch the data for the given task

    id_arr, feature_val_arr, ftnd_arr = fetch_data(task_name=task_name)

    # Normalize feature value array

    feature_val_arr = np.divide(feature_val_arr - np.mean(feature_val_arr, axis=0), np.std(feature_val_arr, axis=0))

    # Initialize variables to be used for cross validation training

    num_examples = feature_val_arr.shape[0]
    pred_ftnd = np.full_like(ftnd_arr, fill_value=np.nan, dtype=float)

    # Train with leave one out cross validation

    for te_ex_idx in range(num_examples):

        # Separate out the training and test data for the fold

        te_ex_bool = np.full_like(ftnd_arr, fill_value=False, dtype=bool)
        te_ex_bool[te_ex_idx] = True

        train_x = feature_val_arr[~te_ex_bool, :]
        train_y = ftnd_arr[~te_ex_bool]

        test_x = feature_val_arr[te_ex_bool, :]
        test_y = ftnd_arr[te_ex_bool]

        # Initialize and fit the model on the training data

        model = SVR()
        model.fit(train_x, train_y)

        # If save models bool is true save the model at appropriate location

        if save_models_bool:
            model_file_path = models_dir_path + 'leave_' + str(te_ex_idx) + '_out.pb'
            pickle.dump(model, open(model_file_path, 'wb'), protocol=pickle.HIGHEST_PROTOCOL)

        # Predict the value for the test example

        pred_y = model.predict(test_x)
        pred_ftnd[te_ex_idx] = pred_y

        print(te_ex_idx, test_y, pred_y)

    # Calculate metrics

    mae, mse, nmae, nmse, r2_sc = compute_metrics(pred_ftnd, ftnd_arr)

    print('Metrics: SVR', task_name)
    print('Mean Absolute Error:', mae)
    print('Mean Squared Error:', mse)
    print('Normalized Mean Absolute Error:', nmae)
    print('Normalized Mean Squared Error:', nmse)
    print('R2 score:', r2_sc)

    metrics_arr = np.array([mae, mse, nmae, nmse, r2_sc])
    print(','.join(metrics_arr.astype(str)))

    if save_results_bool:
        column_headings = ['ID', 'Actual FTND', 'Predicted FTND']
        res_df = pd.DataFrame(np.c_[id_arr, ftnd_arr, pred_ftnd], columns=column_headings)
        res_df.to_csv(models_dir_path + 'fit.csv', index=None)

    return metrics_arr


if __name__ == '__main__':

    # Define the parameters here

    # Select the data source to train the model on. Possible values task1, task2, task3, task4, ov
    data_source = 'ov'

    # Set to true if you want to overwrite the previously trained models and save new ones
    save_models = False
    save_results = True

    # Task 1 - 2.024,8.763,0.965,1.498,0.331
    # Task 2 - 2.021,8.465,0.964,1.447,0.179
    # Task 3 - 2.117,8.781,1.009,1.501,0.244
    # Task 4 - 1.935,7.737,0.922,1.323,0.0001
    # Overall - 1.991,8.147,0.949,1.393,0.226

    metrics_array = train_svr(data_source, save_models, save_results)
