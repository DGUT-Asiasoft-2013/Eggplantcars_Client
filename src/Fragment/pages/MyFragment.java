package Fragment.pages;



import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.AvatarView;
import com.rotk.eggplantcars.LoginActivity;
import com.rotk.eggplantcars.R;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.Server;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyFragment extends Fragment{
	View view;
	Button passwordchange;
	Button zhuxiao;
	Button personal;
	User u;

	TextView textView;
	TextView nicheng;
	ProgressBar progress;
	AvatarView avatar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_page_me, null);
			textView = (TextView) view.findViewById(R.id.text);
			nicheng = (TextView) view.findViewById(R.id.nicheng);
			progress = (ProgressBar) view.findViewById(R.id.my_progress);
			avatar = (AvatarView) view.findViewById(R.id.my_avatar);
			passwordchange = (Button)view.findViewById(R.id.btn_passwordchange);
			zhuxiao = (Button)view.findViewById(R.id.btn_zhuxiao);
			personal = (Button)view.findViewById(R.id.personal);

			passwordchange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//onpasswordchange();
				}


			});
			zhuxiao.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onzhuxiao();
				}

				


			});
			personal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					//onexit();
				}

				


			});
		}

		return view;
	}
	
	
	
	private void onzhuxiao() {
		// TODO Auto-generated method stub
		getActivity().finish();
		Intent inten = new Intent(getActivity(),LoginActivity.class);
		startActivity(inten);
		
	}

	
	@Override
	public void onResume() {
		super.onResume();

		textView.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);

		OkHttpClient client = Server.getsharedClient();
		Request request = Server.requestBuilderWithApi("me")
				.method("get", null)
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {
				    final User user = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							u = user;
							MyFragment.this.onResponse(arg0,user);
						}
					});					
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							MyFragment.this.onFailuer(arg0, e);
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						MyFragment.this.onFailuer(arg0, arg1);
					}
				});
			}
		});
	}

	protected void onResponse(Call arg0, User user) {
		progress.setVisibility(View.GONE);
		avatar.load(user);
		textView.setVisibility(View.VISIBLE);
		textView.setTextColor(Color.BLACK);
		textView.setText("”√ªß√˚£∫"+user.getAccount());
		nicheng.setText("Í«≥∆£∫"+user.getName());
	}

	void onFailuer(Call call, Exception ex){
		progress.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setTextColor(Color.RED);
		textView.setText(ex.getMessage());
	}
}
