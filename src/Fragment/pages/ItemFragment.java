package Fragment.pages;

import java.io.IOException;
import java.util.List;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.DetailsActivity;
import com.rotk.eggplantcars.R;
import com.rotk.eggplantcars.SearchActivity;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import api.Server;
import android.widget.AdapterView.OnItemClickListener;
import entity.Deal;
import entity.Page;
import inputcells.AvatarNewsView;
import inputcells.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ItemFragment extends Fragment{

	View view;
	ListView list;
	List<Deal> data;
	int page=0;
	View btnLoadMore;
	TextView textLoadMore;
	EditText edit;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null){
			view=inflater.inflate(R.layout.fragment_page_item, null);
			btnLoadMore=inflater.inflate(R.layout.loadmore,null);
			textLoadMore=(TextView) btnLoadMore.findViewById(R.id.text);
			edit=(EditText) view.findViewById(R.id.edit_btn);
			
			list=(ListView) view.findViewById(R.id.list);
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
			edit.setFocusable(false);
			edit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent itnt=new Intent(getActivity(),SearchActivity.class);
					
					startActivity(itnt);
					
				}
			});
		}
		return view;
	}
	
	void loadMore() {
		// TODO Auto-generated method stub
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中...");

		Request request=Server.requestBuilderWithApi("dealitems/"+(page+1))
				.get()
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
				try{
					Page<Deal> deal=new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Deal>>(){});
					if(deal.getNumber()>page){
						if(data==null){
							data=deal.getContent();
						}else{
							data.addAll(deal.getContent());
						}
						page=deal.getNumber();

						getActivity().runOnUiThread(new Runnable() {

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

	public void onResume(){
		// TODO Auto-generated method stub
		super.onResume();
		reload();
	}
	
	void reload(){
		Request request =Server.requestBuilderWithApi("dealitems")
				.get()
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Page<Deal> data=new ObjectMapper()
							.readValue(arg1.body().string(),new TypeReference<Page<Deal>>() {} );
					if(ItemFragment.this.isVisible())
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ItemFragment.this.page=data.getNumber();
							ItemFragment.this.data=data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});
				}catch (final Exception e) {
					if(ItemFragment.this.isVisible())
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(getActivity())
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
	
	void onClick(int position){
		Deal deal=data.get(position);
		Intent itnt=new Intent(getActivity(),DetailsActivity.class);
		itnt.putExtra("data",deal);	
		startActivity(itnt);

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
			TextView textMoney=(TextView) view.findViewById(R.id.text);
			AvatarNewsView avatar=(AvatarNewsView) view.findViewById(R.id.avatar);
			
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
