package com.rotk.eggplantcars;





import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import entity.Address;
import entity.Page;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
//我的地址页面
public class MyAddress extends Activity{

	ImageButton btnGoback;
	ImageButton addAddress;
	ListView addressList;
	User user;
	
	List<Address> data;
	int page = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		btnGoback=(ImageButton) findViewById(R.id.goback);
		addAddress=(ImageButton) findViewById(R.id.add_Address);
		addressList=(ListView) findViewById(R.id.show_address);
		addressList.setDividerHeight(0);
		addressList.setAdapter(addressAdapter);
		user = (User) getIntent().getSerializableExtra("user");
		btnGoback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		addAddress.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加地址
				Intent intent = new Intent(MyAddress.this,AddAddress.class);
				intent.putExtra("user", user);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadAddress();
	}
	


	private void loadAddress() {
		Request request = Server.requestBuilderWithApi("getaddress")
				.get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<Address> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<Page<Address>>() {
							});
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							MyAddress.this.page=data.getNumber();
							MyAddress.this.data=data.getContent();
							addressAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (Exception e) {
					Log.d("akloadAddress", e.getMessage());
				}
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.d("AkloadAddress", arg1.getMessage());
			}
		});
		
	}
	
	//删除收货地址
	private void delAddress(String id){
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("addressIdString", id)
				.build();
		Request request = Server.requestBuilderWithApi("address/del")
				.post(body).build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					final Page<Address> dataDel = new ObjectMapper().readValue(arg1.body().string(), 
							new TypeReference<Page<Address>>() {
							});
					MyAddress.this.page = dataDel.getNumber();
					MyAddress.this.data = dataDel.getContent();
					addressAdapter.notifyDataSetInvalidated();
				} catch (Exception e) {
					Log.d("delADD", e.getMessage());
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.d("2929", arg1.getMessage());
			}
		});
				
	}
	
	
	BaseAdapter addressAdapter = new BaseAdapter() {
		
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.eaxmple_addredd, null);
			}else{
				view = convertView;
			}
			TextView name = (TextView) view.findViewById(R.id.user_name);
			TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
			TextView address = (TextView) view.findViewById(R.id.address);
			Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
			Button btnDel = (Button) view.findViewById(R.id.btn_del);
			
			final Address adrs = data.get(position);
			name.setText("收货人："+adrs.getName());
			phoneNum.setText("电话/手机："+adrs.getPhoneNumber());
			address.setText("地址："+adrs.getText());
			
			//btn.setOnc!!!!
			btnDel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					delAddress(adrs.getId()+"");
				}
			});
			
			btnUpdate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent itnt = new Intent(MyAddress.this,MyAddressUpdate.class);
					itnt.putExtra("address",adrs);
					startActivity(itnt);
				}
			});
			
			
			
			
			
			return view;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data == null ? 0 : data.size();
		}
	};
	
	
	
	

	
	
	
	
	
	
	
	
}
