package com.rotk.eggplantcars;

import java.io.IOException;

import api.Server;
import Fragment.PasswordRecoverStep1Fragment;
import Fragment.PasswordRecoverStep1Fragment.OnGoNextListener;
import Fragment.PasswordRecoverStep2Fragment;
import Fragment.PasswordRecoverStep2Fragment.OnSubmitClickedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordRecoverActivity extends Activity {

	PasswordRecoverStep1Fragment step1 = new PasswordRecoverStep1Fragment();
	PasswordRecoverStep2Fragment step2 = new PasswordRecoverStep2Fragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_password_recover);

		step1.setOnGoNextListener(new OnGoNextListener() {

			@Override
			public void onGoNext() {
				goStep2();
			}
		});

		step2.setOnSubmitClickedListener(new OnSubmitClickedListener() {

			@Override
			public void onSubmitClicked() {
				goSubmit();
			}
		});

		getFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
	}

	void goStep2(){

		getFragmentManager()
		.beginTransaction()	
		.setCustomAnimations(
				R.animator.slide_in_right,
				R.animator.slide_out_left,
				R.animator.slide_in_left,
				R.animator.slide_out_right)
		.replace(R.id.container, step2)
		.addToBackStack(null)
		.commit();
	}

	void goSubmit(){
		OkHttpClient client = Server.getsharedClient();
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("email", step1.getText())
				.addFormDataPart("passwordHash", MD5.getMD5(step2.getText()))
				.build();
		Request request = Server.requestBuilderWithApi("passwordrecover").post(body).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {

							PasswordRecoverActivity.this.onResponseYes();
						} catch (Exception e) {
							e.printStackTrace();
							PasswordRecoverActivity.this.onFailureYes();
						}
					}
				});


			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						PasswordRecoverActivity.this.onFailureYes();

					}
				});


			}
		});
	}

	void onResponseYes(){
		new AlertDialog.Builder(PasswordRecoverActivity.this)
		.setMessage("请求成功")//arg1.body().string()放后台
		.setNegativeButton("好", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent itnt = new Intent(PasswordRecoverActivity.this, LoginActivity.class);
				startActivity(itnt);
				finish();

			}
		})
		.show();

	}

	void onFailureYes(){
		new AlertDialog.Builder(PasswordRecoverActivity.this)
		.setMessage("请求失败")
		.setNegativeButton("好", null)
		.show();
	}
}

