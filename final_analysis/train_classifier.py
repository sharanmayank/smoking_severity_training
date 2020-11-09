import os
import pickle
import numpy as np
import pandas as pd

from matplotlib import pyplot as plt

from sklearn.metrics import classification_report

from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import GradientBoostingClassifier

# Initialize global variables and parameters

model_name = 'gb'   # Options : 'rf', 'gb'
task_name = 't1'    # Options : 't1', 't2', 't3', 't4', 'to'

verbose = False
norm_feat = False
grid_search = False
save_models = False

data_dir = '../tr_data/'
models_dir = '../clf_models/' + model_name + '_models/' + task_name + '/'

if not os.path.exists(models_dir):
    os.makedirs(models_dir)

# Based on the task load the data

file_name_dict = {
    't1': 'task1_data.csv',
    't2': 'task2_data.csv',
    't3': 'task3_data.csv',
    't4': 'task4_data.csv',
    'to': 'ov_data.csv',
}

data_file = data_dir + file_name_dict.get(task_name)
data_df = pd.read_csv(data_file)

# Extract features and the labels

id_arr = data_df['Subject ID'].values
clf_labels_arr = data_df['Smoker bool'].values

feature_list = np.array(data_df.columns[1: -2])
print('Features: ', ','.join(feature_list))

feature_val_arr = data_df[feature_list].values

if task_name == 'to':
    feature_val_arr = np.c_[feature_val_arr[:, :10], feature_val_arr[:, 11:]]

# Perform leave one from each class out CV and collate results

num_examples = feature_val_arr.shape[0]
smoker_ex_idx = np.where(clf_labels_arr == 1)[0]
non_smoker_ex_idx = np.where(clf_labels_arr == -1)[0]

pred_label = np.full_like(clf_labels_arr, fill_value=np.nan)

# Perform Grid search for hyper-parameters or initialize them to the preset best values

n_est_idx = 0
m_depth_idx = 1

hp_dict = {
    'rf': {
        't1': [8, 4],   # 70%
        't2': [6, 4],   # 50%
        't3': [4, 6],   # 56.67%
        't4': [4, 3],   # 66.67%
        'to': [14, 4],  # 56.67%
    },
    'gb': {
        't1': [4, 5],   # 80%
        't2': [12, 2],  # 50%
        't3': [10, 2],  # 70%
        't4': [2, 6],   # 56.67%
        'to': [6, 5],   # 83.33%
    }
}

if grid_search:
    n_est_arr = np.arange(2, 20, 2)
    max_depth_arr = np.arange(1, 10, 1)
else:
    n_est_arr = np.array([hp_dict.get(model_name).get(task_name)[n_est_idx]])
    max_depth_arr = np.array([hp_dict.get(model_name).get(task_name)[m_depth_idx]])

acc_arr = np.full(shape=(n_est_arr.shape[0], max_depth_arr.shape[0]), fill_value=np.nan)

for idx_1 in range(len(n_est_arr)):

    n_est = n_est_arr[idx_1]

    for idx_2 in range(len(max_depth_arr)):

        m_depth = max_depth_arr[idx_2]

        print('Number of Estimators', n_est, 'Max Depth', m_depth)

        for r_st in range(1, 2):
            for cv_fold_idx in range(num_examples // 2):

                if verbose:
                    print('CV Fold number', cv_fold_idx + 1)

                # Identify examples to be selected for testing

                test_ex_bool = np.full_like(clf_labels_arr, fill_value=False, dtype=bool)
                test_ex_bool[smoker_ex_idx[cv_fold_idx]] = True
                test_ex_bool[non_smoker_ex_idx[cv_fold_idx]] = True

                # Split the data into train and test

                train_x = feature_val_arr[~test_ex_bool, :]
                test_x = feature_val_arr[test_ex_bool, :]

                train_y = clf_labels_arr[~test_ex_bool]
                test_y = clf_labels_arr[test_ex_bool]

                # Fit a classifier and get results

                if model_name == 'rf':
                    clf_model = RandomForestClassifier(n_estimators=n_est, max_depth=m_depth, random_state=1)
                elif model_name == 'gb':
                    clf_model = GradientBoostingClassifier(n_estimators=n_est, max_depth=m_depth, random_state=r_st)

                clf_model.fit(train_x, train_y)

                tr_pred_labels = clf_model.predict(train_x)
                te_pred_labels = clf_model.predict(test_x)

                if verbose:
                    print('Training report')
                    print(classification_report(train_y, tr_pred_labels))

                pred_label[test_ex_bool] = te_pred_labels

                # Save models if flag is on

                if save_models:
                    test_ex_idx = np.where(test_ex_bool)[0]

                    for ex_idx in test_ex_idx:
                        model_file = models_dir + 'eval_' + str(ex_idx) + '.pb'
                        pickle.dump(clf_model, open(model_file, 'wb'))

            # print('Test report')
            # print(classification_report(clf_labels_arr, pred_label))

            cr_dict = classification_report(clf_labels_arr, pred_label, output_dict=True)
            acc_arr[idx_1, idx_2] = cr_dict.get('accuracy')

            print(r_st, cr_dict.get('accuracy'))

# Dump grid search results

if grid_search:
    plt.imshow(acc_arr, cmap='jet')

    plt.xticks(np.arange(0, len(max_depth_arr)), max_depth_arr)
    plt.yticks(np.arange(0, len(n_est_arr)), n_est_arr)

    plt.xlabel('Max Depth')
    plt.ylabel('Number of Estimators')

    plt.title('Best Accuracy:' + str(np.max(acc_arr)))

    plt.colorbar()
    plt.savefig(models_dir + 'grid_search')
