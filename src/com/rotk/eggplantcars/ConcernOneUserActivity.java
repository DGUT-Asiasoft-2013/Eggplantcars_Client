package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import Fragment.pages.MainFragment;
import android.annotation.SuppressLint;
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
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import api.YeServer;
import entity.Concern;
import entity.News;
import entity.Page;
import inputcells.AvatarNewsView;
import inputcells.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ConcernOneUserActivity extends Activity {

	Concern concern;
	ListView listview;
	List<News> data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concernoneuser);
		
		concern = (Concern)getIntent().getSerializableExtra("concern");
		listview = (ListView)findViewById(R.id.concernuser_list);
		listview.setAdapter(listAdapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onNewsItemClicked(position);
			}

		});
	}
	
	private void onNewsItemClicked(int position) {
		// TODO Auto-generated method stub
		News news = data.get(position);	

		Intent itent = new Intent(ConcernOneUserActivity.this, NewsContentActivity.class);	
		itent.putExtra("news", news);

		startActivity(itent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		accept_author_news();
		
		TextView concern_username = (TextView)findViewById(R.id.concern_username);
		concern_username.setText("êÇ³Æ£º"+concern.getIdKey().getNews_author().getName());
		
		TextView concern_usertime = (TextView)findViewById(R.id.concern_usertime);
		concern_usertime.setText("¹Ø×¢Ê±¼ä£º"+DateFormat.format("MM-dd hh:mm", concern.getCreateDate()).toString());
		
		AvatarView concern_user_avatar = (AvatarView)findViewById(R.id.concern_user_avatar); 
		concern_user_avatar.load(Server.serverAddress+concern.getIdKey().getNews_author().getAvatar());
	}

	private void accept_author_news() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(concern.getIdKey().getNews_author().getId()+"/News").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final List<News> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<List<News>>() {
							});
					
					ConcernOneUserActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ConcernOneUserActivity.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {
					
					ConcernOneUserActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(ConcernOneUserActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(ConcernOneUserActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}
	
	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.example_news_main, null);
			} else {
				view = convertView;
			}
			
			AvatarNewsView avatarNewsView = (AvatarNewsView) view .findViewById(R.id.news_avatar);
			AvatarView avatarAuthorView = (AvatarView) view.findViewById(R.id.news_author_avatar);
			TextView newslaber = (TextView) view.findViewById(R.id.news_laber);
			TextView newstext = (TextView) view.findViewById(R.id.news_text);
			TextView newsAuthorName = (TextView) view.findViewById(R.id.new_author_name);
			TextView newsTime = (TextView) view.findViewById(R.id.news_time);
			
			News news = data.get(position);
			String dateStr = DateFormat.format("MM-dd hh:mm", news.getCreateDate()).toString();
			newslaber.setText(news.getTitle());
			newstext.setText(news.getText());
			newsAuthorName.setText(news.getAuthorName());
			newsTime.setText(dateStr);
			avatarAuthorView.load(Server.serverAddress + news.getAuthorAvatar());
			final String[] newsImg = news.getAvatar().split("\\|");
			avatarNewsView.load(Server.serverAddress+newsImg[0]);
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
