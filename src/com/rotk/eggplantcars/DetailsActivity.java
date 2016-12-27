package com.rotk.eggplantcars;


import java.io.IOException;

import Fragment.DealBarFragment;
import Fragment.DealBarFragment.OnDealSelectedListener;
import Fragment.pages.DetailsFragment;
import Fragment.pages.GoodsFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import api.Server;
import entity.Deal;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class DetailsActivity extends Activity{
	View btn_letter;
	View btn_shoppingcar;
	Button btn_take;
	Deal deal;

	GoodsFragment goodspage=new GoodsFragment();
	DetailsFragment detailspage=new DetailsFragment();
	DealBarFragment tab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		btn_letter=findViewById(R.id.btn_letter);
		btn_shoppingcar=findViewById(R.id.btn_shoppingcar);
		btn_take=(Button) findViewById(R.id.btn_take);
		deal=(Deal)getIntent().getSerializableExtra("data");

		btn_letter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//私信
				Intent intent = new Intent(DetailsActivity.this,ShowDealsLatter.class);
				intent.putExtra("deal", deal);
				startActivity(intent);
				
			}
		});
		btn_shoppingcar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent itnt=new Intent(DetailsActivity.this,ShoppingCarActivity.class);
				startActivity(itnt);
			}
		});
		btn_take.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onTakeClick();
			}
		});
		tab=(DealBarFragment) getFragmentManager().findFragmentById(R.id.tab);
		tab.OnDealSelectedListener(new OnDealSelectedListener(){

			public void OnGoTabClicked(int index) {
				// TODO Auto-generated method stub
				onChange(index);
			}
		});


	}
	void onTakeClick() {
		// TODO Auto-generated method stub
		
		Request request=Server.requestBuilderWithApi("deal/"+deal.getId()+"/shoppingcar")
				.get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final String arg = arg1.body().string();
					DetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							DetailsActivity.this.onResponse(arg0,arg);
						}
					});
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						new AlertDialog.Builder(DetailsActivity.this)
						.setMessage("请求失败，请检查您的网络设置")
						.setPositiveButton("确认",null)
						.show();
					}
				});
			}
		});		
	}
	
	void onResponse(Call arg0, String response) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DetailsActivity.this)
		.setMessage("已加入您的购物车")
		.setPositiveButton("确认",null)
		.show();
	}
	public void onResume(){
		super.onResume();
		if (tab.getDealSelectedIndex()<0){
			tab.setDealSelectedItem(0);
		}
	}
	void onChange(int index) {
		// TODO Auto-generated method stub
		Fragment newfrag=null;
		switch (index) {
		case 0:newfrag=goodspage; break;
		case 1:newfrag=detailspage; break;
		default:break;
		}
		if(newfrag==null)return;
		getFragmentManager()
		.beginTransaction()
		.replace(R.id.content, newfrag)
		.commit();
	}
}
