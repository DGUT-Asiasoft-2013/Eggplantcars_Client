package Fragment.pages;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.NewsContentActivity;
import com.rotk.eggplantcars.NewsUpLoading;
import com.rotk.eggplantcars.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import inputcells.AvatarNewsView;
import inputcells.AvatarView;
import inputcells.ImageAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {

	//tabber第一个页面
	View view;
	int page = 0;
	List<News> data;
	public static int[] res = { R.drawable.item5, R.drawable.item2, R.drawable.item3, R.drawable.item4,
			R.drawable.item1, R.drawable.item6 };
	private ImageAdapter adapter;
	private Gallery gallery;
	private ListView newsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_main, null);
			gallery = (Gallery) view.findViewById(R.id.img_gallery);
			adapter = new ImageAdapter(res, getActivity());
			gallery.setAdapter(adapter);
			newsList = (ListView) view.findViewById(R.id.list_news);
			newsList.setAdapter(listAdapter);
			newsList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position);
				}
			});
			/// ----------
			view.findViewById(R.id.btn_loadNews).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), NewsUpLoading.class);
					startActivity(intent);

				}
			});

			
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					String sInfo = ("索引：" + position % adapter.getResLength());
					Toast.makeText(getActivity(), sInfo, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
			/// ---------

		}
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadApi();
	}

	void onItemClicked(int position) {
		News news = data.get(position);	

		Intent itent = new Intent(getActivity(), NewsContentActivity.class);	
		itent.putExtra("news", news);

		startActivity(itent);
	}

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
							listAdapter.notifyDataSetInvalidated();
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
			avatarNewsView.load(Server.serverAddress+news.getAvatar());
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
