package com.example.mymessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
//친구검색 화면
public class Search extends Activity {
    String myid;
    HubConnection mConnection;
    HubProxy mHub;
    ListView lvsearch;
    ArrayList<String> aaa;
    ArrayAdapter<String> saeachadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

//내 아이디 셋팅
        SetMyId sm = SetMyId.Instance();
        myid = sm.GetId();
        //시그널r셋팅
        try {
            HubConnectionFactory hf = HubConnectionFactory.Instance(myid);
            hf.initialize(myid);
            //디자인패턴에서conet받아옴
            mConnection =hf.getConn();
            mHub = hf.getProxy();
            //서버에서 메세지받을준비
            prepareGetMessage();
            hf.connectToServer();

            mHub.invoke("myconn", myid).get();

        } catch (Exception e) {
            Log.e("SignalR", "Fail to send message");
        }


        //검색한 친구목록 리스트뷰셋팅
        lvsearch = (ListView) findViewById(R.id.lvsearch);
        aaa = new ArrayList<String>();
        saeachadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aaa);
        lvsearch.setAdapter(saeachadapter);


        //검색한 친구리스트 찾을 시 클릭하면 edittxt에 아이디 써줌
        lvsearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();

                EditText et = (EditText) findViewById(R.id.editText1);
                et.setText(str);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mConnection.stop();
        super.onDestroy();
    }
//서버에서 함수가 호출되는지
    private void prepareGetMessage() {

        mHub.on("broadcastMessage", new SubscriptionHandler2<String, String>() {
            @Override
            public void run(final String msg, final String obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        String str = obj;
                        //Toast.makeText(MainFrameActivity.this, str, Toast.LENGTH_LONG).show();
                        TextView et = (TextView) findViewById(R.id.ncfeditText1);
                        et.setText(et.getText().toString() + "msg : " + str + "\n");
                    }
                });
            }
        }, String.class, String.class);
    }

//추가 버튼 눌럿을시
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
                    sendfriend(myid, "친구를 추가하였습니다.", fid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //친구를 추가하였다고 전송
    private void sendfriend(String id,String msg,String fid) {
        try {
            mHub.invoke("sendtoid", id,msg,fid).get();
            mHub.invoke("onMyAdd", id).get();
        } catch( Exception e ){
            Log.e("SignalR", "Fail to send message");
        }
    }

//친구추가 버튼 눌렀을 때
    public void BtnAdd(View v)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stu
                final EditText nmeditText = (EditText) findViewById(R.id.editText1);
                OnAdd(myid, nmeditText.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("Addid", nmeditText.getText().toString());
                //메인엑티비티로 결과를 리턴(resultCode와 intent)
                //결과코드: RESULT_OK,RESULT_CANCELED
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    // 검색 버튼
    public void BtnSearch(View v)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stu
                EditText nmeditText = (EditText) findViewById(R.id.editText1);
                FindID(myid, nmeditText.getText().toString());
            }
        });
    }
    //친구 찾기
    private void FindID(final String user_id ,final String fid) {

        String URL = "http://projectjo.iptime.org/WebMethod.asmx";
        String namespace = "http://tempuri.org/";
        String methodName = "SearchFriend";
        String soapAction = "http://tempuri.org/SearchFriend";


        //매개변수 추가
        SoapObject request = new SoapObject(namespace, methodName);
        request.addProperty("myid", user_id);
        request.addProperty("search", fid);


        //호출 준비
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE android = new HttpTransportSE(URL);
        String str = "";
        //웹서비스 호출
        try {
            android.call(soapAction, envelope);

            SoapObject result = (SoapObject) envelope.bodyIn;
            aaa.clear();
//			saeachadapter.notifyDataSetChanged();
            for (int i = 0; i < result.getPropertyCount(); i++) {
                SoapObject data = (SoapObject) result.getProperty(i);
                for (int j = 0; j < data.getPropertyCount(); j++) {
                    String id = data.getProperty(j).toString();
                    aaa.add(0,id);
                }
            }
            saeachadapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
