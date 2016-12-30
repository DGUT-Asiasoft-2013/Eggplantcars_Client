package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import entity.History;
import entity.Page;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends Activity{
	EditText edit;
	ImageButton search;
	ImageButton back;
	
	ListView list;
	List<History> data;
	int page=0;
	View btnListFoot;
	TextView textListFoot;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		edit=(EditText) findViewById(R.id.edit_search);
		search=(ImageButton) findViewById(R.id.btn_search);
		back=(ImageButton) findViewById(R.id.btn_back);
		btnListFoot=LayoutInflater.from(this).inflate(R.layout.listfoot, null);
		textListFoot=(TextView)btnListFoot.findViewById(R.id.text);
		
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		list.addFooterView(btnListFoot);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				onHistoryClick(position);
			}
		});
		
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchOnClick();
				
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backOnClick();
			}
		});
		btnListFoot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delectOnClick();
			}
		});
	}
	
	

	void delectOnClick() {
		// TODO Auto-generated method stub
		OkHttpClient client=Server.getsharedClient();
		
		Request Request=Server.requestBuilderWithApi("delecthistory")
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
							refresh();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(SearchActivity.this)
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


	public void refresh() {
		reload();
//		finish();
//		Intent itnt=new Intent(SearchActivity.this,SearchActivity.class);
//		startActivity(itnt);
	}
	
	public void onResume(){
		// TODO Auto-generated method stub
		super.onResume();
		reload();
	}
	
	void onHistoryClick(int position) {
		// TODO Auto-generated method stub
		History history=data.get(position);
		String keyword=history.getText();
		Intent itnt=new Intent(SearchActivity.this,SearchListActivity.class);
		itnt.putExtra("keyword",keyword);
		startActivity(itnt);
	}
	
	void searchOnClick() {
		// TODO Auto-generated method stub
		final String keyword=edit.getText().toString();
		if(keyword!=null && !keyword.isEmpty()){
			
			OkHttpClient client=Server.getsharedClient();
			
			MultipartBody requestBody = new MultipartBody.Builder()
					.addFormDataPart("text", keyword)
					.build();
			
			okhttp3.Request Request=Server.requestBuilderWithApi("history")
					.method("post",null)
					.post(requestBody)
					.build();
			
			client.newCall(Request).enqueue(new Callback() {
				
				@Override
				public void onResponse(final Call arg0, Response arg1) throws IOException {
					// TODO Auto-generated method stub
					try{
						final String arg = arg1.body().string();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								onSearch(keyword);
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
					
				}
			});
			
		}else{
			new AlertDialog.Builder(SearchActivity.this)
			.setMessage("关键词不能为空")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setNegativeButton("好", null)
			.show();
			return ;
		}
		
	}
	void onSearch(String keyword) {
		// TODO Auto-generated method stub
		Intent itnt=new Intent(SearchActivity.this,SearchListActivity.class);
		itnt.putExtra("keyword",keyword);
		startActivity(itnt);
	}
	
	void backOnClick() {
		// TODO Auto-generated method stub
		SearchActivity.this.finish();
	}
	
	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.searchlistview, null);	
			}else{
				view = convertView;
			}

			TextView textTitle = (TextView) view.findViewById(R.id.title);
			
			History history =data.get(position);

			textTitle.setText(history.getText());
			
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
	
	void reload(){
		Request request =Server.requestBuilderWithApi("historyitems")
				.get()
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Page<History> data=new ObjectMapper()
							.readValue(arg1.body().string(),new TypeReference<Page<History>>() {} );
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SearchActivity.this.page=data.getNumber();
							SearchActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(SearchActivity.this)
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
	
}
