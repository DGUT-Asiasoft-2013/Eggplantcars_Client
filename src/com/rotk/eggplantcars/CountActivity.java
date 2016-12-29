package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Fragment.pages.MyFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import entity.Address;
import entity.ShoppingCar;
import entity.User;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class CountActivity extends Activity{
	ImageButton back;
	ImageButton address;
	TextView buyer_name;
	TextView buyer_address;
	TextView countprice;
	ListView list;
	Address a;
	Button buy;

	ArrayList<ShoppingCar> data = new ArrayList<ShoppingCar>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_count);
		data=(ArrayList<ShoppingCar>) getIntent().getSerializableExtra("data");
		countprice=(TextView) findViewById(R.id.count);
		back=(ImageButton) findViewById(R.id.back);
		address=(ImageButton) findViewById(R.id.select_address);
		buyer_name=(TextView) findViewById(R.id.buyer_name);
		buyer_address=(TextView) findViewById(R.id.buyer_address);
		buy=(Button) findViewById(R.id.buy);

		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		back.setOnClickListener(new OnClickListener() {//返回按键

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		address.setOnClickListener(new OnClickListener() {//选择地址按键
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent itnt=new Intent(CountActivity.this,SelectAddressActivity.class);
				startActivity(itnt);
			}
		});
		buy.setOnClickListener(new OnClickListener() {//购物按键
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buyClick();
			}
		});
	}

	void buyClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSum(data);//计算总价
		getAddress();//获取最晚地址
	}

	void getAddress() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("getlastaddress")
				.get()
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Address address = new ObjectMapper().readValue(arg1.body().bytes(), Address.class);
					runOnUiThread(new Runnable() {
						public void run() {
							a=address;
							setAddress(arg0,address);//设置地址文本
						}

					});					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(CountActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setAddress(Call arg0, Address address) {
		// TODO Auto-generated method stub
		buyer_address.setText("收货地址："+address.getText());
		buyer_name.setText("收件人："+address.getName());
	}
	
	void doSum(List<ShoppingCar> data) {
		// TODO Auto-generated method stub
		double count = 0;  
		for (int i = 0; i < data.size(); i++) {  
			int price = Integer.parseInt(data.get(i).getId().getDeal().getPrice());
			count += price;
		}  
		countprice.setText("￥"+count); 
	}  
	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.listview, null);	
			}else{
				view = convertView;
			}

			TextView textTitle = (TextView) view.findViewById(R.id.title);
			TextView textPrice = (TextView) view.findViewById(R.id.text);
			AvatarNewsView avatar=(AvatarNewsView) view.findViewById(R.id.avatar);

			avatar.load(Server.serverAddress+data.get(position).getId().getDeal().getDealAvatar());
			textTitle.setText(data.get(position).getId().getDeal().getTitle());
			textPrice.setText("￥"+data.get(position).getId().getDeal().getPrice());

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
			return data==null ? 0 : data.size();
		}
	};

}
