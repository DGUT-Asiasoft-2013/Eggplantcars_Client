package com.rotk.eggplantcars;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import api.Server;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeEmailActivity extends Activity{
	ImageButton back;
	Button btn_post;
	EditText changeemail;
	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeemail);
		back=(ImageButton) findViewById(R.id.back);
		btn_post=(Button) findViewById(R.id.btn_post);
		changeemail=(EditText) findViewById(R.id.changeemail);
		user = (User)getIntent().getSerializableExtra("user");

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent intent = new Intent(ChangeEmailActivity.this,ChangeActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
			}
		});

		btn_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(changeemail.getText()!=null && !changeemail.getText().toString().isEmpty()){
					goPost();
				}else{
					new AlertDialog.Builder(ChangeEmailActivity.this)
					.setMessage("昵称不能为空")
					.setNegativeButton("确定",null)
					.show();
				}
			}
		});
	}

	void goPost() {
		// TODO Auto-generated method stub
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("email",changeemail.getText().toString())
				.build();
		Request request = Server.requestBuilderWithApi("emailchange")
				.post(body)
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
				if(result){
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(ChangeEmailActivity.this)
							.setMessage("修改成功")
							.setPositiveButton("确定",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
									overridePendingTransition(0, R.anim.slide_out_bottom);
								}
							})
							.show();
						}
					});

				}
				else{
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(ChangeEmailActivity.this)
							.setMessage("修改失败")
							.setNegativeButton("返回",null)
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(ChangeEmailActivity.this)
						.setMessage("修改失败！连接错误")
						.setNegativeButton("返回",null)
						.show();
					}
				});
			}
		});
	}

}
