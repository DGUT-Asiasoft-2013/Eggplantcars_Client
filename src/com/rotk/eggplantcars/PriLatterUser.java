package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.cloudage.membercenter.entity.PrivateLatter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jauker.widget.BadgeView;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import entity.Page;
import entity.User;
import inputcells.AvatarNewsView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;


public class PriLatterUser extends Activity{
	int page = 0;
	List<User> data;
	ListView userList;
	private BadgeView counterUnreadBadge;
	
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
	
	
	interface getCountCallback{
		void onResult(int position, int count);
	}
	
	//获取未读信息条数
	void getCount(final int position,final getCountCallback callback){
		
		Request request = Server.requestBuilderWithApi("unread/"+data.get(position).getId()).get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final int countUnread = new ObjectMapper().readValue(arg1.body().string(),Integer.class );
					runOnUiThread(new Runnable() {					
						@Override
						public void run() {
							callback.onResult(position, countUnread);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				Log.d("fox", arg1.getMessage());
			}
		});
	}
	
	
	
	
	//加载用户列表
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
		changeUnread(receiver);
		Intent intent = new Intent(this,ShowPrivateLatter.class);
		intent.putExtra("receiver", receiver);
		startActivity(intent);
	}
	
	
	//修改未读消息为已读    post
	private void changeUnread(User receiver) {
		// TODO Auto-generated method stub
		
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("senderIdString",receiver.getId())
				.build();
		Request request = Server.requestBuilderWithApi("unread/update")
				.post(body)
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				final String responseBody = arg1.body().string();
				Log.d("changeUnreadd", responseBody);
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.d("failchangeUnread", arg1.getMessage());
			}
		});
	}



	
	
	//数据适配器
	BaseAdapter userAdapter = new BaseAdapter() {
		
		
		@SuppressLint({ "InflateParams", "RtlHardcoded" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				convertView = inflater.inflate(R.layout.example_userlist, null);
				holder = new ViewHolder();
				holder.img = (AvatarNewsView) convertView.findViewById(R.id.user_img);
				holder.textView = (TextView) convertView.findViewById(R.id.user_name);
				holder.badgeView = new BadgeView(parent.getContext());
				holder.badgeView.setTargetView(holder.textView);
				holder.badgeView.setBadgeGravity(Gravity.CENTER_VERTICAL | Gravity.END);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
//			AvatarNewsView userImg = (AvatarNewsView) view.findViewById(R.id.user_img);
//			TextView userName = (TextView) view.findViewById(R.id.user_name);
//			User user = data.get(position);
//			Log.d("name", user.getName());		
//			userName.setText(user.getName()+"");
//			userImg.load(Server.serverAddress+user.getAvatar());
			User user = data.get(position);			
			holder.textView.setText(user.getName()+"");
			holder.img.load(Server.serverAddress+user.getAvatar());
			
			holder.badgeView.setBadgeCount(0);
			PriLatterUser.this.getCount(position, new getCountCallback() {
				@Override
				public void onResult(int resultPosition, int count) {
					if(resultPosition == position)
						holder.badgeView.setBadgeCount(count);
				}
			});
			
			return convertView;
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
		
		class ViewHolder{
			TextView textView;
			BadgeView badgeView;
			AvatarNewsView img;
		}
		
	};
	
}
