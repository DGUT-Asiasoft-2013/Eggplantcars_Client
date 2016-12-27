package com.rotk.eggplantcars;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.Server;
import entity.Page;
import entity.PrivateLatter;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
//聊天窗口
public class ShowPrivateLatter extends Activity {

	ImageButton latterBtn;//提交消息按钮
	TextView latterName;//跟谁在聊天
	ImageButton latterUserInfo; //点击弹出聊天者信息
	EditText latterInput;//输入框
	ListView latterList;//聊天listview
	int page = 0;
	
	User receiver;
	List<PrivateLatter> data;
	final String latterType[]={"send","receive"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_privatelatter);
		latterBtn=(ImageButton) findViewById(R.id.show_latter_upload);
		latterName = (TextView) findViewById(R.id.show_latter_name);
		latterInput=(EditText) findViewById(R.id.show_latter_input);
		latterList=(ListView) findViewById(R.id.show_latter_list);
		latterList.setDividerHeight(0);
		latterList.setAdapter(latterAdapter);
		receiver = (User) getIntent().getSerializableExtra("receiver");
		latterName.setText(receiver.getName());
		
		
		//提交私信
		latterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendLatter();
				latterInput.setText("");
			}
		});
		//返回
		findViewById(R.id.goback).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				finish();
			}
		});
	}
	
	Handler handler = new Handler();
	boolean isVisible = false;
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isVisible = true;
		dorefresh();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isVisible = false;
	}
	
	
	//刷新信息
	void dorefresh(){
		
		refresh();
		if (isVisible) {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					dorefresh();
				}
			}, 3000);
		}
	}
	
	
	

	//发送私信
	void sendLatter() {
		String latterText = (latterInput.getText()+"").toString();
		if (latterText.length()==0) {
			Toast.makeText(this, "按个空格也可以哦", Toast.LENGTH_SHORT).show();
			return;
		}
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("text", latterText)
				.addFormDataPart("latterType",latterType[0])
				.addFormDataPart("receiverAccount", receiver.getAccount())
				.build();
		Request request = Server.requestBuilderWithApi("privateLatter")
				.post(body)
				.build();
		
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						refresh();
					}
				});
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				
				Log.d("whatbug",arg1.getMessage());
			}
		});
		
	}
	
	
	public void refresh(){
		//获取私信
		Request request = Server.requestBuilderWithApi("getprivateLatter/"+receiver.getId()+"")
				.get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<PrivateLatter> latterdata = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<Page<PrivateLatter>>() {
							});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							ShowPrivateLatter.this.page = latterdata.getNumber();
							ShowPrivateLatter.this.data = latterdata.getContent();
							latterAdapter.notifyDataSetInvalidated();
							
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
					Log.d("receiverId", receiver.getId());
					Log.d("refresh error", e.getMessage());
				}
				
			}
			
			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						new AlertDialog.Builder(ShowPrivateLatter.this).setMessage(arg1.getMessage()).show();
					}
				});
			}
		});
	}
	

	
	
	//数据设配器
	static class ViewHolderSender{
		TextView textsend = null;
		AvatarNewsView sendimg;
		TextView texttime;
	}
	static class ViewHolderReceiver{
		TextView textreceiver = null;
		AvatarNewsView receiverimg;
		TextView texttime;
	}

	
	BaseAdapter latterAdapter = new BaseAdapter() {
		private final int TYPE_ONE=0,TYPE_TWO=1;   //0 sende    1 receiver
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			PrivateLatter latter = data.get(position);
			int type = getItemViewType(position);
			ViewHolderSender holderSender = null;
			ViewHolderReceiver holderReceiver = null;
			if (convertView == null) {
				switch (type) {
				case TYPE_ONE:				//sender
					LayoutInflater inflater = LayoutInflater.from(parent.getContext());
					convertView = inflater.inflate(R.layout.example_sender, null);
					holderSender = new ViewHolderSender();
					holderSender.textsend = (TextView) convertView.findViewById(R.id.sender_text);
					holderSender.textsend.setText(latter.getLatterText());			
					
					holderSender.sendimg = (AvatarNewsView) convertView.findViewById(R.id.author_img);
					holderSender.sendimg.load(Server.serverAddress+latter.getSender().getAvatar());
					String dateStr = DateFormat.format("hh:mm", latter.getCreateDate()).toString();
					holderSender.texttime = (TextView) convertView.findViewById(R.id.time);
					holderSender.texttime.setText(dateStr);
					
					convertView.setTag(holderSender);
					break;
				case TYPE_TWO:
					LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
					convertView = inflater1.inflate(R.layout.example_receiver, null);
					holderReceiver= new ViewHolderReceiver();
					holderReceiver.textreceiver = (TextView) convertView.findViewById(R.id.receiver_text);
					holderReceiver.textreceiver.setText(latter.getLatterText());
					
					holderReceiver.receiverimg = (AvatarNewsView) convertView.findViewById(R.id.receiver_img);
					holderReceiver.receiverimg.load(Server.serverAddress+receiver.getAvatar());
					String dateStr1 = DateFormat.format("hh:mm", latter.getCreateDate()).toString();
					holderReceiver.texttime = (TextView) convertView.findViewById(R.id.time);
					holderReceiver.texttime.setText(dateStr1);
					
					convertView.setTag(holderReceiver);
					break;
				}
			} else {
				switch (type) {
				case TYPE_ONE:
					holderSender = (ViewHolderSender) convertView.getTag();
					holderSender.textsend = (TextView) convertView.findViewById(R.id.sender_text);
					holderSender.textsend.setText(latter.getLatterText());
					
					holderSender.sendimg = (AvatarNewsView) convertView.findViewById(R.id.author_img);
					holderSender.sendimg.load(Server.serverAddress+latter.getSender().getAvatar());
					String dateStr = DateFormat.format("hh:mm", latter.getCreateDate()).toString();
					holderSender.texttime = (TextView) convertView.findViewById(R.id.time);
					holderSender.texttime.setText(dateStr);
					
					break;

				case TYPE_TWO:
					holderReceiver = (ViewHolderReceiver) convertView.getTag();
					holderReceiver.textreceiver = (TextView) convertView.findViewById(R.id.receiver_text);
					holderReceiver.textreceiver.setText(latter.getLatterText());
					
					holderReceiver.receiverimg = (AvatarNewsView) convertView.findViewById(R.id.receiver_img);
					holderReceiver.receiverimg.load(Server.serverAddress+receiver.getAvatar());
					String dateStr1 = DateFormat.format("hh:mm", latter.getCreateDate()).toString();
					holderReceiver.texttime = (TextView) convertView.findViewById(R.id.time);
					holderReceiver.texttime.setText(dateStr1);
					
					break;
				}
			}
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
			if (data==null) {
				return 0;
			} else {
				return data.size();
			}
			
		}
		/** 该方法返回多少个不同的布局*/ 
		public int getViewTypeCount() {
			return 2;
		}
		 /** 根据position返回相应的Item*/  
		public int getItemViewType(int position) {
			PrivateLatter data = ShowPrivateLatter.this.data.get(position);
			if(data.getReceiver().getAccount().equals(receiver.getAccount())){
				return TYPE_ONE;
			}else {
				return TYPE_TWO;
			}
		}
		
	};
	
	
	
}
