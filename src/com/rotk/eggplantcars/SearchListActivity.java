package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import android.widget.AdapterView.OnItemClickListener;
import entity.Deal;
import entity.Page;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SearchListActivity extends Activity{
	ListView list;
	List<Deal> data;
	int page=0;
	View btnLoadMore;
	TextView textLoadMore;
	String keyword;
	TextView text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_searchlist);
		text=(TextView) findViewById(R.id.text);
		keyword=(String) getIntent().getExtras().get("keyword");
		text.setText("关于"+keyword+"的搜索结果");
		btnLoadMore = LayoutInflater.from(this).inflate(R.layout.loadmore, null);
		
		list=(ListView)findViewById(R.id.list);
		list.addFooterView(btnLoadMore);
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onClick(position);
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Request request =Server.requestBuilderWithApi("deal/s/"+keyword)
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
							SearchListActivity.this.page=data.getNumber();
							SearchListActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					// TODO: handle exception
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(SearchListActivity.this)
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
	
	
	protected void onClick(int position) {
		// TODO Auto-generated method stub
		Deal deal=data.get(position);
		Intent itnt=new Intent(SearchListActivity.this,DetailsActivity.class);
		itnt.putExtra("data",deal);
		
		startActivity(itnt);
	}

	protected void loadMore() {
		// TODO Auto-generated method stub
		
	}
	
	
	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.listview, null);	
			}else{
				view = convertView;
			}

			TextView textTitle = (TextView) view.findViewById(R.id.title);
			TextView textDate = (TextView) view.findViewById(R.id.date);
			AvatarNewsView avatar=(AvatarNewsView) view.findViewById(R.id.avatar);
			TextView textMoney=(TextView) view.findViewById(R.id.text);

			Deal deal =data.get(position);

			avatar.load(Server.serverAddress+deal.getDealAvatar());
			String dateStr=DateFormat.format("yyyy-mm-dd hh:mm", deal.getCreateDate()).toString();
			textDate.setText(dateStr);
			textTitle.setText(deal.getTitle());
			textMoney.setText(deal.getPrice()+"￥");
			

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
