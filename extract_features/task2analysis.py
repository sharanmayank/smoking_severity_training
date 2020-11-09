import numpy as np
import os
from os import listdir
import re

from itertools import groupby

from copy import deepcopy


def moving_sum(in_data, window):

    """
    Parameters:
        in_data             (np.ndarray)        : Input data
        window              (int)               : Window size
    Returns:
        arr                 (np.ndarray)        : Accumulated array
    """

    data = deepcopy(in_data)

    # Take cumulative sum over the array

    arr = data.cumsum()

    # Subtract a shifted array with original array

    arr[window:] = arr[window:] - arr[:-window]

    return arr


def find_seq(arr, min_seq_length=1):

    """
    Parameters:
        arr                 (np.ndarray)        : Array in which to find the sequences
        min_seq_length      (int)               : Set as the smallest length of the sequence to be considered a chunk
    Returns:
        res                 (np.ndarray)        : 4 column result, integer, start_idx, end_idx, num_in_seq
    """

    # Initialise the result array

    res = []
    start_idx = 0

    # Get groups

    group_list = groupby(arr)

    for seq_num, seq in group_list:

        # Get number of elements in the sequence

        seq_len = len(list(seq))

        # Discard single elements since they are not a sequence

        if seq_len <= min_seq_length:
            start_idx += seq_len
            continue
        else:
            temp_res = [seq_num, start_idx, start_idx + seq_len - 1, seq_len]
            start_idx += seq_len
            res.append(temp_res)

    return np.array(res)


def isOnImage(x0, x1, y0, y1, x, y):
    return (x >= x0) and (x <= x1) and (y <= y1)and (y >= y0)


root_dir = '../data/'
result_dir = '../results/'

subj_list = listdir(root_dir)

w = 1366.0
h = 768.0
imw = w/6
imh = h/6

if not os.path.exists(result_dir + 'task2/'):
    os.makedirs(result_dir + 'task2/')

task_res_file = result_dir + 'task2/' + 'overall.csv'
f_ov = open(task_res_file, 'w+')

for subj_id in subj_list:

    # Compute features for a given subject id

    task2_dir = root_dir + subj_id + '/task2/'

    subj_res_dir = result_dir + 'task2/'

    if not os.path.exists(subj_res_dir):
        os.makedirs(subj_res_dir)

    res_file = subj_res_dir + '/' + str(subj_id) + '.txt'

    img_wise_res = []

    pos_avg_toi = 0
    pos_avg_toc = 0
    neg_avg_toi = 0
    neg_avg_toc = 0

    s_data_im = np.full(shape=(8, 354), fill_value=np.nan)

    s_idx = 0

    for set_id in range(4):
        for img_id in range(4):

            img_x = w / 2
            img_y = h / 2

            # Iterate over each image in each set

            fname = task2_dir + str(set_id) + '_' + str(img_id) + '.txt'
            # print fname
            f = open(fname, 'r')

            # Initialize feature variables

            toi = 0
            toc = 0

            gaze_arr = []

            for line in f:

                line_t = line.rstrip()
                line_t = line_t.split()

                # Read location of the cross

                tstamp = int(line_t[0])
                xpos = float(line_t[1])
                ypos = float(line_t[2])
                cx0 = float(line_t[3])
                cx1 = float(line_t[4])
                cy0 = float(line_t[5])
                cy1 = float(line_t[6])

                b1 = isOnImage(cx0, cx1, cy0, cy1, xpos, ypos)
                b2 = isOnImage(w / 2 - imw / 2, w / 2 + imw / 2, h / 2 - imh / 2, h / 2 + imh / 2, xpos, ypos)

                # If gaze is on cross we add to time on cross

                if b1 and not b2:
                    toc += 17

                # If gaze is on image we add to time on image

                if b2 and not b1:
                    toi += 17

                gaze_arr.append([tstamp, xpos, ypos, cx0, cx1, cy0, cy1])

            gaze_arr = np.array(gaze_arr)

            c_x_arr = (gaze_arr[:, 3] + gaze_arr[:, 4]) / 2
            c_y_arr = (gaze_arr[:, 5] + gaze_arr[:, 6]) / 2

            c_dist_arr = np.power(np.power(gaze_arr[:, 1] - c_x_arr, 2) + np.power(gaze_arr[:, 2] - c_y_arr, 2),
                                  0.5)
            im_dist_arr = np.power(np.power(gaze_arr[:, 1] - img_x, 2) + np.power(gaze_arr[:, 2] - img_y, 2), 0.5)

            if gaze_arr.shape[0] >= 354:
                if img_id < 2:
                    s_data_im[s_idx, :] = im_dist_arr[:354]
                    s_idx += 1

            img_wise_res.append((set_id, img_id, toc, toi))

            if img_id < 2:
                pos_avg_toi += toi
                pos_avg_toc += toc
            else:
                neg_avg_toi += toi
                neg_avg_toc += toc

            f.close()

    # Calculate the number of switches

    s_profile_im = np.nanpercentile(s_data_im, q=50, axis=0)

    window = 50

    div_arr = np.full_like(s_profile_im, fill_value=window)
    div_arr[:window] = np.arange(1, window + 1)

    s_profile_im = np.divide(moving_sum(s_profile_im, window), div_arr)
    s_profile_im_der = np.r_[0, np.diff(s_profile_im)]

    thr = 0.5

    s_profile_im_der[s_profile_im_der < -thr] = -5
    s_profile_im_der[s_profile_im_der > thr] = 5
    s_profile_im_der[np.logical_and(s_profile_im_der >= -thr, s_profile_im_der <= thr)] = 0

    s_seq = find_seq(s_profile_im_der, 1)

    for idx_2 in range(s_seq.shape[0] - 1):
        if s_seq[idx_2, 0] == s_seq[idx_2 + 1, 0]:
            s_seq[idx_2 + 1, 1] = s_seq[idx_2, 1]
            s_seq[idx_2 + 1, 3] = s_seq[idx_2 + 1, 2] - s_seq[idx_2, 1] + 1
            s_seq[idx_2, 0] = np.nan

    s_seq = s_seq[~np.isnan(s_seq[:, 0])]

    for idx_2 in range(s_seq.shape[0] - 2):
        if s_seq[idx_2, 0] * s_seq[idx_2 + 2, 0] > 0 and s_seq[idx_2 + 1, 0] == 0 and s_seq[idx_2 + 1, 3] < 10:
            s_seq[idx_2 + 2, 1] = s_seq[idx_2, 1]
            s_seq[idx_2 + 2, 3] = s_seq[idx_2 + 2, 2] - s_seq[idx_2, 1] + 1

            s_seq[idx_2, 0] = np.nan
            s_seq[idx_2 + 1, 0] = np.nan

    s_seq = s_seq[~np.isnan(s_seq[:, 0])]

    s_seq = s_seq.astype(int)

    s_switch = 0

    for idx_2 in range(s_seq.shape[0] - 1):

        if s_seq[idx_2, 0] * s_seq[idx_2 + 1, 0] < 0 and max(s_seq[idx_2, 3], s_seq[idx_2 + 1, 3]) > 5:

            temp_arr = s_profile_im[s_seq[idx_2, 1]: s_seq[idx_2 + 1, 2] + 1]

            if np.sum(temp_arr > 300) * 100 / len(temp_arr) < 70:
                s_switch += 1

        elif ((idx_2 < s_seq.shape[0] - 2) and s_seq[idx_2, 0] * s_seq[idx_2 + 2, 0] < 0 and
              s_seq[idx_2 + 1, 0] == 0 and max(s_seq[idx_2, 3], s_seq[idx_2 + 2, 3]) > 5):
            temp_arr = s_profile_im[s_seq[idx_2, 1]: s_seq[idx_2 + 2, 2] + 1]

            if np.sum(temp_arr > 300) * 100 / len(temp_arr) < 70:
                s_switch += 1

    if np.sum(s_profile_im[:60] < 400) == 0:
        s_switch = max(0, s_switch - 2)

    # Due to erroneous data for the subject we substitute the median value of the feature

    if subj_id == 'S16':
        s_switch = 4

    # 8 images of each type so average the features calculated

    pos_avg_toi = float(pos_avg_toi) / 8
    pos_avg_toc = float(pos_avg_toc) / 8
    neg_avg_toi = float(neg_avg_toi) / 8
    neg_avg_toc = float(neg_avg_toc) / 8

    f2 = open(res_file, 'w+')

    f2.write('Task2 Results\n\n')

    f2.write('Smoking Cues : avg time on cross - ' + str(pos_avg_toc) + ' ms avg time on image - ' + str(pos_avg_toi) +
             ' ms num switches - ' + str(s_switch) + '\n')

    f2.write('Non Smoking Cues : avg time on cross - ' + str(neg_avg_toc) + ' ms avg time on image - ' + str(neg_avg_toi) +
             ' ms\n')

    for (si, ii, toc, toi) in img_wise_res:
        f2.write('Set ' + str(si) + ' Image ' + str(ii) + ' : time on cross - ' + str(toc) + ' ms time on image - ' +
                 str(toi) + ' ms\n')

    f_ov.write(subj_id + ',' + str(pos_avg_toc) + ',' + str(pos_avg_toi) + ',' + str(neg_avg_toc) + ',' +
               str(neg_avg_toi) + ',' + str(s_switch) + '\n')

    f2.close()

f_ov.close()
