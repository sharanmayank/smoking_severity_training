import time
from tkinter import *

"""Generate animation of the data for task 3 for a given subject"""
"""Moving white box represents the location of the image"""
"""The task was to follow on the cross at te center"""

r = 3

subj_id = 'P01'

root_dir = '../data/' + subj_id + '/task3/'

set_id = 0
img_id = 1

w = 1366.0
h = 768.0


def create_path(canvas, index):

    # color_init -= 10000
    # color_init += 1

    c_w = int((99 * index) / max_pts)

    if c_w < 10:
        r_w = "0" + str(c_w)
    else:
        r_w = str(c_w)

    if c_w >= 90:
        b_w = "0" + str(99 - c_w)
    else:
        b_w = str(99 - c_w)

    color = "#" + b_w + "00" + r_w

    if index < max_pts - 1:
        temp_x1 = img_x1_list[index]
        temp_x2 = img_x2_list[index]

        temp_y1 = img_y1_list[index]
        temp_y2 = img_y2_list[index]

        temp_x = crs_x_list[index]
        temp_y = crs_y_list[index]

        delay = t_list[index + 1] - t_list[index]

        canvas.create_rectangle(temp_x1, temp_y1, temp_x2, temp_y2)
        canvas.create_oval(temp_x - r, temp_y - r, temp_x + r, temp_y + r, fill=color)
        canvas.after(delay, create_path, canvas, index + 1)
    else:
        temp_x1 = img_x1_list[index]
        temp_x2 = img_x2_list[index]

        temp_y1 = img_y1_list[index]
        temp_y2 = img_y2_list[index]

        temp_x = crs_x_list[index]
        temp_y = crs_y_list[index]

        canvas.create_rectangle(temp_x1, temp_y1, temp_x2, temp_y2)
        canvas.create_oval(temp_x - r, temp_y - r, temp_x + r, temp_y + r, fill=color)


animation = Tk()
print(0)

fname = root_dir + str(set_id) + '_' + str(img_id) + '.txt'
f = open(fname, 'r')

print(1)

canvas = Canvas(animation, width=w, height=h)
canvas.pack(expand=YES, fill=BOTH)


t_list = []

crs_x_list = []
crs_y_list = []

img_x1_list = []
img_x2_list = []

img_y1_list = []
img_y2_list = []

for line in f:
    temp_ar = line.split()

    t_list.append(int(temp_ar[0]))

    crs_x_list.append(float(temp_ar[1]))
    crs_y_list.append(float(temp_ar[2]))

    img_x1_list.append(float(temp_ar[3]))
    img_x2_list.append(float(temp_ar[4]))

    img_y1_list.append(float(temp_ar[5]))
    img_y2_list.append(float(temp_ar[6]))

max_pts = len(t_list)

create_path(canvas, 0)

animation.mainloop()
print(2)
