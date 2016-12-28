package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import api.YeServer;
import entity.Deal;
import entity.News;
import entity.NewsComment;
import entity.Page;
import inputcells.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;
//新闻详细页面
public class NewsContentActivity extends Activity {

	News news;
	//List<News> data;
	ListView listView;
	ImageButton btn_concern;
	Button btn_good;
	Button btn_comment;
	private boolean isConcerned;//关注
	private boolean isLiked;//点赞
	
	List<NewsComment> data;
	int page=0;
	View btnLoadMore;
	TextView textLoadMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newscontent);
		news = (News)getIntent().getSerializableExtra("news");

		btnLoadMore = LayoutInflater.from(this).inflate(R.layout.loadmore, null);
		textLoadMore=(TextView) btnLoadMore.findViewById(R.id.text);

		btn_good = (Button)findViewById(R.id.btn_good);
		btn_concern = (ImageButton)findViewById(R.id.btn_concern);
		btn_comment = (Button)findViewById(R.id.btn_comment);
		listView =(ListView)findViewById(R.id.list_comment);
		
		listView.setAdapter(listAdapter);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);
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
		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadMore();
			}
		});
	}

	void loadMore() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				btnLoadMore.setEnabled(false);
				textLoadMore.setText("载入中...");

				Request request=Server.requestBuilderWithApi("News/"+news.getId()+"/comments/"+(page+1))
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
							Page<NewsComment> newsComment=new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<NewsComment>>(){});
							if(newsComment.getNumber()>page){
								if(data==null){
									data=newsComment.getContent();
								}else{
									data.addAll(newsComment.getContent());
								}
								page=newsComment.getNumber();

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

	//获取有多少个赞
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
			btn_good.setText("赞("+count+")");
		}else{
			btn_good.setText("无");
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
		reloadlist();
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

	private void reloadlist() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("News/"+news.getId()+"/comments")
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
							NewsContentActivity.this.page=data.getNumber();
							NewsContentActivity.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(NewsContentActivity.this)
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
