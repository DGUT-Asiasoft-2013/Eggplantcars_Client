package com.rotk.eggplantcars;

import java.io.IOException;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import api.Server;
import inputcells.PictureInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewActivity extends Activity{
	EditText title;
	EditText carmodel;
	EditText buydate;
	EditText price;
	EditText text;
	EditText traveldistance;
	PictureInputCellFragment dealAvatar;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);
		title=(EditText)findViewById(R.id.title);
		carmodel=(EditText)findViewById(R.id.carmodel);
		buydate=(EditText)findViewById(R.id.buydate);
		traveldistance=(EditText)findViewById(R.id.traveldistance);
		price=(EditText)findViewById(R.id.price);
		text=(EditText)findViewById(R.id.text);
		dealAvatar = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.dealavatar);
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clicked();
			}
		});
	}
	void clicked(){
		String title=NewActivity.this.title.getText().toString();
		String carmodel=NewActivity.this.carmodel.getText().toString();
		String buydate=NewActivity.this.buydate.getText().toString();
		String traveldistance=NewActivity.this.traveldistance.getText().toString();
		String price=NewActivity.this.price.getText().toString();
		String text=NewActivity.this.text.getText().toString();

		OkHttpClient client=Server.getsharedClient();
		
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("title", title)
				.addFormDataPart("carModel", carmodel)
				.addFormDataPart("buyDate", buydate)
				.addFormDataPart("travelDistance", traveldistance)
				.addFormDataPart("price", price)
				.addFormDataPart("text", text);
		if(dealAvatar.getPngData()!=null){
			requestBodyBuilder
			.addFormDataPart(
					"dealAvatar",
					"avatar",
					RequestBody
					.create(MediaType.parse("image/png"),
							dealAvatar.getPngData()));
		}
				
		okhttp3.Request Request=Server.requestBuilderWithApi("deal")
				.method("post",null)
				.post(requestBodyBuilder.build())
				.build();

		client.newCall(Request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final String arg = arg1.body().string();
					NewActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							NewActivity.this.onResponse(arg0,arg);
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
				NewActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(NewActivity.this, arg1.getLocalizedMessage() , Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	protected void onResponse(Call arg0, String response) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(NewActivity.this)
		.setMessage("已成功发布交易")
		.setPositiveButton("确认",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_out_bottom,0);
			}
		})
		.show();

	}
}
