package com.rotk.eggplantcars;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import entity.ShoppingCar;
import inputcells.AvatarNewsView;

public class CountActivity extends Activity{
	ImageButton back;
	TextView buyer_name;
	TextView buyeraddress;
	TextView countprice;
	ListView list;
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

		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSum(data);
	}
	void doSum(List<ShoppingCar> data) {
		// TODO Auto-generated method stub
		double count = 0;  
		for (int i = 0; i < data.size(); i++) {  
			int price = Integer.parseInt(data.get(i).getId().getDeal().getPrice());
			count += price;
		}  
		countprice.setText("гд"+count); 
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
			textPrice.setText("гд"+data.get(position).getId().getDeal().getPrice());

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
