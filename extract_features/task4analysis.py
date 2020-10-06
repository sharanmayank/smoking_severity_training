import numpy as np
import os
from os import listdir
import re


def longestSubstringFinder(string1, string2):

    if string1 == string2:
        return string1
    answer = ""
    len1, len2 = len(string1), len(string2)
    for i in range(len1):
        match = ""
        for j in range(len2):
            if (i + j < len1) and (string1[i + j] == string2[j]):
                match += string2[j]
            else:
                if len(match) > len(answer):
                    answer = match
                match = ""
    return answer


def isOnImage(x0, x1, y0, y1, x, y):
    return (x >= x0) and (x <= x1) and (y <= y1)and (y >= y0)


root_dir = '../data/'
result_dir = '../results/'

subj_list = listdir(root_dir)

task_res_file = result_dir + 'task4/' + 'overall.csv'
f_ov = open(task_res_file, 'w+')

w = 1366.0
h = 768.0
vw = 2 * w / 3
vh = 2 * h / 3
start = 2500
freq = 5000
period = 1000


if not os.path.exists(result_dir):
    os.makedirs(result_dir)

for subj_id in subj_list:

    # Compute features for a given subject id

    task4_dir = root_dir + subj_id + '/task4/'

    subj_res_dir = result_dir + 'task4/'

    if not os.path.exists(subj_res_dir):
        os.makedirs(subj_res_dir)

    res_file = subj_res_dir + '/' + str(subj_id) + '.txt'

    vid_wise_res = []

    for vid_id in range(2):

        # Iterate over each video in each set

        fname = task4_dir + str(vid_id) + '.txt'
        # print fname
        f = open(fname, 'r')

        # Initialize feature variables

        tov = 0
        tabove = 0
        tbelow = 0

        for line in f:

            line_t = line.rstrip()
            line_t = line_t.split()

            # Extract gaze location

            tstamp = int(line_t[0])
            xpos = float(line_t[1])
            ypos = float(line_t[2])

            if isOnImage(w/2 - vw/2, w/2 + vw/2, h/2 - vh/2, h/2 + vh/2, xpos, ypos):
                tov += 17
            if ypos < h/2 - vh/2:
                tabove += 17
            if ypos > h/2 + vh/2:
                tbelow += 17
        f.close()

        f2 = open(task4_dir + str(vid_id) + '_seq.txt', 'r')

        dis_seq = f2.readline()
        dis_seq = dis_seq.rstrip()

        print(dis_seq)

        ent_seq = f2.readline()
        ent_seq = ent_seq.rstrip()

        print(ent_seq)

        match_seq = longestSubstringFinder(dis_seq, ent_seq)
        print(match_seq)

        vid_wise_res.append((vid_id, tov, tabove, tbelow, len(match_seq)))

    f2 = open(res_file, 'w+')

    f2.write('Task4 Results\n\n')

    f_ov.write(subj_id)

    for (vi, tov, ta, tb, mm) in vid_wise_res:
        f2.write('Video ' + str(vi) + ' : time on video - ' + str(tov) + ' ms time above video - ' +
                 str(ta) + ' ms time below video - ' + str(tb) + ' ms match percentage - ' + str(float(mm) * 100 / 6) +
                 '\n')

        f_ov.write(',' + str(tov) + ',' + str(ta) + ',' + str(tb) + ',' + str(float(mm) * 100 / 6))

    f_ov.write('\n')

    f2.close()

f_ov.close()
