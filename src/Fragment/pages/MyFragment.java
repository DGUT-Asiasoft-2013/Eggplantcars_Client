package Fragment.pages;



import java.io.IOException;

import org.w3c.dom.Text;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rotk.eggplantcars.AvatarView;
import com.rotk.eggplantcars.LoginActivity;
import com.rotk.eggplantcars.PasswordChangeActivity;
import com.rotk.eggplantcars.PasswordRecoverActivity;
import com.rotk.eggplantcars.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;

import android.content.DialogInterface.OnCancelListener;

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
	TextView passwordchange; //修改密码
	TextView zhuxiao;	//注销
	TextView personal;//私信
	TextView comment;
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
			passwordchange = (TextView) view.findViewById(R.id.btn_passwordchange);
			zhuxiao = (TextView) view.findViewById(R.id.btn_zhuxiao);
			personal = (TextView) view.findViewById(R.id.personal);
			comment =(TextView) view .findViewById(R.id.mycomment);

			passwordchange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onpasswordchange();
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
		new AlertDialog.Builder(getActivity())
		.setTitle("注销")
		.setMessage("注销当前用户")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent);
			}
		})
		.setNegativeButton("返回", null)
		.show();

	}
	
	void onpasswordchange(){
		Intent itnt = new Intent(this.getActivity(),PasswordChangeActivity.class);
		startActivity(itnt);
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
		textView.setTextColor(Color.BLUE);
		textView.setText("用户名："+user.getAccount());
		nicheng.setText("昵称："+user.getName());
	}

	void onFailuer(Call call, Exception ex){
		progress.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setTextColor(Color.RED);
		textView.setText(ex.getMessage());
	}
}
