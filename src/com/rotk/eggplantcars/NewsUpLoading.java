package com.rotk.eggplantcars;

import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import api.Server;
import inputcells.PictureInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsUpLoading extends Activity{
	//News录入界面
	
	Button button;
	EditText editText;
	EditText editTitle;
	PictureInputCellFragment newsinputAvatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_news_load);
		button = (Button)findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.edit1);
		editTitle = (EditText) findViewById(R.id.edit_title);
		newsinputAvatar=(PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_news_avatar);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendContent();
				finish();
			}
		});
	}
	
	
	void sendContent(){
		String text=editText.getText().toString();
		String title=editTitle.getText().toString();
		MultipartBody.Builder body = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("text", text);
		if(newsinputAvatar.getPngData()!=null){
			body.addFormDataPart("avatar", "avatar", 
					RequestBody.create(MediaType.parse("image/png"), 
							newsinputAvatar.getPngData()));
		}
				
		Request request = Server.requestBuilderWithApi("news")
				.post(body.build())
				.build();
		
		Server.getsharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseBody = arg1.body().string();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						NewsUpLoading.this.onSucceed(responseBody);			
					}
				});
			}
			
			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {				
						NewsUpLoading.this.onFailed(arg0, arg1);
					}
				});
				
			}
		});
	}
	
	 void onSucceed(String responseBody) {
		 new AlertDialog.Builder(NewsUpLoading.this)
			.setTitle("上传成功RUA!")
			.setMessage(responseBody)
			.setPositiveButton("Rua!",null)
			.show();
		
	}
	 void onFailed(Call arg0,Exception arg1){
		 new AlertDialog.Builder(NewsUpLoading.this)
			.setTitle("上传失败")
			.setMessage(arg1.getLocalizedMessage())
			.setPositiveButton("Rua!",null)
			.show();
	 }
	
	
	
	
}
