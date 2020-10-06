import time
from tkinter import *

"""Generate animation of the data for task 1 for a given subject"""
"""Green area represents the location of the image"""
"""The task was to look away from the image"""

r = 3

subj_id = 'S01'

root_dir = '../data/' + subj_id + '/task1/'

set_id = 0
img_id = 0

w = 1366.0
h = 768.0


def create_path(canvas, index):

    # color_init -= 10000
    # color_init += 1

    c_w = int((99 * index) / max_pts)

    if c_w < 10:
        r_w = "0" + str(int(c_w))
    else:
        r_w = str(int(c_w))

    if c_w >= 90:
        b_w = "0" + str(int(99 - c_w))
    else:
        b_w = str(99 - int(c_w))

    color = "#" + b_w + "00" + r_w

    if index < max_pts - 1:
        temp_x = x_list[index]
        temp_y = y_list[index]
        delay = t_list[index + 1] - t_list[index]

        canvas.create_oval(temp_x - r, temp_y - r, temp_x + r, temp_y + r, fill=color)
        canvas.after(delay, create_path, canvas, index + 1)
    else:
        temp_x = x_list[index]
        temp_y = y_list[index]

        canvas.create_oval(temp_x - r, temp_y - r, temp_x + r, temp_y + r, fill=color)


animation = Tk()
print(0)

fname = root_dir + str(set_id) + '_' + str(img_id) + '.txt'
f = open(fname, 'r')

img_pos = f.readline()
img_pos = img_pos.rstrip()
img_pos = img_pos.split()

img_x0 = float(img_pos[0])
img_x1 = float(img_pos[1])

img_y0 = float(img_pos[2])
img_y1 = float(img_pos[3])

print(1)
canvas = Canvas(animation, width=w, height=h)

canvas.pack(expand=YES, fill=BOTH)

# canvas.configure(background='#808080')
canvas.create_rectangle(img_x0, img_y0, img_x1, img_y1, fill='green')

t_list = []
x_list = []
y_list = []

for line in f:
    temp_ar = line.split()
    t_list.append(int(temp_ar[0]))
    x_list.append(float(temp_ar[1]))
    y_list.append(float(temp_ar[2]))

max_pts = len(x_list)

create_path(canvas, 0)

animation.mainloop()
print(2)
