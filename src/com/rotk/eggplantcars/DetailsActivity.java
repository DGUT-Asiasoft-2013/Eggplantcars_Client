package com.rotk.eggplantcars;


import Fragment.DealBarFragment;
import Fragment.DealBarFragment.OnDealSelectedListener;
import Fragment.pages.DetailsFragment;
import Fragment.pages.GoodsFragment;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import entity.Deal;

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
		deal=(Deal) getIntent().getSerializableExtra("data");
		
		btn_letter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//к╫пе
				Intent intent = new Intent(DetailsActivity.this,ShowDealsLatter.class);
				intent.putExtra("deal", deal);
				startActivity(intent);
				
			}
		});
		btn_shoppingcar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		btn_take.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
