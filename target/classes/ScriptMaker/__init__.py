import random

from pygame.math import clamp

with open("../../scripts/4000pills", "w", encoding="utf-8") as file:
    #HEADER
    for i in range(4000):
        file.write("ent_create phys_pill ")
        #origin
        bound = 2000
        vel = 1
        x = random.random() * bound - 1000
        y = random.random() * bound - 1000
        file.write("origin ") #from 1->4
        file.write(str(x) + "," + str(y) + " ")
        #origin
        file.write("vel ") #from -1->1
        file.write(str(random.random() * vel - vel/2) + "," + str(random.random() * vel - vel/2) + " ")
        #file.write(str("0,0 "))
        #size
        file.write("size ")
        file.write(str(random.random()*4+1) + " ")
        #size
        file.write("ang ")
        file.write(str(random.random()*360) + " ")
        #length
        file.write("length ") #from 0(50%) -> 2
        val = random.random()*0
        if val < 6:
            val = 0
        file.write(str(val) + " ")
        #movable
        file.write("movable ") #from 0(20%) rest >0
        file.write("1 ")#str(clamp(random.random()-0.1, 0, 1)) + " ")
        #mass
        file.write("mass ") #from 0.3->2
        #file.write(str(random.random()*2+0.5) + " ")
        file.write("1 ")
        #colorIndx
        file.write("colorIdx ") #from 100
        file.write(str(int(x/5) + int(y/5)) + " ")
        file.write("\n")
    file.write("sm_step 1")