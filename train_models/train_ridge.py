"""
Train a ridge regression model on the data.
"""

# Import libraries needed

import os
import pickle
import numpy as np
import pandas as pd

# Import functions from the project

from train_models.utils import fetch_data
from train_models.utils import compute_metrics
from train_models.utils import get_hyper_params

from train_models.constants import PathConstants


def fit_ridge(train_x, train_y, dt=0.1):

    num_ex = train_x.shape[0]
    num_feat = train_x.shape[1]

    train_x_1 = np.c_[train_x, np.ones(shape=(num_ex, 1))]

    if num_feat < num_ex:

        sq_mat = np.matmul(train_x_1.transpose(), train_x_1) + dt * np.identity(num_feat + 1, dtype=float)
        sq_mat_pinv = np.linalg.pinv(sq_mat)

        weights_arr = np.matmul(np.matmul(sq_mat_pinv, train_x_1.transpose()), train_y)

    else:

        sq_mat = np.matmul(train_x_1, train_x_1.transpose()) + dt * np.identity(num_ex, dtype=float)
        sq_mat_pinv = np.linalg.pinv(sq_mat)

        weights_arr = np.matmul(np.matmul(train_x_1.transpose(), sq_mat_pinv), train_y)

    return weights_arr


def predict_ridge(weights_arr, test_x):

    return np.dot(weights_arr, np.r_[test_x.flatten(), 1])


def train_ridge(task_name, save_models_bool, save_results_bool):

    # Create the models directory if does not exist

    models_dir_path = PathConstants.ridge_models_dir + task_name + '/'

    if save_models_bool and not(os.path.exists(models_dir_path)):
        os.makedirs(models_dir_path)

    # Fetch the data for the given task

    id_arr, feature_val_arr, ftnd_arr = fetch_data(task_name=task_name)

    # Normalize feature value array

    feature_val_arr = np.divide(feature_val_arr - np.mean(feature_val_arr, axis=0), np.std(feature_val_arr, axis=0))

    # Initialize variables to be used for cross validation training

    num_examples = feature_val_arr.shape[0]
    pred_ftnd = np.full_like(ftnd_arr, fill_value=np.nan, dtype=float)

    # Load hyper-parameters for the model

    hp_dict = get_hyper_params(model_name='ridge', task_name=task_name)

    dt = hp_dict.get('delta')

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

        weights_arr = fit_ridge(train_x, train_y, dt)

        # If save models bool is true save the model at appropriate location

        if save_models_bool:
            model_file_path = models_dir_path + 'leave_' + str(te_ex_idx) + '_out.pb'
            pickle.dump(weights_arr, open(model_file_path, 'wb'), protocol=pickle.HIGHEST_PROTOCOL)

        # Predict the value for the test example

        pred_y = predict_ridge(weights_arr, test_x)

        if pred_y < 0:
            pred_y = 0

        pred_ftnd[te_ex_idx] = pred_y

        print(te_ex_idx, test_y, pred_y)

    # Calculate metrics

    mae, mse, nmae, nmse, r2_score = compute_metrics(pred_ftnd, ftnd_arr)

    print('Metrics: SVR', task_name)
    print('Mean Absolute Error:', mae)
    print('Mean Squared Error:', mse)
    print('Normalized Mean Absolute Error:', nmae)
    print('Normalized Mean Squared Error:', nmse)
    print('R2 score:', r2_score)

    metrics_arr = np.array([mae, mse, nmae, nmse, r2_score])
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

    # Task 1 - 0.5 [2.427 7.585 1.157 1.297 0.035]
    # Task 2 - 0.5 [2.136 6.699 1.018 1.145 0.   ]
    # Task 3 - 1.0 [2.191 7.246 1.045 1.239 0.034]
    # Task 4 - 0.5 [2.579 9.012 1.23  1.541 0.094]
    # Overall - 1.0 [ 2.608 11.379  1.243  1.945  0.022]

    metrics_array = train_ridge(data_source, save_models, save_results)
