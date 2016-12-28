package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import entity.NewsComment;
import entity.Page;
import inputcells.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class AllCommentActivity extends Activity{
	Button btn_comment;
	Button btn_mycomment;
	String action;
	ListView list;
	
	List<NewsComment> data;
	int page=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_allcomment);
		btn_comment=(Button) findViewById(R.id.btn_comment);
		btn_mycomment=(Button) findViewById(R.id.btn_mycomment);
		
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		btn_comment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action=("mycomments");
				reload();
			}
		});
		btn_mycomment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				action=("receivedcomment");
				reload();
			}
		});
	}
	
	

	protected void onResume() {
		super.onResume();
		action=("mycomments");
		reload();
	}
	
	void reload() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("news/author_id/"+action)
				.get()
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Page<NewsComment> data=new ObjectMapper()
							.readValue(arg1.body().string(),new TypeReference<Page<NewsComment>>() {} );
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AllCommentActivity.this.page=data.getNumber();
							AllCommentActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(AllCommentActivity.this)
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

	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.listview_round, null);	
			}else{
				view = convertView;
			}

			
			TextView textDate = (TextView) view.findViewById(R.id.date);
			AvatarView avatar=(AvatarView) view.findViewById(R.id.avatar);
			TextView text=(TextView) view.findViewById(R.id.text);
			TextView name=(TextView) view.findViewById(R.id.name);

			NewsComment newsComment =data.get(position);

			avatar.load(Server.serverAddress+newsComment.getAuthor().getAvatar());
			String dateStr=DateFormat.format("yyyy-mm-dd hh:mm", newsComment.getCreateDate()).toString();
			textDate.setText(dateStr);
			text.setText(newsComment.getText());
			name.setText(newsComment.getAuthor().getName());
			

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
