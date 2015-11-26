package com.example.mymessenger;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
// 회원가입
public class JoinActiviity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitvity_join);
	}
// 가입버튼을 클릭했을때
	public void BtnClickJoin(View v)
	{
		//Move to  LoginPage
		//Get ID
		final EditText ideditText = (EditText) findViewById(R.id.joineditText1);
		//Get PW
		final EditText pweditText = (EditText) findViewById(R.id.joineditText2);
		final EditText nmeditText = (EditText) findViewById(R.id.joineditText3);
		//가입하기 위해 호출
		sendPostRequest(ideditText.getText().toString(), pweditText.getText().toString(),nmeditText.getText().toString());

		//Soap사용준비
	}
//비동기로 소켓통신
	private void sendPostRequest(String user_id,   String user_pwd, String user_name) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
			//통신 시작전 UI에서 처리해야할 일
			@Override
			protected void onPreExecute() {
				//없으므로 그냥 패스
			}

			//서버와 통신기능
			@Override
			protected String doInBackground(String... params) {
				String URL = "http://projectjo.iptime.org/WebMethod.asmx";
				String namespace = "http://tempuri.org/";
				String methodName = "Join";
				String soapAction = "http://tempuri.org/Join";
				//실행할 때 넣은 파라미터 받아옴
				String user_id = params[0];
				String user_pwd = params[1];
				String user_name = params[2];
				//매개변수 추가
				SoapObject request = new SoapObject(namespace, methodName);
				request.addProperty("id", user_id);
				request.addProperty("pw", user_pwd);
				request.addProperty("name", user_name);

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
					str  = result.toString();
					if (str.equals("true")) {
						return str;
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(JoinActiviity.this, e.toString(), Toast.LENGTH_LONG).show();
				}
				return str;
			}
			//종료될 때 호출됨
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if(result.equals("true")){
					Toast.makeText(JoinActiviity.this, "Success", Toast.LENGTH_LONG).show();
				finish();}
				else
				{
					Toast.makeText(JoinActiviity.this, "Failed", Toast.LENGTH_LONG).show();
				}
			}/// onPostExecute

		}
		SendPostReqAsyncTask sendTask = new SendPostReqAsyncTask();
		sendTask.execute(user_id, user_pwd,user_name);     // 비동기 방식 백그라운드로 받아 오기
	}
}
