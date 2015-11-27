package com.phone.ignore.constants;

public class Schedule {

    public String strTitle;
    public String strDate;
    public String strStartTime;
    public String strEndTime;
    public long regTime;

    public Schedule(String strTitle, String strDate, String strStartTime, String strEndTime, long regTime) {
        this.strTitle = strTitle;
        this.strDate = strDate;
        this.strStartTime = strStartTime;
        this.strEndTime = strEndTime;
        this.regTime = regTime;
    }
}
