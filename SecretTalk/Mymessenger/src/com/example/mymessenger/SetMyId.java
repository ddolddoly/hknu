package com.example.mymessenger;

import android.os.Build;
import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//Static class 아이디를 저장하기위해서, 공통적으로 사용되는 함수 선언
public class SetMyId{
    private static SetMyId sm;

    private String m_id;
    private boolean isServer = false;
    public SetMyId(){sm = this;}
    public static SetMyId Instance() {
        if(sm == null)
        {
            sm = new SetMyId();
        }
        return sm;}

    public void SetID(String id)
    {m_id = id;}
    public String GetId()
    {return m_id;}
    public boolean GetIsServer()
    {return isServer;}
    public void SetIsServer(boolean b)
    {isServer =b;}


    // 기존암호화방식
    //암호화
 /*   public  static String ConvertChar(String msg)
    {
        char[] c = msg.toCharArray();
        char[] cover = new char[msg.length()];
        for(int i =0;i<c.length;i++)
        {
            cover[i] =(char)( c[i] + 60);
        }
        String str = new String(cover);
        return str;
    }
    //복호화
    public static String PassingChar(String msg)
    {
        char[] c = msg.toCharArray();
        char[] cover = new char[msg.length()];
        for(int i =0;i<c.length;i++)
        {
            cover[i] =(char)( c[i] - 60);
        }
        String str = new String(cover);
        return str;
    }*/


//AES방식을 이용한 암호화방식
    public static String btyeArrayToHex(byte[] buf)
    {
        if(buf == null || buf.length ==0)
            return null;

        StringBuffer sb = new StringBuffer(buf.length*2);
        String hexNumber;
        for(int x =0; x <buf.length;x++) {

            hexNumber = "0" + Integer.toHexString(0xff&buf[x]);
            sb.append(hexNumber.substring(hexNumber.length()-2));
        }
        return sb.toString();
    }
    public static byte[]hexToByteArray(String hex)
    {
        if(hex ==null  || hex.length() ==0) return null;

        byte[] ba = new byte[hex.length()/2];

        for(int i =0 ;i<ba.length;i++){
            ba[i] = (byte) Integer.parseInt(hex.substring(2*i,2*i+2),16);
        }
        return ba;
    }

    //private static final String PASSWORD = "fe8025475de7cd55"; //고정암호 현재사용x
    //암호화
    public static String encryte(String msg ,String pas)
    {
        try {


            SecretKeySpec skeys = new SecretKeySpec(pas.getBytes(), "AES");
            Cipher cip = Cipher.getInstance("AES");
            cip.init(cip.ENCRYPT_MODE, skeys);

            byte[] encrypted = cip.doFinal(msg.getBytes());
            return btyeArrayToHex(encrypted);
        }
        catch (Exception e)
        {

        }
        return null;
    }
    //복호화
    public static String decryte(String msg, String pas)
    {
        String orStr="";
        try
        {
            SecretKeySpec skeys = new SecretKeySpec(pas.getBytes(),"AES");
            Cipher cip = Cipher.getInstance("AES");
            cip.init(cip.DECRYPT_MODE,skeys);

            byte[] origin = cip.doFinal(hexToByteArray(msg));
            orStr = new String(origin,0,origin.length);
            return orStr;

        }
        catch (Exception e)
        {
            Log.i("복호화실패" ,e.toString());
        }
        return orStr;
    }

    //랜덤암호만들기
    public static String getRandomString()
    {
        int length= 16;
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] ="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,1,2,3,4,5,6,7,8,9,0".split(",");


        for (int i=0 ; i<length ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

}

