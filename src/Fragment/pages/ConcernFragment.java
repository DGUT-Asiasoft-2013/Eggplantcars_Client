package Fragment.pages;


import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.ConcernOneUserActivity;
import com.rotk.eggplantcars.DetailsActivity;
import com.rotk.eggplantcars.NewsContentActivity;
import com.rotk.eggplantcars.R;
import com.rotk.eggplantcars.ShoppingCarActivity;

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

public class ConcernFragment extends Fragment{
	View view;

	TextView btn_shoppingCar;
	ListView myconcern;
	List<Concern> data;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view ==null){
			view = inflater.inflate(R.layout.fragment_page_concern, null);
			myconcern = (ListView)view.findViewById(R.id.concern_list);
			btn_shoppingCar=(TextView) view.findViewById(R.id.mycomment);

			btn_shoppingCar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent itnt=new Intent(getActivity(),ShoppingCarActivity.class);
					startActivity(itnt);
				}
			});
		
			myconcern.setAdapter(listAdapter);
			myconcern.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(position);
				}
			});
		}
		return view;
	}

	void onItemClicked(int position) {

		Concern concern = data.get(position);	
		Intent itent = new Intent(getActivity(), ConcernOneUserActivity.class);	
		itent.putExtra("concern", concern);

		startActivity(itent);
	}

	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.example_myconcernlist, null);
			} else {
				view = convertView;
			}

			AvatarView avatarAuthorView = (AvatarView) view.findViewById(R.id.myconcern_avatar);
			TextView news_author_name = (TextView) view.findViewById(R.id.name);
			TextView concern_time = (TextView) view.findViewById(R.id.concern_time);
			Concern concern = data.get(position);
			String dateStr = DateFormat.format("MM-dd hh:mm", concern.getCreateDate()).toString();
			news_author_name.setText("êÇ³Æ£º"+concern.getIdKey().getNews_author().getName());
			concern_time.setText("¹Ø×¢Ê±¼ä£º"+dateStr);
			avatarAuthorView.load(Server.serverAddress+concern.getIdKey().getNews_author().getAvatar());
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getMyConcernsId();
	}

	private void getMyConcernsId() {
		Request request = YeServer.requestBuilderWithApi("Concerns/getMyConcerns").get().build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				final List<Concern> result = new ObjectMapper().readValue(responseString,
						new TypeReference<List<Concern>>() {
				});
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						//						new AlertDialog.Builder(getActivity())
						//						.setMessage(responseString)
						//						.show();
						ConcernFragment.this.data = result;
						listAdapter.notifyDataSetInvalidated();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(getActivity())
						.setMessage("fffff")
						.show();
					}
				});
			}
		});
	}

}


