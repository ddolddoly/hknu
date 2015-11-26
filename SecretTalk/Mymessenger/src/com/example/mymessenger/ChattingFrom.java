package com.example.mymessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
//1:1 채팅에 사용된다.
public class ChattingFrom extends Activity {

	ArrayList<String> arrmme;
	ArrayList<String> msg;
	String uuid;
	String myid;
	HubConnection mConnection;
	HubProxy mHub;
	//대화목록
	ChattingAdapter chattadap;
	ArrayList<ChattingData> chiiarr;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chattingfrom);

	    // TODO Auto-generated method stub
		//시작 할 때 데이터를 받아온다.
		Intent intent = getIntent();
		myid = intent.getExtras().getString("myid");
		Bundle b = getIntent().getExtras();
		msg = b.getStringArrayList("MSGList");
		arrmme = b.getStringArrayList("MemList");
		uuid =  intent.getExtras().getString("UUID");
		EditText et = (EditText)findViewById(R.id.cfeditText1);

		// 1:N 채팅 리스트셋팅

		chiiarr = new ArrayList<ChattingData>();
		ListView chlistview = (ListView)findViewById(R.id.chattlv2);
		chattadap = new ChattingAdapter(ChattingFrom.this,chiiarr,myid);
		chattadap.SetContext(this);
		chlistview.setAdapter(chattadap);

		//메세지 출력
		for(int i =0 ;i<msg.size();i++)
		{
			et.setText(et.getText().toString() + msg.get(i) + "\n");
		}
		//소켓 셋팅
		try {
			// 메인에 소켓을 가져온다.
			HubConnectionFactory hf = HubConnectionFactory.Instance(myid);
			mConnection =hf.getConn();
			mHub = hf.getProxy();
			//메세지 받을 준비
			prepareGetMessage();
		} catch (Exception e) {
			Log.e("SignalR", "Failed to send message");
		}
	}

	public  void BtnSearch(View v)
	{
		final EditText et = (EditText) findViewById(R.id.cfeditText1);
		//암호화해서 메세지를 보낸다.
		String pw =SetMyId.getRandomString();
		String msg = SetMyId.encryte(et.getText().toString(),pw);
		sendMessage(myid, msg, arrmme.get(1),pw);
		et.setText(null);

	}

	//Get Server Message
	private void prepareGetMessage() {

		mHub.on("encrymsgfrom1",  new SubscriptionHandler4<String, String, String,String>(){
			@Override
			public void run(final String s1, final String s2, final String s3,final String s4) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						String str = SetMyId.decryte(s2,s4);
						ListView chlistview = (ListView)findViewById(R.id.nchattlv);

						ChattingData cd = new ChattingData(s1,str);
						chiiarr.add(cd);
						chattadap.notifyDataSetChanged();


					}
				});
			}
		}, String.class, String.class, String.class,String.class);
	}

//메세지 전송
	private void sendMessage(String id,String pw,String youid,String key) {
		try {
			mHub.invoke("sendencryte1", id,pw,youid,key).get();
		} catch( Exception e ){
			Log.e("SignalR", "Fail to send message");
		}
	}

}
