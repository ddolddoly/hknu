import time
import sys
import math
import threading
import serial
import socket
import gps


# CONSTANT declaration
RECEIVE_PORT = 2048
SEND_PORT = 2049
TARGET_ADDR = ""
SERIAL_PORT = "/dev/ttyACM0"
SERIAL_PORT2 = "/dev/ttyACM1"
TIME_PER_BEARING = 0.005 # 1 degree time at turn

# variable declaration
receiver = None
receiver_thread = None
sender = None
sender_thread = None
arduino = None
gpsd = None
gps_thread = None
gps_flag = False
gps_data = None



# initialization
def init():
    # socket setting
    global receiver
    global sender
    receiver = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    receiver.bind(("", RECEIVE_PORT))
    sender = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sender.bind(("", SEND_PORT))

    # arduino setting
    global arduino
    try:
        arduino = serial.Serial(SERIAL_PORT, 9600)
    except:
        arduino = serial.Serial(SERIAL_PORT2, 9600)
    arduino.flushInput()

    # thread setting
    global receiver_thread
    global sender_thread
    global gps_thread
    receiver_thread = threading.Thread(target=receive_thread)
    sender_thread = threading.Thread(target=send_thread)
    gps_thread = gps_drive_class()

    # GPS setting
    global gpsd
    gpsd = gps.gps("127.0.0.1", "2947")
    gpsd.stream(gps.WATCH_ENABLE|gps.WATCH_NEWSTYLE)


# UDP packet receive thread
def receive_thread():
    global TARGET_ADDR
    global gps_flag
    while True:
        data, addr = receiver.recvfrom(200)
        if addr:
            TARGET_ADDR = addr[0]
        if data:
            if (data[0] == 'M'):
                gps_thread.stop()
                arduino.write(data)
                # print data
            elif (data[0] == 'G'):
                # gps_flag = True
                if gps_flag:
                    gps_thread.set_destination(data)
                    gps_thread.resume()
                else:
                    sender.sendto(bytes("GN"), (TARGET_ADDR, 2049))


# UDP packet send thread
def send_thread():
    global gps_flag
    while True:
        msg = ""
        try :
            report = gpsd.next()
            if report["class"] == "TPV" and TARGET_ADDR != "":
                if hasattr(report, "epx") and hasattr(report, "epy"):
                    if report.epx < 20.0 and report.epy < 20.0:
                        t_lat = float(report.lat) * 100000.0 % 10
                        t_lon = float(report.lon) * 100000.0 % 10
                        lat = round(float(report.lat), 4)
                        lon = round(float(report.lon), 4)
                        
                        if t_lat < 2.0:
                            pass
                        elif t_lat < 5.0:
                            lat += 0.00003
                        elif t_lat < 9.0:
                            lat -= 0.00004

                        if t_lon < 2.0:
                            pass
                        elif t_lon < 5.0:
                            lon += 0.00003
                        elif t_lon < 9.0:
                            lon -= 0.00004
                        
                        gps_thread.set_my_location(lat, lon)
                        gps_flag = True
                        msg = str(lat) + "," + str(lon)
                        sender.sendto(bytes(msg), (TARGET_ADDR, 2049))
                    else:
                        sender.sendto(bytes("GE"), (TARGET_ADDR, 2049))

        except socket.error as msg:
            print "Error Code : ", str(msg[0]), " Message ", msg[1]
            sys.exit()
        time.sleep(1)


# gps drive class
class gps_drive_class(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.flag = False
        self.my_lat = 0.0
        self.my_lon = 0.0
        self.latlon_list = []
        self.latlon_index = 0

    def run(self):
        while True:
            if self.flag:
                # while until move to destination
                while self.flag:
                    # find my latitude, longitude
                    # self.set_my_location(37.010852, 127.26433) # for test
                    self.lat1, self.lon1 = self.get_my_location()
                    arduino.write("M8100")
                    time.sleep(5)
                    # self.set_my_location(37.01071, 127.26417) # for test
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
                    # print "destination lat :", self.des_lat, "lon :", self.des_lon
                    print "destination bearing :", self.des_bearing
                    # print "direction :", self.direction
                    print "calc_bearing :", self.calc_bearing


                    # motor control
                    arduino.write("MS")
                    time.sleep(0.1)
                    if self.flag:
                        self.motor_turn(self.direction, self.calc_bearing)

                    # check arrive
                    if self.flag and self.is_arrive():
                        if self.latlon_index == len(self.latlon_list)-1:
                            print "arrived!"
                            self.stop()
                        else:
                            self.latlon_index = self.latlon_index + 1

            time.sleep(0.5)

    def resume(self):
        self.flag = True

    def stop(self):
        self.flag = False


    # check arrive
    def is_arrive(self):
        self.my_lat, self.my_lon = self.get_my_location()
        self.des_lat, self.des_lon = self.get_destination()
        self.v_lat = abs(self.my_lat - self.des_lat)
        self.v_lon = abs(self.my_lon - self.des_lon)
        if self.v_lat < 0.0001 and self.v_lon < 0.0001:
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


    # get destination LatLng
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
        if self.temp2 != 0:
            self.temp3 = self.temp1 / self.temp2
            if self.temp3 >= 1:
                self.temp3 = 1
            elif self.temp3 <= -1:
                self.temp3 = -1
            # print "self.temp3 :", self.temp3
            self.bearing = math.acos(self.temp3)
        else:
            self.bearing = 0

        # get true bearing
        if math.sin(self.rlon2 - self.rlon1) < 0.0:
            self.true_bearing = self.bearing * (180 / 3.141592)
            self.true_bearing = self.true_bearing * -1.0
        else:
            self.true_bearing = self.bearing * (180 / 3.141592)

        return self.true_bearing


    # get destination direction
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
        if bearing < 1.0:
            print "straight"
        else:
            if direction == 1:
                print "turn right"
                arduino.write("M6100")
            elif direction == -1:
                print "turn left"
                arduino.write("M4100")


        print bearing * TIME_PER_BEARING, "seconds"
        time.sleep(bearing * TIME_PER_BEARING + 0.05)
        arduino.write("MS")
        time.sleep(0.05)




### main logic
init()
gps_thread.start()
receiver_thread.start()
sender_thread.start()



