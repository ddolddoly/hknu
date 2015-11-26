package com.example.mymessenger;


import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.InvalidStateException;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.NullLogger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.SimpleEntry;
import microsoft.aspnet.signalr.client.http.CookieCredentials;
import microsoft.aspnet.signalr.client.http.HttpConnection;
import microsoft.aspnet.signalr.client.http.HttpConnectionFuture;
import microsoft.aspnet.signalr.client.http.HttpConnectionFuture.ResponseCallback;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.Response;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

import android.util.Log;
import android.widget.Toast;
//시그널r 커넥트를 공통으로 사용하기위한 디자인패턴
public class HubConnectionFactory {

    private static HubConnectionFactory hubfac;
    public HubConnectionFactory(String id){
        hubfac = this;
    }
    public static HubConnectionFactory Instance(String id) {
        if(hubfac == null)
        {
            hubfac = new HubConnectionFactory(id);
        }
        return hubfac;}

    HubConnection mConnection;
    HubProxy mHub;
    String id;

    public HubConnection getConn()
    {
        return mConnection;
    }
    public HubProxy getProxy(){

        return mHub;
    }

    public void initialize(String id) {
        String serverUrl = "http://projectjo.iptime.org";
        //String serverUrl = "http://localhost:2408";
        String hubName = "ChatHub";

        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        mConnection = new HubConnection(serverUrl);
        mHub = mConnection.createHubProxy(hubName);
    }
    public void connectToServer() {
        try {
            SignalRFuture<Void> awaitConnection = mConnection.start();
            awaitConnection.get();
            //Toast.makeText(this, "Connected SignalR", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.e("SignalR", "Failed to connect to server");
        }
    }
}
