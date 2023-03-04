import TeamProjectSource as TPS
from TeamProjectSource import *

#TPS.Ultras()
TB = TPS.ThunderBorg()
TB.Init()
#1 = max power, can set power output to a variable and control it that way (0, 0.5, 0.75, 1)

class EggBot:
    def forward():
        TB.SetMotors(fPower)
        print("1")
        
    def reverse():
        TB.SetMotors(rPower)
        print("2")
    
    def left():
        TB.SetMotor1(fPower)
        TB.SetMotor2(rPower)
        print("3")
    
    def right():
        TB.SetMotor1(rPower)
        TB.SetMotor2(fPower)
        print("4")
        
    while True:
        forward()
        time.sleep(1)
        reverse()
        time.sleep(1)
        left()
        time.sleep(1)
        right()
        time.sleep(1)
        TB.MotorsOff()
        break    

"""
    def navigate():
        longitutde = java.longitude
        newLongitude = update.java.longitude
        latitude = java.latitude
        newLatitude = update.java.latitude
       
        forward()
        reverse()
        left()
        right()
        
        if newLongitude > longitude:
            foward()
        elif newLongitude > longitude & newLatitude > latitude:
            forward()
            right()
        elif newLongitude < longitude:
            reverse()
        elif newLongitude < longitude & newLatitude > latitude:
            reverse()
            right()
    
    def avoidObstacles():
        if ultraSonicFront > 10:
            reverse()
            right()
            
        elif ultraSonicRear > 10:
            forward()
            right()
    
    
    if __name__ == "__main__":
    try:
        while True:
            if avoidObstacles == False:
                navigate()
        
            elif UltraSonics == True:
                avoidObs()
        
    except KeyboardInterrupt()
        TB.MotorsOff()
"""    
EggBot()