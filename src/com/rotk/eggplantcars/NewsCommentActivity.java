package com.rotk.eggplantcars;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import api.Server;
import entity.News;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsCommentActivity extends Activity{

	News news;
	EditText edit1;
	ImageButton button1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_comment);
		button1=(ImageButton) findViewById(R.id.button1);
		edit1=(EditText) findViewById(R.id.edit1);
		news = (News)getIntent().getSerializableExtra("news");

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClicked();
			}
		});
	}
	void onClicked() {
		// TODO Auto-generated method stub
		String text=edit1.getText().toString();
		OkHttpClient client = Server.getsharedClient();

		MultipartBody requestBody = new MultipartBody.Builder()
				.addFormDataPart("text", text)
				.build();

		Request request = Server.requestBuilderWithApi("News/"+news.getId()+"/comments")
				.method("post", null)
				.post(requestBody)
				.build();

		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("正在发布评论...");
		dlg.show();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final String arg = arg1.body().string();
					NewsCommentActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							NewsCommentActivity.this.onResponse(arg0,arg);
						}
					});
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(NewsCommentActivity.this, arg1.getLocalizedMessage() , Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	protected void onResponse(Call arg0, String response) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(NewsCommentActivity.this)
		.setMessage("发表成功")
		.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		})
		.show();

	}
}
