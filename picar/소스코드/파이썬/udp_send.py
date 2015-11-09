import socket
import sys

PORT = 2048
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind(("", PORT))
print("waiting on port : ", PORT)

while True :
    msg = str(input('Enter message to send : '))
     
    try :
        #Set the whole string
        s.sendto(bytes(msg, "utf8"), ("192.168.10.101", 2049))
        
        # receive data from client (data, addr)
        #d = s.recvfrom(1024)
        #reply = d[0]
        #addr = d[1]
         
        #print('Server reply : ', reply)
     
    except socket.error as msg:
        print('Error Code : ', str(msg[0]), ' Message ', msg[1])
        sys.exit()
