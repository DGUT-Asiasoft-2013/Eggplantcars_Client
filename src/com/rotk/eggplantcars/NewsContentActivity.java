package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import api.YeServer;
import entity.News;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class NewsContentActivity extends Activity {

	News news;
	//List<News> data;
	ListView listView;
	ImageButton btn_concern;
	Button btn_good;
	Button btn_comment;
	private boolean isConcerned;//¹Ø×¢
	private boolean isLiked;//µãÔÞ

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newscontent);
		news = (News)getIntent().getSerializableExtra("news");

		btn_good = (Button)findViewById(R.id.btn_good);
		btn_concern = (ImageButton)findViewById(R.id.btn_concern);
		btn_comment = (Button)findViewById(R.id.btn_comment);
		listView =(ListView)findViewById(R.id.list_comment);
		//listView.setAdapter(listAdapter);

		findViewById(R.id.btn_concern).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onConcern();
			}
		});

		btn_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onComment();
			}
		});
		btn_good.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onlike();
			}
		});
	}

	void onComment() {
		// TODO Auto-generated method stub
		Intent itent = new Intent(NewsContentActivity.this, NewsCommentActivity.class);
		itent.putExtra("news", news);
		startActivity(itent);
	}

	private void onlike() {
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("likes", String.valueOf(!isLiked))
				.build(); 

		Request request = YeServer.requestBuilderWithApi("news/"+news.getId()+"/likes")
				.post(body).build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						reloadlikes();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						reloadlikes();
					}
				});
			}
		});
	}

	private void reloadlikes() {
		reloadLikes();
		checkLiked();
	}

	private void checkLiked() {
		Request request = YeServer.requestBuilderWithApi("news/"+news.getId()+"/isliked").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(result);
						}
					});
				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(false);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onCheckLikedResult(false);
					}
				});				
			}
		});
	}

	private void onCheckLikedResult(boolean result) {
		isLiked = result;
		btn_good.setTextColor(result ? Color.BLUE : Color.BLACK);
	}

	private void reloadLikes() {
		Request request = YeServer.requestBuilderWithApi("news/"+news.getId()+"/likes")
				.get().build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					String responseString = arg1.body().string();
					final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(count);
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(0);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onReloadLikesResult(0);
					}
				});
			}
		});
	}

	private void onReloadLikesResult(Integer count) {
		if(count>0){
			btn_good.setText("ÔÞ("+count+")");
		}else{
			btn_good.setText("ÎÞ");
		}
	}

	private void onConcern() {
		// TODO Auto-generated method stub
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("Concern", String.valueOf(!isConcerned))
				.build(); 

		Request request = YeServer.requestBuilderWithApi(news.getAuthorId()+"/Concerns")
				.post(body).build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						reloadconcern();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						reloadconcern();
					}
				});
			}
		});
	}

	void reloadconcern(){
		checkConcerned();
	}

	private void checkConcerned() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(news.getAuthorId()+"/isConcerned").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckConcernedResult(result);
						}
					});
				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckConcernedResult(false);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onCheckConcernedResult(false);
					}
				});				
			}
		});
	}

	private void onCheckConcernedResult(Boolean result) {
		isConcerned = result;
		if(result){
			btn_concern.setImageResource(R.drawable.like);
		}
		else{
			btn_concern.setImageResource(R.drawable.unlike);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reloadlikes();
		reloadconcern();
		//String authorId = getIntent().getStringExtra("authorId");

		TextView text_title = (TextView)findViewById(R.id.text_title);
		text_title.setText(news.getTitle());

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
