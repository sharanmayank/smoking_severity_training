import numpy as np
import pandas as pd
import statsmodels.api as sm

from sklearn.metrics import r2_score
from sklearn.linear_model import LinearRegression

data_dir = '../tr_data/'
data_file = data_dir + 'task2_data.csv'

# Load the file

data_df = pd.read_csv(data_file)

# Extract the feature and the labels

id_arr = data_df['Subject ID'].values
labels_arr = data_df['FTND'].values

feat_arr = data_df['Num Switches'].values

# Evaluate correlation of feature with label

print(np.corrcoef(feat_arr.astype(float), labels_arr.astype(float)))

pred_arr = []

for idx in range(feat_arr.shape[0]):
    model = LinearRegression()

    idx_bool = np.full_like(labels_arr, fill_value=True, dtype=bool)
    idx_bool[idx] = False

    model.fit(feat_arr[idx_bool].reshape(-1, 1), labels_arr[idx_bool])
    pred_y = model.predict(X=feat_arr[~idx_bool].reshape(-1, 1))

    pred_arr.append(pred_y[0])

pred_arr = np.array(pred_arr)
pred_arr[pred_arr < 0] = 0

mae = np.mean(np.abs(pred_arr - labels_arr))
mse = np.mean(np.power(pred_arr - labels_arr, 2))

print('MAE', mae, 'MSE', mse, 'NMAE', mae / np.mean(np.abs(np.mean(labels_arr) - labels_arr)), 'NMSE',
      mse / np.mean(np.power(np.mean(labels_arr) - labels_arr, 2)))

# Perform r squared and p value analysis

print('R2 score', r2_score(labels_arr, pred_arr))

a = []
b = []

for idx in range(feat_arr.shape[0]):

    idx_bool = np.full_like(labels_arr, fill_value=True, dtype=bool)
    idx_bool[idx] = False

    X = feat_arr[idx_bool].reshape(-1, 1)
    y = labels_arr[idx_bool]

    X2 = sm.add_constant(X)
    est = sm.OLS(y, X2)
    est2 = est.fit()
    p_var = est2.pvalues
    a.append(p_var[0])
    b.append(p_var[1])

print('Intercept p value', np.mean(a), 'Coefficient p value', np.mean(b))
