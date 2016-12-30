package com.rotk.eggplantcars;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import entity.Record;

public class RecordDetailsActivity extends Activity {

	Record record;
	
	ImageButton btn_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recorddetails);
		record = (Record)getIntent().getSerializableExtra("record");
		btn_back = (ImageButton)findViewById(R.id.btn_back);

		//返回
		btn_back.setOnClickListener(new OnClickListener() {

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

		//类型
		TextView record_type = (TextView)findViewById(R.id.record_type);
		record_type.setText(record.getRecord_type());

		//交易金额
		TextView record_cash = (TextView)findViewById(R.id.record_cash);
		record_cash.setText(String.valueOf(record.getRecord_cash())+".00 元");

		//交易时间
		TextView record_date = (TextView)findViewById(R.id.record_date);
		record_date.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", record.getCreateDate()).toString());

		//余额
		TextView my_cash = (TextView)findViewById(R.id.my_cash);
		my_cash.setText(String.valueOf(record.getMy_cash())+".00 元");
		
		//备注
		TextView text = (TextView)findViewById(R.id.text);
		text.setText(record.getText());
	}
}
