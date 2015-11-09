import threading
import gps

gps_thread = None

session = gps.gps("127.0.0.1", "2947")
session.stream(gps.WATCH_ENABLE|gps.WATCH_NEWSTYLE)

def run_gps():
    while True:
        try:
            report = session.next()
            if report['class'] == 'TPV':
                if hasattr(report, 'time'):
                    print report.time
                if hasattr(report, 'lat'):
                    print str(report.lat) + ',' + str(report.lon)
                if hasattr(report, 'lon'):
                    print report.lon
        except KeyboardInterrupt:
            gps_thread.join()
            quit()


gps_thread = threading.Thread(target=run_gps)
gps_thread.start()
gps_thread.join()
