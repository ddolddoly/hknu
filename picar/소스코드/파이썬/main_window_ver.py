import time
import sys
import math
import threading
import socket


# CONSTANT declaration
RECEIVE_PORT = 2048
SEND_PORT = 2049
TARGET_ADDR = ""
TIME_PER_BEARING = 0.006 # 1 degree time at turn

# variable declaration
receiver = None
receiver_thread = None
sender = None
sender_thread = None
arduino = None
gps_thread = None


# initialization
def init():
    # socket setting
    global receiver
    global sender
    receiver = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    receiver.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    receiver.bind(("", RECEIVE_PORT))
    sender = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sender.bind(("", SEND_PORT))

    # thread setting
    global receiver_thread
    global sender_thread
    global gps_thread
    receiver_thread = threading.Thread(target=receive_thread)
    sender_thread = threading.Thread(target=send_thread)
    gps_thread = gps_drive_class() # for run/stop switch


# UDP packet receive thread
def receive_thread():
    global TARGET_ADDR
    while True:
        data, addr = receiver.recvfrom(200)
        if addr:
            TARGET_ADDR = addr[0]
        if data:
            if (data[0] == 'M'):
                # arduino.write(data)
                gps_thread.stop()
                print data
            elif (data[0] == 'G'):
                gps_thread.set_destination(data)
                gps_thread.resume()


# UDP packet send thread
def send_thread():
    while True:
        msg = ""
        try :
            pass

        except socket.error as msg:
            #print "Error Code : ", str(msg[0]), " Message ", msg[1]
            sys.exit()
        time.sleep(2)


# gps drive class
class gps_drive_class(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.flag = False
        self.my_lat = 0.0
        self.my_lon = 0.0
        self.latlon_list = []
        self.latlon_index = 0

    # run
    def run(self):
        while True:
            if self.flag:
                # find my latitude, longitude
                self.set_my_location(37.010852, 127.26433) # for test
                self.lat1, self.lon1 = self.get_my_location()

                # while until move to destination
                while self.flag:
                    # arduino.write("MF")
                    self.set_my_location(37.01071, 127.26417) # for test
                    self.lat2, self.lon2 = self.get_my_location()

                    # find bearing
                    self.my_bearing = self.get_bearing(self.lat1, self.lon1,\
                                                       self.lat2, self.lon2)
                    self.des_lat, self.des_lon = self.get_destination()
                    self.des_bearing = self.get_bearing(self.lat2, self.lon2,\
                                                        self.des_lat, self.des_lon)
                    
                    # find destination direction
                    self.direction, self.calc_bearing =\
                            self.get_direction(self.my_bearing, self.des_bearing)

                    
                    print "my bearing :", self.my_bearing
                    print "destination lat :", self.des_lat, "lon :", self.des_lon
                    print "destination bearing :", self.des_bearing
                    print "direction :", self.direction
                    print "calc_bearing :", self.calc_bearing


                    # motor control
                    time.sleep(2)
                    # arduino.write("MS")
                    if self.flag:
                        self.motor_turn(self.direction, self.calc_bearing)

                    # check arrive
                    if self.flag and self.is_arrive():
                        if self.latlon_index == len(self.latlon_list)-1:
                            self.stop()
                        else:
                            self.latlon_index = self.latlon_index + 1

            time.sleep(0.1)

    # resume
    def resume(self):
        self.flag = True
        

    # stop
    def stop(self):
        self.flag = False


    # check arrive
    def is_arrive():
        self.v = abs(self.get_my_location() - self.get_destination())
        if v < 0.0001:
            return True
        else:
            return False

        
    # set picar location
    def set_my_location(self, lat, lon):
        self.my_lat = lat
        self.my_lon = lon


    # get picar location. use gpsd
    def get_my_location(self):
        return self.my_lat, self.my_lon


    # set destination LatLon
    def set_destination(self, latlon):
        self.latlon_list = []
        self.latlon_index = 0
        self.temp = latlon[1:].split('/')
        for i in range(len(self.temp)-1):
            self.a = []
            self.a.append(round(float(self.temp[i].split(',')[0]), 5))
            self.a.append(round(float(self.temp[i].split(',')[1]), 5))
            self.latlon_list.append(self.a)
        print self.latlon_list
        

    # get destination LatLon
    def get_destination(self):
        self.temp = self.latlon_list[self.latlon_index]
        self.lat = float(self.temp[0])
        self.lon = float(self.temp[1])
        return self.lat, self.lon
    

    # get bearing
    def get_bearing(self, lat1, lon1, lat2, lon2):
        # convert to radian
        self.rlat1 = lat1 * (3.141592 / 180)
        self.rlon1 = lon1 * (3.141592 / 180)
        self.rlat2 = lat2 * (3.141592 / 180)
        self.rlon2 = lon2 * (3.141592 / 180)

        # get distance
        self.temp1 = math.sin(self.rlat1)\
                     * math.sin(self.rlat2)
        self.temp2 = math.cos(self.rlat1)\
                     * math.cos(self.rlat2)\
                     * math.cos(self.rlon1 - self.rlon2)
        self.distance = math.acos(self.temp1 + self.temp2)

        # get radian bearing
        self.temp1 = math.sin(self.rlat2)\
                     - math.sin(self.rlat1)\
                     * math.cos(self.distance)
        self.temp2 = math.cos(self.rlat1)\
                     * math.sin(self.distance)
        self.bearing = math.acos(self.temp1 / self.temp2)

        # get true bearing
        if math.sin(self.rlon2 - self.rlon1) < 0.0:
            self.true_bearing = self.bearing * (180 / 3.141592)
            self.true_bearing = self.true_bearing * -1.0
        else:
            self.true_bearing = self.bearing * (180 / 3.141592)
        
        return self.true_bearing
    

    # get destination direction
    # direction ==  1 : turn right
    # direction == -1 : turn left
    def get_direction(self, my_bearing, des_bearing):
        if my_bearing < 0.0:
            self.direction = -1
        else:
            self.direction = 1
                    
        if my_bearing < 0.0:
            self.direction = self.direction * -1
        if des_bearing < 0.0:
            self.direction = self.direction * -1

        self.calc_bearing = des_bearing - my_bearing
        if self.calc_bearing < 0.0:
            self.direction = self.direction * -1
            self.calc_bearing = 360 - abs(self.calc_bearing)

        if self.calc_bearing > 180.0:
            self.direction = self.direction * -1
            self.calc_bearing = 360 - self.calc_bearing

        return self.direction, self.calc_bearing
    

    def motor_turn(self, direction, bearing):
        if direction == 1:
            # arduino.write("MR")
            pass
        elif direction == -1:
            # arduino.write("ML")
            pass

        print bearing * TIME_PER_BEARING, "seconds"
        # time.sleep(bearing * TIME_PER_BEARING)
        # arduino.write("MS")


### main logic
init()
receiver_thread.start()
sender_thread.start()
gps_thread.start()


