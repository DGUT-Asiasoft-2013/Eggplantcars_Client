package com.rotk.eggplantcars;







import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import api.Server;
import entity.Deal;

public class DetailsActivity extends Activity{
	Deal deal;
	TextView text;
	TextView title;
	TextView name;
	TextView carmodel;
	TextView price;
	TextView buydate;
	TextView traveldistance;
	AvatarView dealavatar;
	Button take;//加入购物车
	Button letter;//私信卖家
	Button comment;//评价

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		text=(TextView) findViewById(R.id.text);
		title=(TextView) findViewById(R.id.title);
		name=(TextView) findViewById(R.id.sellername);
		carmodel=(TextView) findViewById(R.id.carmodel);
		price=(TextView) findViewById(R.id.price);
		buydate=(TextView) findViewById(R.id.buydate);
		traveldistance=(TextView) findViewById(R.id.traveldistance);
		take=(Button) findViewById(R.id.btn_take);
		letter=(Button) findViewById(R.id.btn_letter);
		comment=(Button) findViewById(R.id.btn_comment);
		dealavatar=(AvatarView) findViewById(R.id.avatar);

		deal=(Deal)getIntent().getSerializableExtra("data");

		text.setText("   卖家介绍："+deal.getText());
		title.setText(deal.getTitle());
		name.setText("卖家："+deal.getSellerName());
		carmodel.setText("二手车型号："+deal.getCarModel());
		price.setText("售价："+deal.getPrice());
		buydate.setText("商品被已使用："+deal.getBuyDate()+"年");
		traveldistance.setText("行驶里程："+deal.getTravelDistance());
		dealavatar.load(Server.serverAddress+deal.getDealAvatar());

		take.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takeOnClick();
			}
		});
		letter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				letterOnClick();
			}
		});
		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				commentOnClick();
			}
		});

	}

	void commentOnClick() {
		// TODO Auto-generated method stub

	}

	void letterOnClick() {
		// TODO Auto-generated method stub

	}

	void takeOnClick() {
		// TODO Auto-generated method stub


	}
}
