package com.rotk.eggplantcars;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.Server;
import entity.Address;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyAddressUpdate extends Activity{

	EditText name;		//重设名字
	EditText phoneNum;	//reset
	EditText city;		//reset
	EditText addressDetail;	//reset
	Button btnAddress;	//reset
	Address address;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addaddress);
		name = (EditText) findViewById(R.id.edit_name);
		phoneNum = (EditText) findViewById(R.id.edit_phonenum);
		city = (EditText) findViewById(R.id.edit_city);
		addressDetail = (EditText) findViewById(R.id.edit_address);
		btnAddress = (Button) findViewById(R.id.btn_address_submit);
		
		address = (Address) getIntent().getSerializableExtra("address");
		name.setText(address.getName());
		phoneNum.setText(address.getPhoneNumber());
		
		
		
		findViewById(R.id.goback).setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateAddress();
				overridePendingTransition(0, R.anim.slide_out_bottom);
			}
		});
	}

	protected void updateAddress() {
		// TODO Auto-generated method stub
		String nameText = (name.getText() + "").toString();
		if (nameText.length() == 0) {
			Toast.makeText(this, "收件人为空，难道要送给我", Toast.LENGTH_SHORT).show();
			return;
		}
		String phoneNumText = (phoneNum.getText() + "").toString();
		if (phoneNumText.length() == 0) {
			Toast.makeText(this, "留个电话交个朋友呗", Toast.LENGTH_SHORT).show();
			return;
		}
		String cityText = (city.getText() + "").toString();
		if (cityText.length() == 0) {
			Toast.makeText(this, "五湖四海的朋友啊报上来头", Toast.LENGTH_SHORT).show();
			return;
		}
		String addressDetailText = (addressDetail.getText() + "").toString();
		if (addressDetailText.length() == 0) {
			Toast.makeText(this, "我就在你方圆几里确见不到你的身影", Toast.LENGTH_SHORT).show();
			return;
		}
		
		MultipartBody body  = new MultipartBody.Builder()
				.addFormDataPart("name", nameText)
				.addFormDataPart("phoneNumber", phoneNumText)
				.addFormDataPart("text", cityText+" "+addressDetailText)
				.addFormDataPart("addressIdString", address.getId()+"")
				.build();
		Request request = Server.requestBuilderWithApi("address/update")
				.post(body)
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String rString = arg1.body().string();
				Log.d("AddUpdate", rString);
				finish();
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.d("updateworng", arg1.getMessage());
			}
		});
				
	}
}
