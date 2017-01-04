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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import entity.Deal;
import entity.Page;
import entity.User;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MySaleActivity extends Activity{
	ListView list;
	List<Deal> data;
	int page=0;
	
	User user;
	Button pull;
	ImageButton back;
	View btnLoadMore;
	TextView textLoadMore;
	
	private ArrayList<Deal> selected = new ArrayList<Deal>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mysale);
		back=(ImageButton) findViewById(R.id.back);
		pull=(Button) findViewById(R.id.pull);
		user = (User)getIntent().getSerializableExtra("user");
		btnLoadMore = LayoutInflater.from(this).inflate(R.layout.loadmore, null);
		textLoadMore=(TextView) btnLoadMore.findViewById(R.id.text);
		
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		list.addFooterView(btnLoadMore);
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
		
		pull.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				goPull(selected);
			}
		});
		
		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadMore();
			}
		});
	}
	
	void goPull(ArrayList<Deal> selected) {
		// TODO Auto-generated method stub
		if(selected.size()==0){
			new AlertDialog.Builder(MySaleActivity.this)
			.setMessage("您还没有选中商品")
			.setPositiveButton("确定",null)
			.show();
		}else{
			for (int i = 0; i < selected.size(); i++) { 
				Integer deal_id=selected.get(i).getId();
				onPull(deal_id);
				}
		}
	}

	

	void onClicked(int position) {
		// TODO Auto-generated method stub
		Deal deal=data.get(position);
		Intent itnt=new Intent(MySaleActivity.this,DetailsActivity.class);
		itnt.putExtra("data",deal);	
		startActivity(itnt);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reload();
	}
	
	void onPull(Integer deal_id) {
		// TODO Auto-generated method stub
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("deal_id",String.valueOf(deal_id))
				.build();
		
		Request request = Server.requestBuilderWithApi("deal/"+deal_id+"/pullmysale")
				.post(body)
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
				if(result){
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(MySaleActivity.this)
							.setMessage("成功下架")
							.setPositiveButton("确定",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									reload();
								}
							})
							.show();
						}
					});

				}
				else{
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(MySaleActivity.this)
							.setMessage("下架失败")
							.setNegativeButton("返回",null)
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(MySaleActivity.this)
						.setMessage("下架失败！连接错误")
						.setNegativeButton("返回",null)
						.show();
					}
				});
			}
		});
	}
	
	void reload() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("deal/"+user.getId()+"/mysale")
				.get()
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Page<Deal> data=new ObjectMapper()
							.readValue(arg1.body().string(),new TypeReference<Page<Deal>>() {} );
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							MySaleActivity.this.page=data.getNumber();
							MySaleActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(MySaleActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MySaleActivity.this)
				.setMessage("链接失败，请检查您的网络")
				.show();
			}
		});
	}
	
	void loadMore() {
		// TODO Auto-generated method stub
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中...");

		Request request=Server.requestBuilderWithApi("deal/"+user.getId()+"/mysale/"+(page+1))
				.get()
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
				try{
					Page<Deal> deal=new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Deal>>(){});
					if(deal.getNumber()>page){
						if(data==null){
							data=deal.getContent();
						}else{
							data.addAll(deal.getContent());
						}
						page=deal.getNumber();

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								listAdapter.notifyDataSetInvalidated();
							}
						});
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub

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

			final Deal deal =data.get(position);


			avatar.load(Server.serverAddress+deal.getDealAvatar());
			textTitle.setText(deal.getTitle());
			textPrice.setText("￥"+deal.getPrice());



			btn_choose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(btn_choose.isChecked()==true){
						btn_choose.setChecked(true);
						selected.add(deal);
					}else{
						btn_choose.setChecked(false);
						selected.remove(deal);
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
}
