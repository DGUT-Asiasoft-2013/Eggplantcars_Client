package com.rotk.eggplantcars;

import java.util.List;



import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import entity.News;

public class NewsContentActivity extends Activity {
	
	News news;
	//List<News> data;
	ListView listView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newscontent);
		
		listView =(ListView)findViewById(R.id.list_comment);
		//listView.setAdapter(listAdapter);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		news = (News)getIntent().getSerializableExtra("news");
		
		TextView text1 = (TextView)findViewById(R.id.text1);
		text1.setText(news.getTitle());
		
		TextView text_authorname = (TextView)findViewById(R.id.text_authorname);
		text_authorname.setText(news.getAuthorName());
		
		TextView text_creatDate = (TextView)findViewById(R.id.text_creatDate);
		text_creatDate.setText(DateFormat.format("yyyy-MM-dd hh:mm", news.getCreateDate()).toString());
		
		TextView text_authorwrite = (TextView)findViewById(R.id.text_authorwrite);
		text_authorwrite.setText(news.getText());
		
		AvatarView avatar = (AvatarView)findViewById(R.id.news_avatar);
		avatar.load(Server.serverAddress+news.getAuthorAvatar());
	}

}
