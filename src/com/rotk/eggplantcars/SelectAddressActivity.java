package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import entity.Address;
import entity.Page;
import entity.ShoppingCar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SelectAddressActivity extends Activity{
	ArrayList<ShoppingCar> selected = new ArrayList<ShoppingCar>();
	ListView addressList;
	List<Address> data;
	int page = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectaddress);
		selected=(ArrayList<ShoppingCar>) getIntent().getSerializableExtra("data");
		addressList=(ListView) findViewById(R.id.list);
		addressList.setDividerHeight(0);
		addressList.setAdapter(addressAdapter);
		addressList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onClicked(position,selected);
			}
		});
	}
	
	void onClicked(int position, ArrayList<ShoppingCar> selected) {
		// TODO Auto-generated method stub
		finish();
		Address adrs = data.get(position);
		Intent itnt=new Intent(SelectAddressActivity.this,CountActivity.class);
		String address=adrs.getText();
		String name=adrs.getName();
		String phone=adrs.getPhoneNumber();
		
		itnt.putExtra("name",name);
		itnt.putExtra("address",address);
		itnt.putExtra("phone",phone);
		itnt.putExtra("data", selected);
		startActivity(itnt);
	}
	
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
							SelectAddressActivity.this.page=data.getNumber();
							SelectAddressActivity.this.data=data.getContent();
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
	
	BaseAdapter addressAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.listview_selectaddress, null);
			}else{
				view = convertView;
			}
			TextView name = (TextView) view.findViewById(R.id.user_name);
			TextView phoneNum = (TextView) view.findViewById(R.id.phone_num);
			TextView address = (TextView) view.findViewById(R.id.address);

			Address adrs = data.get(position);
			name.setText("收货人："+adrs.getName());
			phoneNum.setText("电话/手机："+adrs.getPhoneNumber());
			address.setText("地址："+adrs.getText());

			//btn.setOnc!!!!

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
