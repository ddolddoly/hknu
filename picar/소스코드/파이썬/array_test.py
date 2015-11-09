s = "G37.123123123,127.123123123/37.4567899,127.4567899/"
print s

sa = s[1:].split("/")
print sa
print len(sa)



gps_list = []
for i in range(len(sa)-1):
    a = []
    a.append(round(float(sa[i].split(",")[0]), 5))
    a.append(round(float(sa[i].split(",")[1]), 5))
    gps_list.append(a)
    print gps_list
