package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import api.YeServer;
import entity.Record;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class RecordActivity extends Activity {

	User user;

	ImageButton btn_back;
	List<Record> data;

	ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		user = (User)getIntent().getSerializableExtra("user");
		listview = (ListView)findViewById(R.id.list_record);
		listview.setAdapter(listAdapter);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onNewsItemClicked(position);
			}

		});

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
		Request request = YeServer.requestBuilderWithApi(user.getId()+"/Records").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final List<Record> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<List<Record>>() {
					});

					RecordActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							RecordActivity.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {

					RecordActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(RecordActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(RecordActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	private void onNewsItemClicked(int position) {
		// TODO Auto-generated method stub

		Record record = data.get(position);
		Intent intent = new Intent(RecordActivity.this, RecordDetailsActivity.class);	
		intent.putExtra("record", record);
		startActivity(intent);
	}

	//适配器
	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.list_record, null);
			} else {
				view = convertView;
			}


			TextView record_type = (TextView) view.findViewById(R.id.record_type);//交易类型
			TextView my_cash = (TextView) view.findViewById(R.id.mymoney);//我的余额
			TextView record_date = (TextView) view.findViewById(R.id.record_date);//交易时间
			TextView cashchange = (TextView) view.findViewById(R.id.cashchange);//交易金额


			Record record = data.get(position);
			record_type.setText(record.getRecord_type());
			if(record.getRecord_type().equals("充值")){

				cashchange.setText("+"+String.valueOf(record.getRecord_cash()));

			}
			else if(record.getRecord_type().equals("转账")){
				cashchange.setText("-"+String.valueOf(record.getRecord_cash()));
			}
			else if(record.getRecord_type().equals("收账")){
				cashchange.setText("+"+String.valueOf(record.getRecord_cash()));
			}
			else if(record.getRecord_type().equals("退款")){
				cashchange.setText("+"+String.valueOf(record.getRecord_cash()));
			}
			String dateStr = DateFormat.format("yyyy-MM-dd", record.getCreateDate()).toString();
			my_cash.setText("余额："+String.valueOf(record.getMy_cash()));
			record_date.setText(dateStr);

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
