package com.rotk.eggplantcars;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import api.YeServer;
import entity.Money;
import entity.Page;
import entity.ShoppingCar;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShoppingCarActivity extends Activity{
	TextView countprice;
	ImageButton back;
	Button buy;
	Button delect;

	ListView list;
	List<ShoppingCar> data;
	int page=0;
	private ArrayList<ShoppingCar> selected = new ArrayList<ShoppingCar>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcar);
		countprice=(TextView) findViewById(R.id.count);
		back=(ImageButton) findViewById(R.id.back);
		buy=(Button) findViewById(R.id.buy);
		delect=(Button) findViewById(R.id.delect);

		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onClicked(position);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		delect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onDelectClick(selected);
			}
		});
		buy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBuyClick(selected);
			}
		});
		buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBuyClick(selected);
			}
		});
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reload();
	}
	void reload() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("myshoppingcar")
				.get()
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Page<ShoppingCar> data=new ObjectMapper()
							.readValue(arg1.body().string(),new TypeReference<Page<ShoppingCar>>() {} );
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ShoppingCarActivity.this.page=data.getNumber();
							ShoppingCarActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(ShoppingCarActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ShoppingCarActivity.this)
				.setMessage("链接失败，请检查您的网络")
				.show();
			}
		});
	}

	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.shoppingcarlist, null);	
			}else{
				view = convertView;
			}

			TextView textTitle = (TextView) view.findViewById(R.id.title);
			TextView textPrice = (TextView) view.findViewById(R.id.price);
			AvatarNewsView avatar=(AvatarNewsView) view.findViewById(R.id.avatar);
			final CheckBox btn_choose=(CheckBox) view.findViewById(R.id.choose);

			final ShoppingCar shoppingCar =data.get(position);


			avatar.load(Server.serverAddress+shoppingCar.getId().getDeal().getDealAvatar());
			textTitle.setText(shoppingCar.getId().getDeal().getTitle());
			textPrice.setText("￥"+shoppingCar.getId().getDeal().getPrice());



			btn_choose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(btn_choose.isChecked()==true){
						btn_choose.setChecked(true);
						selected.add(shoppingCar);
						doSum(selected);
					}else{
						btn_choose.setChecked(false);
						selected.remove(shoppingCar);
						doSum(selected);
					}
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
			return data==null ? 0 : data.size();
		}
	};

	private void doSum(List<ShoppingCar> selected) {
		// TODO Auto-generated method stub
		double count = 0;  
		for (int i = 0; i < selected.size(); i++) {  
			int price = Integer.parseInt(selected.get(i).getId().getDeal().getPrice());
			count += price;
		}  
		countprice.setText("￥"+count); 
	}  

	void onClicked(int position) {
		// TODO Auto-generated method stub
		ShoppingCar shoppingCar=data.get(position);
		Intent itnt=new Intent(ShoppingCarActivity.this,DetailsActivity.class);
		itnt.putExtra("data",shoppingCar);
		startActivity(itnt);
	}

	void onDelectClick(List<ShoppingCar> selected) {
		// TODO Auto-generated method stub
		for (int i = 0; i < selected.size(); i++) { 
			Integer deal_id=selected.get(i).getId().getDeal().getId();
			onDelect(deal_id);
		}
	}

	void onBuyClick(ArrayList<ShoppingCar> selected) {
		// TODO Auto-generated method stub
		if(selected.size()==0){
			new AlertDialog.Builder(ShoppingCarActivity.this)
			.setMessage("您还没有选中商品")
			.setPositiveButton("确定",null)
			.show();
		}else{
			Intent itnt=new Intent(ShoppingCarActivity.this,CountActivity.class);
			itnt.putExtra("data", selected);
			startActivity(itnt);
		}
	}
	

	void onDelect(Integer deal_id){
		OkHttpClient client=Server.getsharedClient();

		Request Request=Server.requestBuilderWithApi("myshoppingcar/"+deal_id+"/delect")
				.get()
				.build();
		client.newCall(Request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ShoppingCarActivity.this.finish();
							Intent itnt=new Intent(ShoppingCarActivity.this,ShoppingCarActivity.class);
							startActivity(itnt);
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(ShoppingCarActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ShoppingCarActivity.this)
				.setMessage("链接失败，请检查您的网络")
				.show();
			}
		});
	}
}
