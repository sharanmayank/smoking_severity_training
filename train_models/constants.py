"""
File containing all constants used in the code
"""


class PathConstants:

    """Contains all path constants"""

    data_dir = '../tr_data/'
    rf_models_dir = '../models/rf_models/'
    svr_models_dir = '../models/svr_models/'
    ridge_models_dir = '../models/ridge_models/'


class RFHyperParams:

    """Contains the hyper-parameters for random forest models"""

    hp_dict = {
        'task1': {
            'n_est': 3,
            'max_depth': 10,
            'random_state': 149,
        },
        'task2': {
            'n_est': 3,
            'max_depth': 10,
            'random_state': 581,
        },
        'task3': {
            'n_est': 3,
            'max_depth': 10,
            'random_state': 97,
        },
        'task4': {
            'n_est': 3,
            'max_depth': 10,
            'random_state': 62,
        },
        'ov': {
            'n_est': 3,
            'max_depth': 10,
            'random_state': 364,
        },
    }


class RidgeHyperParams:

    """Contains the hyper-parameters for ridge models"""

    hp_dict = {
        'task1': {
            'delta': 0.5,
        },
        'task2': {
            'delta': 0.5,
        },
        'task3': {
            'delta': 1,
        },
        'task4': {
            'delta': 0.5,
        },
        'ov': {
            'delta': 1,
        },
    }
