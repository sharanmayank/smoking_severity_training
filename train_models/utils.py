"""
Utility functions to be used all across training and evaluation
"""

# Import libraries needed

import numpy as np
import pandas as pd

# Import functions from the project

from train_models.constants import PathConstants
from train_models.constants import RFHyperParams
from train_models.constants import RidgeHyperParams


def fetch_data(task_name):

    # Load data from the file

    data_file = PathConstants.data_dir + task_name + '_data.csv'
    data_df = pd.read_csv(data_file)

    # Extract features and the labels

    id_arr = data_df['Subject ID'].values
    ftnd_arr = data_df['FTND'].values

    feature_list = np.array(data_df.columns[1: -2])
    feature_val_arr = data_df[feature_list].values

    return id_arr, feature_val_arr, ftnd_arr


def get_hyper_params(model_name, task_name):

    hp_dict = {}

    if model_name == 'rf':
        hp_dict = RFHyperParams.hp_dict.get(task_name)
    elif model_name == 'ridge':
        hp_dict = RidgeHyperParams.hp_dict.get(task_name)

    return hp_dict


def compute_metrics(pred_y, true_y):

    mean_y = np.mean(true_y)
    diff = pred_y - true_y

    mae = np.mean(np.abs(diff))
    mse = np.mean(np.square(diff))

    nmae = np.sum(np.abs(diff)) / np.sum(np.abs(true_y - mean_y))
    nmse = np.sum(np.square(diff)) / np.sum(np.square(true_y - mean_y))

    r2_score = np.square(np.corrcoef(true_y, pred_y)[0, 1])

    return np.round(mae, 3), np.round(mse, 3), np.round(nmae, 3), np.round(nmse, 3), np.round(r2_score, 3)
