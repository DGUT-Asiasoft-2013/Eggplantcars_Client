package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import entity.Page;
import entity.User;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class PriLatterUser extends Activity{
	int page = 0;
	List<User> data;
	ListView userList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privatelatter);
		userList= (ListView) findViewById(R.id.latter_list);
		userList.setAdapter(userAdapter);
		userList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onItemClicked(position);
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadUser();
	}
	
	void loadUser(){
		Request request = Server.requestBuilderWithApi("alluser").get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
				final Page<User> data = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<entity.User>>() {
				});
				runOnUiThread(new Runnable() {				
					@Override
					public void run() {
						PriLatterUser.this.page=data.getNumber();
						PriLatterUser.this.data = data.getContent();
						userAdapter.notifyDataSetInvalidated();
					}
				});
				}catch(final Exception exception){
					runOnUiThread(new Runnable() {		
						@Override
						public void run() {
							Toast.makeText(PriLatterUser.this,exception.getMessage(),
								     Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
							Toast.makeText(PriLatterUser.this,arg1.getMessage(),
									Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	

	
	void onItemClicked(int position){
		User receiver = data.get(position);
		Intent intent = new Intent(this,ShowPrivateLatter.class);
		intent.putExtra("receiver", receiver);
		startActivity(intent);
	}
	
	
	BaseAdapter userAdapter = new BaseAdapter() {
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView == null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.example_userlist, null);
			}else{
				view = convertView;
			}
			AvatarNewsView userImg = (AvatarNewsView) view.findViewById(R.id.user_img);
			TextView userName = (TextView) view.findViewById(R.id.user_name);
			User user = data.get(position);
			Log.d("name", user.getName());		
			userName.setText(user.getName()+"");
			userImg.load(Server.serverAddress+user.getAvatar());
			
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
