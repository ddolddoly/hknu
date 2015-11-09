import socket
PORT = 2048
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind(("", PORT))
print("waiting on port : ", PORT)
while True :
    data, addr = s.recvfrom(1024)
    if data :
        print("data is : ", data)
        print("data is : ", data.decode())
