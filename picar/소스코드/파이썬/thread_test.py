import threading

def gps_run():
    print("gps_run")

gps_thread = threading.Thread(target=gps_run)
gps_thread.start()
gps_thread.join()
