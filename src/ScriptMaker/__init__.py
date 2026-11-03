import random

from pygame.math import clamp

with open("../../scripts/randomPills", "w", encoding="utf-8") as file:
    #HEADER
    for i in range(1000):
        file.write("ent_create phys_pill ")
        #origin
        bound = 60
        vel = 20
        x = random.random() * (bound - 2) + 1
        y = random.random() * (bound - 2) + 1
        file.write("origin ") #from 1->4
        file.write(str(x) + "," + str(y) + " ")
        #origin
        file.write("vel ") #from -1->1
        file.write(str(random.random() * vel - vel/2) + "," + str(random.random() * vel - vel/2) + " ")
        #size
        file.write("size ")
        file.write(str(random.random()*0.5+0.05) + " ")
        #size
        file.write("ang ")
        file.write(str(random.random()*360) + " ")
        #length
        file.write("length ") #from 0(50%) -> 2
        file.write(str(clamp(random.random()*4-2, 0, 2)) + " ")
        #movable
        file.write("movable ") #from 0(20%) rest >0
        file.write("1 ")#str(clamp(random.random()-0.1, 0, 1)) + " ")
        #mass
        file.write("mass ") #from 0.3->2
        file.write(str(random.random()*17+3) + " ")
        #colorIndx
        file.write("colorIdx ") #from 100
        file.write(str(int(x/5) + int(y/5)) + " ")
        file.write("\n")
    file.write("sm_step 1")