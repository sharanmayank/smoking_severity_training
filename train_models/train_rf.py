"""
Train a random forest regression model on the data.
"""

# Import libraries needed

import os
import pickle
import numpy as np
import pandas as pd

from sklearn.ensemble import RandomForestRegressor

# Import functions from the project

from train_models.utils import fetch_data
from train_models.utils import compute_metrics
from train_models.utils import get_hyper_params

from train_models.constants import PathConstants


def train_rf(task_name, save_models_bool, save_results_bool):

    # Create the models directory if does not exist

    models_dir_path = PathConstants.rf_models_dir + task_name + '/'

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

    hp_dict = get_hyper_params(model_name='rf', task_name=task_name)

    n_est = hp_dict.get('n_est')
    m_depth = hp_dict.get('max_depth')
    rand_st = hp_dict.get('random_state')

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

        model = RandomForestRegressor(n_estimators=n_est, max_depth=m_depth, random_state=rand_st)
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

    mae, mse, nmae, nmse, r2_score = compute_metrics(pred_ftnd, ftnd_arr)

    print('Metrics: RF', task_name)
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
    save_results = False

    # Task 1 - 149 [1.289 3.333 0.614 0.57 0.46]
    # Task 2 - 581 [1.756 6.022 0.837 1.03  0.066]
    # Task 3 - 97 [2.02  6.849 0.963 1.171 0.019]
    # Task 4 - 62 [1.9   6.359 0.906 1.087 0.044]
    # Overall - 364 [1.778 6.437 0.847 1.101 0.05]

    metrics_array = train_rf(data_source, save_models, save_results)
