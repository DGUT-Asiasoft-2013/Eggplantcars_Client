package Fragment.pages;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.NewsContentActivity;
import com.rotk.eggplantcars.NewsUpLoading;
import com.rotk.eggplantcars.R;
import com.rotk.eggplantcars.ShowPrivateLatter;
import com.rotk.eggplantcars.ViewPagerShoepicActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import api.Server;
import entity.News;
import entity.Page;
import entity.PrivateLatter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.AvatarNewsView;
import inputcells.AvatarView;
import inputcells.ImageAdapter;
import inputcells.utils.AddPicNews;
import inputcells.utils.MyListener;
import inputcells.utils.PullToRefreshLayout;
import inputcells.utils.PullableListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {

	//tabber第一个页面
	View view;
	int page = 0;
	List<News> data;
	
	private PullableListView newsList;
	private PullToRefreshLayout ptrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_main, null);
			View listViewHeader = inflater.inflate(R.layout.fragment_show_viewpager, null);
			
			ptrl = (PullToRefreshLayout) view.findViewById(R.id.main_view);
			newsList = (PullableListView) view.findViewById(R.id.list_news);
			newsList.addHeaderView(listViewHeader);
			newsList.setAdapter(listAdapter);
			newsList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position-1);
				}
			});
			/// ----------
			view.findViewById(R.id.btn_loadNews).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), AddPicNews.class);
					startActivity(intent);

				}
			});

			
			ptrl.setOnRefreshListener(new MyListener(){

				@Override
				public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
					// TODO Auto-generated method stub
					super.onRefresh(pullToRefreshLayout);
					loadApi();
					pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				}

				@Override
				public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
					// TODO Auto-generated method stub
					super.onLoadMore(pullToRefreshLayout);
					loadMore();
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}
				
			});
			
			

		}
		return view;
	}

	
	protected void loadMore() {
		// TODO Auto-generated method stub
		Request request = Server.requestBuilderWithApi("shownews/"+(page+1))
				.get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<News> moredata = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<Page<News>>() {
							});
					if (moredata.getNumber() > page) {
						getActivity().runOnUiThread(new Runnable() {	
							@Override
							public void run() {
								if (data == null) {
									data = moredata.getContent();
								} else {
									data.addAll(moredata.getContent());
								}
								page = moredata.getNumber();
								listAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (Exception e) {
					Log.d("car", e.getMessage());
				}
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.d("car", arg1.getMessage());
			}
		});
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadApi();
	}

	
	//新闻点击
	void onItemClicked(int position) {
		News news = data.get(position);	

		Intent itent = new Intent(getActivity(), NewsContentActivity.class);	
		itent.putExtra("news", news);

		startActivity(itent);
	}

	
	//加载新闻
	void loadApi() {
		Request request = Server.requestBuilderWithApi("shownews").get().build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					final Page<News> data = new ObjectMapper().readValue(arg1.body().string(),
							new TypeReference<Page<News>>() {
							});
					if(MainFragment.this.isVisible())
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							MainFragment.this.page = data.getNumber();
							MainFragment.this.data = data.getContent();
							listAdapter.notifyDataSetChanged();
						}
					});
				} catch (final Exception e) {
					if(MainFragment.this.isVisible())
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				if(MainFragment.this.isVisible())
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(getActivity()).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	static class NewsHolder{
		TextView newslaber = null;
		TextView newstext = null;
		TextView newsauthorname = null;
		TextView newstime = null;
		AvatarView newsauthoravatar = null;
		AvatarNewsView newsAvatar = null;
	}
	static class NewsPicHolder{
		TextView picnewslaber = null;
		TextView picnewsauthorname = null;
		TextView picnewstime = null;
		AvatarNewsView img1,img2,img3;
	}
	
	
	//新闻设配器
	BaseAdapter listAdapter = new BaseAdapter() {
		private final int TYPE_ONE=0,TYPE_TWO=1; 
		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view;
		    News news =MainFragment.this.data.get(position);
			int type = getItemViewType(position);
			NewsHolder newsHolder = null;
			NewsPicHolder newsPicHolder = null;
			
			if (convertView == null) {
				switch (type) {
				case TYPE_ONE:
					convertView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.example_news_main, null);  //布置布局
					newsHolder = new NewsHolder();
					newsHolder.newslaber = (TextView) convertView.findViewById(R.id.news_laber);
					newsHolder.newstext = (TextView) convertView.findViewById(R.id.news_text);
					newsHolder.newstime = (TextView) convertView.findViewById(R.id.news_time);
					newsHolder.newsauthorname = (TextView) convertView.findViewById(R.id.new_author_name);
					newsHolder.newsauthoravatar = (AvatarView) convertView.findViewById(R.id.news_author_avatar);
					newsHolder.newsAvatar = (AvatarNewsView) convertView.findViewById(R.id.news_avatar);
					//以上定义，以下赋值
					newsHolder.newslaber.setText(news.getTitle());
					newsHolder.newstext.setText(news.getText());
					String dateStr = DateFormat.format("MMdd hh:mm", news.getCreateDate()).toString();
					newsHolder.newstime.setText(dateStr);
					newsHolder.newsauthoravatar.load(Server.serverAddress+news.getAuthorAvatar());
					newsHolder.newsauthorname.setText(news.getAuthorName());
					final String[] newsImg = news.getAvatar().split("\\|");  //按  |  分隔
					newsHolder.newsAvatar.load(Server.serverAddress+newsImg[0]);
					convertView.setTag(newsHolder);
					break;

				case TYPE_TWO:
					//下午继续搞第二种布局
					convertView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.example_news_pic, null);
					newsPicHolder = new NewsPicHolder();
					newsPicHolder.picnewslaber = (TextView) convertView.findViewById(R.id.picnews_laber);
					newsPicHolder.picnewsauthorname = (TextView) convertView.findViewById(R.id.author_name);
					newsPicHolder.picnewstime = (TextView) convertView.findViewById(R.id.picnewstime);
					newsPicHolder.img1 = (AvatarNewsView) convertView.findViewById(R.id.img1);
					newsPicHolder.img2 = (AvatarNewsView) convertView.findViewById(R.id.img2);
					newsPicHolder.img3 = (AvatarNewsView) convertView.findViewById(R.id.img3);
					
					
					newsPicHolder.picnewslaber.setText(news.getTitle());
					newsPicHolder.picnewsauthorname.setText(news.getAuthorName());
					String dateStr1 = DateFormat.format("MMdd hh:mm", news.getCreateDate()).toString();
					newsPicHolder.picnewstime.setText(dateStr1);
					
					
					
					//处理图片
					final String[] newsImg1 = news.getAvatar().split("\\|");  //按  |  分隔
					if (newsImg1.length == 3) {
						for (int i = 0; i < newsImg1.length; i++) {
							AvatarNewsView[] imgs = new AvatarNewsView[]{newsPicHolder.img1,newsPicHolder.img2,newsPicHolder.img3};
							imgs[i].load(Server.serverAddress+newsImg1[i]);
						}
						//等等图片点击效果
						final String sendpicurl = news.getAvatar();
						newsPicHolder.img3.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",3);
								startActivity(intent);	
							}
						});
						newsPicHolder.img2.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",2);
								startActivity(intent);	
							}
						});
						newsPicHolder.img1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",1);
								startActivity(intent);			
							}
						});
						
						
						//
					}else if (newsImg1.length == 2) {
						newsPicHolder.img3.setVisibility(AvatarNewsView.GONE);
						for (int i = 0; i < newsImg1.length; i++) {
							AvatarNewsView[] imgs = new AvatarNewsView[]{newsPicHolder.img1,newsPicHolder.img2};
							imgs[i].load(Server.serverAddress+newsImg1[i]);
						}
						final String sendpicurl = news.getAvatar();
						newsPicHolder.img2.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",2);
								startActivity(intent);	
							}
						});
						newsPicHolder.img1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",1);
								startActivity(intent);			
							}
						});
					}
	
					convertView.setTag(newsPicHolder);
					break;
				}
			} else {
				switch (type) {
				case TYPE_ONE:
					newsHolder = (NewsHolder) convertView.getTag();
					newsHolder.newslaber = (TextView) convertView.findViewById(R.id.news_laber);
					newsHolder.newstext = (TextView) convertView.findViewById(R.id.news_text);
					newsHolder.newstime = (TextView) convertView.findViewById(R.id.news_time);
					newsHolder.newsauthorname = (TextView) convertView.findViewById(R.id.new_author_name);
					newsHolder.newsauthoravatar = (AvatarView) convertView.findViewById(R.id.news_author_avatar);
					newsHolder.newsAvatar = (AvatarNewsView) convertView.findViewById(R.id.news_avatar);
					//以上定义，以下赋值
					newsHolder.newslaber.setText(news.getTitle());
					newsHolder.newstext.setText(news.getText());
					String dateStr = DateFormat.format("MMdd hh:mm", news.getCreateDate()).toString();
					newsHolder.newstime.setText(dateStr);
					newsHolder.newsauthoravatar.load(Server.serverAddress+news.getAuthorAvatar());
					newsHolder.newsauthorname.setText(news.getAuthorName());
					final String[] newsImg = news.getAvatar().split("\\|");  //按  |  分隔
					newsHolder.newsAvatar.load(Server.serverAddress+newsImg[0]);
					break;

				case TYPE_TWO:
					newsPicHolder = (NewsPicHolder) convertView.getTag();
					newsPicHolder.picnewslaber = (TextView) convertView.findViewById(R.id.picnews_laber);
					newsPicHolder.picnewsauthorname = (TextView) convertView.findViewById(R.id.author_name);
					newsPicHolder.picnewstime = (TextView) convertView.findViewById(R.id.picnewstime);
					newsPicHolder.img1 = (AvatarNewsView) convertView.findViewById(R.id.img1);
					newsPicHolder.img2 = (AvatarNewsView) convertView.findViewById(R.id.img2);
					newsPicHolder.img3 = (AvatarNewsView) convertView.findViewById(R.id.img3);
					
					
					newsPicHolder.picnewslaber.setText(news.getTitle());
					newsPicHolder.picnewsauthorname.setText(news.getAuthorName());
					String dateStr1 = DateFormat.format("MMdd hh:mm", news.getCreateDate()).toString();
					newsPicHolder.picnewstime.setText(dateStr1);
					
					
					
					//处理图片
					final String[] newsImg1 = news.getAvatar().split("\\|");  //按  |  分隔
					if (newsImg1.length == 3) {
						for (int i = 0; i < newsImg1.length; i++) {
							AvatarNewsView[] imgs = new AvatarNewsView[]{newsPicHolder.img1,newsPicHolder.img2,newsPicHolder.img3};
							imgs[i].load(Server.serverAddress+newsImg1[i]);
						}
						//等等图片点击效果
						//等等图片点击效果
						final String sendpicurl = news.getAvatar();
						newsPicHolder.img3.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",3);
								startActivity(intent);	
							}
						});
						newsPicHolder.img2.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",2);
								startActivity(intent);	
							}
						});
						newsPicHolder.img1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",1);
								startActivity(intent);			
							}
						});				
						
						//
					}else if (newsImg1.length == 2) {
						newsPicHolder.img3.setVisibility(AvatarNewsView.GONE);
						for (int i = 0; i < newsImg1.length; i++) {
							AvatarNewsView[] imgs = new AvatarNewsView[]{newsPicHolder.img1,newsPicHolder.img2};
							imgs[i].load(Server.serverAddress+newsImg1[i]);
						}
						//
						final String sendpicurl = news.getAvatar();
						newsPicHolder.img2.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",2);
								startActivity(intent);	
							}
						});
						newsPicHolder.img1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(getActivity(),ViewPagerShoepicActivity.class);
								intent.putExtra("newsImgurl",sendpicurl);
								intent.putExtra("picposition",1);
								startActivity(intent);			
							}
						});
					}
					break;
				}
			}
//			View view = null;
//			if (convertView == null) {
//				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//				view = inflater.inflate(R.layout.example_news_main, null);
//			} else {
//				view = convertView;
//			}
//			
//			AvatarNewsView avatarNewsView = (AvatarNewsView) view .findViewById(R.id.news_avatar);
//			AvatarView avatarAuthorView = (AvatarView) view.findViewById(R.id.news_author_avatar);
//			TextView newslaber = (TextView) view.findViewById(R.id.news_laber);
//			TextView newstext = (TextView) view.findViewById(R.id.news_text);
//			TextView newsAuthorName = (TextView) view.findViewById(R.id.new_author_name);
//			TextView newsTime = (TextView) view.findViewById(R.id.news_time);
//			
//			News news = data.get(position);
//			String dateStr = DateFormat.format("MM-dd hh:mm", news.getCreateDate()).toString();
//			newslaber.setText(news.getTitle());
//			newstext.setText(news.getText());
//			newsAuthorName.setText(news.getAuthorName());
//			newsTime.setText(dateStr);
//			avatarAuthorView.load(Server.serverAddress + news.getAuthorAvatar());
//			avatarNewsView.load(Server.serverAddress+news.getAvatar());
//			return view;
			return convertView;
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
		
		/** 该方法返回多少个不同的布局*/ 
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		 /** 根据position返回相应的Item*/  
		public int getItemViewType(int position) {
			News news = data.get(position);
			final String[] newsImg = news.getAvatar().split("\\|");  //按  |  分隔
			if(newsImg.length<=1){
				return TYPE_ONE;
			}else {
				return TYPE_TWO;
			}
		}
		
	};
}
