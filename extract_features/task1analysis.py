import numpy as np
import os
from os import listdir


def isOnImage(x0, x1, y0, y1, x, y):
    return (x >= x0) and (x <= x1) and (y <= y1)and (y >= y0)


root_dir = '../data/'
result_dir = '../tr_data_dir/'

subj_list = listdir(root_dir)

task_res_file = result_dir + 'task1/' + 'overall.csv'
f_ov = open(task_res_file, 'w+')

if not os.path.exists(result_dir):
    os.makedirs(result_dir)

for subj_id in subj_list:

    # Compute features for a given subject id

    task1_dir = root_dir + subj_id + '/task1/'

    subj_res_dir = result_dir + 'task1/'

    if not os.path.exists(subj_res_dir):
        os.makedirs(subj_res_dir)

    res_file = subj_res_dir + '/' + str(subj_id) + '.txt'

    img_wise_res = []

    pos_avg_rxn = 0
    neg_avg_rxn = 0
    pos_avg_retn = 0
    neg_avg_retn = 0
    pos_avg_rett = 0
    neg_avg_rett = 0

    for set_id in range(4):
        for img_id in range(4):

            # Iterate over each image in each set

            fname = task1_dir + str(set_id) + '_' + str(img_id) + '.txt'
            # print fname
            f = open(fname, 'r')

            # Read location of the image

            img_pos = f.readline()
            img_pos = img_pos.rstrip()
            img_pos = img_pos.split()

            img_x0 = float(img_pos[0])
            img_x1 = float(img_pos[1])

            img_y0 = float(img_pos[2])
            img_y1 = float(img_pos[3])

            # Initialize feature variables

            rxn_time = 0
            number_returns = 0
            total_ret_time = 0
            previson = False
            isonimage = False
            firsttime = True
            rxntaken = False

            line_num = 0

            for line in f:

                line_t = line.rstrip()
                line_t = line_t.split()

                tstamp = int(line_t[0])
                xpos = float(line_t[1])
                ypos = float(line_t[2])

                previson = isonimage
                isonimage = isOnImage(img_x0, img_x1, img_y0, img_y1, xpos, ypos)

                # Identify the reaction time of the subject

                if not rxntaken:
                    if firsttime:
                        firsttime = not isonimage
                    else:
                        rxntaken = not isonimage
                        rxn_time = 17 * line_num
                else:

                    # Identify a possible return to the image

                    if isonimage and not previson:
                        number_returns += 1
                        total_ret_time += 17
                    elif isonimage and previson:
                        total_ret_time += 17

                line_num += 1

            # Calculate features of average return time

            if number_returns > 0:
                avg_ret_time = float(total_ret_time) / number_returns
            else:
                avg_ret_time = 0

            img_wise_res.append((set_id, img_id, rxn_time, number_returns, avg_ret_time))

            # Identify the image as smoking or non smoking cue based on the id of the image

            if img_id < 2:
                pos_avg_rxn += rxn_time
                pos_avg_retn += number_returns
                pos_avg_rett += avg_ret_time
            else:
                neg_avg_rxn += rxn_time
                neg_avg_retn += number_returns
                neg_avg_rett += avg_ret_time

            f.close()

    # Since there are 8 images for each type (smoking vs non-smoking) we calculate the feature as an average

    pos_avg_rxn = float(pos_avg_rxn) / 8
    pos_avg_retn = float(pos_avg_retn) / 8
    pos_avg_rett = float(pos_avg_rett) / 8
    neg_avg_rxn = float(neg_avg_rxn) / 8
    neg_avg_retn = float(neg_avg_retn) / 8
    neg_avg_rett = float(neg_avg_rett) / 8

    f2 = open(res_file, 'w+')

    f2.write('Task1 Results\n\n')

    f2.write('Smoking Cues : avg rxn time - ' + str(pos_avg_rxn) + ' ms avg num returns - ' + str(pos_avg_retn) +
             ' avg return time - ' + str(pos_avg_rett) + ' ms\n')

    f2.write('Non Smoking Cues : avg rxn time - ' + str(neg_avg_rxn) + ' ms avg num returns - ' + str(neg_avg_retn) +
             ' avg return time - ' + str(neg_avg_rett) + ' ms\n\n')

    for (si, ii, rt, nr, art) in img_wise_res:
        f2.write('Set ' + str(si) + ' Image ' + str(ii) + ' : rxn time - ' + str(rt) + ' ms num ret - ' +
                 str(nr) + ' ret time - ' + str(art) + ' ms\n')

    f2.close()

    f_ov.write(subj_id + ',' + str(pos_avg_rxn) + ',' + str(pos_avg_retn) + ',' + str(pos_avg_rett) + ',' +
               str(neg_avg_rxn) + ',' + str(neg_avg_retn) + ',' + str(neg_avg_rett) + '\n')

f_ov.close()

