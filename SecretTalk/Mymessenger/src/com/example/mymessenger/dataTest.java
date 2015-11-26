package com.example.mymessenger;

import java.util.ArrayList;


//CusromAdapter 에서 사용할 클래스
public class dataTest {
    public ArrayList<String> memberList;
    public ArrayList<String> messageList;

    public dataTest(ArrayList<String> mem, ArrayList<String> msg)
    {
        memberList = mem;
        messageList = msg;

    }
};