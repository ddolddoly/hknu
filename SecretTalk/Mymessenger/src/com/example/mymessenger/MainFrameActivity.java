package com.example.mymessenger;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler4;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
@SuppressWarnings("deprecation")
public class MainFrameActivity extends TabActivity {
	final static int ACT_EDIT = 0;
	HubConnection mConnection;
	HubProxy mHub;
	Intent Chattingintent;
	TabHost mTab;
	String myid;


	//대화목록
	ChattingAdapter chattadap;
	ArrayList<ChattingData> chiiarr;
	//1:N 리스트뷰 셋팅
	CusromAdapter csapater;
	ArrayList<dataTest> dataArray;

//친구목록
	ListView listmem;
	ArrayList<String> arraylist;
	ArrayAdapter<String>aa;
	boolean isServer = false; //친구가 추가했을때 확읺용
	public static Context m_con;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.mainframe);
		// TODO Auto-generated method stub
		m_con= this;
		mTab = getTabHost();

		LayoutInflater inflater = LayoutInflater.from(this);
		inflater.inflate(R.layout.mainframe, mTab.getTabContentView(), true);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		Intent intent = getIntent();
		myid = intent.getExtras().getString("user_id");
		SetMyId sm = SetMyId.Instance();
		sm.SetID(myid);

		//시그널r셋팅
		try {
			HubConnectionFactory hf = HubConnectionFactory.Instance(myid);
			hf.initialize(myid);
			mConnection =hf.getConn();
			mHub = hf.getProxy();
			//initialize(myid);
			prepareGetMessage();
			hf.connectToServer();

			//connectToServer();
			mHub.invoke("myconn", myid).get();

		} catch (Exception e) {
			Log.e("SignalR", "Fail to send message");
		}


		//친구목록 리스트뷰 셋팅
		listmem = (ListView) findViewById(R.id.lv);
		arraylist = new ArrayList<String>();
		aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arraylist);
		listmem.setAdapter(aa);


		//채팅 리스트 텝 리스트뷰 셋팅 (커스텀 리스트뷰)
		dataArray = new ArrayList<dataTest>();
		ListView lvchet = (ListView)findViewById(R.id.chattlv);
		csapater= new CusromAdapter(this, dataArray,myid);
		csapater.SetContext(this);
		lvchet.setAdapter(csapater);
		// 1:N 채팅 리스트셋팅

		chiiarr = new ArrayList<ChattingData>();
		ListView chlistview = (ListView)findViewById(R.id.nchattlv);
		chattadap = new ChattingAdapter(this,chiiarr,myid);
		chattadap.SetContext(this);
		chlistview.setAdapter(chattadap);


		//탭셋팅
		mTab.addTab(mTab.newTabSpec("flist").setIndicator("Freind").setContent(R.id.friendlist));

		mTab.addTab(mTab.newTabSpec("clist").setIndicator("Chatting").setContent(R.id.chatting));

		mTab.addTab(mTab.newTabSpec("nclist").setIndicator("NChatting").setContent(R.id.nchatsend));


		//서버에서 친구목록 불러오기
		GetMyFriend(myid);

		Button bt = (Button)findViewById(R.id.btnfSearchf);
		//리스트뷰 롱 클릭시 1:1 채팅창 만들어줌
		listmem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				String str = parent.getItemAtPosition(position).toString();
				//커스텀 리스트뷰에 값을 넣어준다.
                ArrayList<String> user = new ArrayList<String>();
                user.add(myid);
                user.add(str);
                ArrayList<String> msg = new ArrayList<String>();

                dataTest ds = new dataTest(user, msg);
                dataArray.add(ds);
                csapater.notifyDataSetChanged();

				return true;
			}
		});

	}

	@Override
	protected void onDestroy() {
		mConnection.stop();
		super.onDestroy();
	}

	// 친구 검색 버튼
	public void BtnSearchF(View v)
	{
		Intent intent = new Intent().setClass(this,Search.class);
		startActivityForResult(intent, ACT_EDIT);
	}
	//Get Server Message 서버에서 메세지가 오는지 확인
	private void prepareGetMessage() {
		//전체 대화
		mHub.on("encrymsgfrom", new SubscriptionHandler3<String, String,String>() {
			@Override
			public void run(final String msg, final String obj,final String key) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//
						String str = SetMyId.decryte(obj,key);

						ListView chlistview = (ListView)findViewById(R.id.nchattlv);

						ChattingData cd = new ChattingData(msg,str);
						chiiarr.add(cd);
						chattadap.notifyDataSetChanged();
					}
				});
			}
		}, String.class, String.class,String.class);
//상대방이 친구추가시
		mHub.on("broadcastMessages1", new SubscriptionHandler3<String, String, String>() {
			@Override
			public void run(final String s1, final String s2, final String s3) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//
						Toast.makeText(MainFrameActivity.this, s1 + "가 친구를 추가했습니다.", Toast.LENGTH_LONG).show();
						//s1 이 친구아이디
						OnAdd(myid, s1);
					}
				});
			}
		}, String.class, String.class, String.class);

	}

		//1:N 채팅 전송
	private void send(String id,String msg,String pw) {
		try {
			mHub.invoke("sendpw", id,msg,pw).get();
		} catch( Exception e ){
			Log.e("SignalR", "Fail to send message");
		}
	}
	//친구를 추가하였다고 전송
	private void sendfriend(String id,String msg,String fid) {
		try {
			mHub.invoke("sendtoid", id,msg,fid).get();
		} catch( Exception e ){
			Log.e("SignalR", "Fail to send message");
		}
	}

	//1:N 메세지보내기
	public void BtnNSendClick(View v)
	{
		final EditText nmeditText = (EditText) findViewById(R.id.ncfeditText1);
		String key =SetMyId.getRandomString();
		String msg = SetMyId.encryte(nmeditText.getText().toString(),key);
		String id = myid;
		send(id, msg,key);
        nmeditText.setText(null);
		//arraylist.add("Test111");
		//aa.notifyDataSetChanged();
	}

	/////상대방이 친구 추가시 나도 친구추가 현재는 나와상대둘다 로그인이 돼어있어야함
	private void OnAdd(final String user_id ,String fid) {

				String URL = "http://projectjo.iptime.org/WebMethod.asmx";
				String namespace = "http://tempuri.org/";
				String methodName = "AddFriend";
				String soapAction = "http://tempuri.org/AddFriend";

				//매개변수 추가
				SoapObject request = new SoapObject(namespace, methodName);
				request.addProperty("myid", user_id);
				request.addProperty("fid", fid);


				//호출 준비
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				envelope.dotNet = true;

				HttpTransportSE android = new HttpTransportSE(URL);
				String str = "";
				//웹서비스 호출
				try {
					android.call(soapAction, envelope);
					SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
					str = result.toString();
					if (str.equals("true")) {

						arraylist.add(fid);
				aa.notifyDataSetChanged();
				SetMyId sn = SetMyId.Instance();
				if (sn.GetIsServer()) {
					sn.SetIsServer(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//로그인시 친구리스트
	private void GetMyFriend(String myidt) {

		//리스트뷰 셋팅

		String URL = "http://projectjo.iptime.org/WebMethod.asmx";
		String namespace = "http://tempuri.org/";
		String methodName = "GetFriend";
		String soapAction = "http://tempuri.org/GetFriend";

		//매개변수 추가
		try {
			SoapObject request = new SoapObject(namespace, methodName);
			request.addProperty("myid", myid);


			//호출 준비
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			envelope.dotNet = true;

			HttpTransportSE android = new HttpTransportSE(URL);

			//웹서비스 호출

			android.call(soapAction, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;

			for (int i = 0; i < result.getPropertyCount(); i++) {
				SoapObject data = (SoapObject)
						result.getProperty(i);
				for (int j = 0; j < data.getPropertyCount(); j++) {
					String id = data.getProperty(j).toString();
					if(!id.equals("anyType{}"))
						arraylist.add(0, data.getProperty(j).toString());

				}
			}
			aa.notifyDataSetChanged();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//친구 검색 창(인텐트간 통신에서 리턴한 값)
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
			case ACT_EDIT:
				if (resultCode == RESULT_OK) {
					String id =  data.getStringExtra("Addid");
					arraylist.add(id);
					aa.notifyDataSetChanged();
				}
				break;
		}
	}

}
