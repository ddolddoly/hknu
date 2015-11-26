package com.example.mymessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

//로그인 화면
public class LoginActivity extends Activity {
	Intent mainintent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mainintent = new Intent(this,MainFrameActivity.class);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//Button Join Click
	public void BtnClickJoin(View v)
	{
		Intent intent = new Intent(this,JoinActiviity.class);
		startActivity(intent);
	}
	//LoginBtnClick
	public void BtnClickLogIn(View v)
	{
		//Move to  LoginPage
		//Get ID
		final EditText ideditText = (EditText) findViewById(R.id.LogeditText1);
		//Get PW
		final EditText pweditText = (EditText) findViewById(R.id.LogeditText2);
		sendPostRequest(ideditText.getText().toString(),pweditText.getText().toString());
		//Soap사용준비
	}

	private void sendPostRequest(String user_id,   String user_pwd)
	{
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
			//통신 시작전 UI에서 처리해야할 일
			@Override
			protected void onPreExecute() {
				Toast.makeText(LoginActivity.this,"LogIn .....",Toast.LENGTH_LONG).show();
			}

			//서버와 통신기능
			@Override
			protected String doInBackground(String... params) {

				String URL = "http://projectjo.iptime.org/WebMethod.asmx";
				String namespace = "http://tempuri.org/";
				String methodName = "Login";
				String soapAction = "http://tempuri.org/Login";
				String user_id = params[0];
				String user_pwd = params[1];
				//매개변수 추가
				SoapObject request = new SoapObject(namespace, methodName);
				request.addProperty("id", user_id);
				request.addProperty("pw", user_pwd);

				//호출 준비
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				envelope.dotNet = true;

				HttpTransportSE android = new HttpTransportSE(URL);
				String str ="";
				//웹서비스 호출
				try {
					android.call(soapAction, envelope);
					SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
					str = result.toString();

					if ( str.equals("true")){
						mainintent.putExtra("user_id",user_id);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return str;
			}
			//종료될 때 호출 doInBackground에서 리턴한 값 이 넘어온다.
			@Override
			protected void onPostExecute(String result)
			{
				super.onPostExecute(result);
				if(result.equals("true")) {
					Toast.makeText(LoginActivity.this,"Success",Toast.LENGTH_LONG).show();
					//메인화면 실행
					startActivity(mainintent);
				}
				else
				{
					Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_LONG).show();
				}
				//startActivity(mainintent);
			}/// onPostExecute
			//SendPostReqAsyncTask sendTask = new SendPostReqAsyncTask();
			//sendTask.execute(user_id, user_pwd);     // 비동기 방식 백그라운드로 받아 오기

		}

		///////////////////////////////////
		// 이곳에서 로그인을 위한 웹문서를 비동기 백그라운드로 요청한다.

		SendPostReqAsyncTask sendTask = new SendPostReqAsyncTask();
		sendTask.execute(user_id, user_pwd);     // 비동기 방식 백그라운드로 받아 오기.....

		///////////////////////////////////

	}



}
