package com.rotk.eggplantcars;

import java.io.IOException;

import com.cloudage.membercenter.entity.Record;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.YeServer;
import entity.Money;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class MyMoneyActivity extends Activity {
	//钱包页面
	TextView name;
	TextView mycash;
	LinearLayout address; //我的收货地址
	Money money;
	User user;
	LinearLayout deposit;
	LinearLayout record;
	int cash = 0;
	int change = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymoney);

		name = (TextView)findViewById(R.id.money_name);
		mycash = (TextView)findViewById(R.id.mycash);
		deposit = (LinearLayout)findViewById(R.id.deposit);
		address = (LinearLayout) findViewById(R.id.address);
		record = (LinearLayout)findViewById(R.id.record);
		
		user = (User)getIntent().getSerializableExtra("user");
		
		deposit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//充值
				godeposit();
			}
		});
		
		address.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyMoneyActivity.this,MyAddress.class);
				intent.putExtra("user", user);
				startActivity(intent);
				
			}
		});
		
		record.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyMoneyActivity.this,RecordActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
			}
		});
	}
	//充值方法实现
	private void godeposit() {
		Intent intent = new Intent(this,DepositActivity.class);
		intent.putExtra("money", money);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkMoneyExsists();
	}

	private void checkMoneyExsists() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(user.getId()+"/isMoneyed").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					if(result){
						//获取当前用户的钱包数据
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//								new AlertDialog.Builder(MyMoneyActivity.this).setMessage(responseString)
								//								.show();
								getMyMoneyAccount();
							}
						});

					}
					else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								new AlertDialog.Builder(MyMoneyActivity.this)
								.setTitle("提示")
								.setMessage("用户还没开通钱包功能，点击确定开通")
								.setNegativeButton("返回", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										finish();
									}
								})
								.setPositiveButton("确定", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										//创建用户钱包
										MyMoneyActivity.this.createMyMoneyAccount();
										checkMoneyExsists();
									}
								})
								.show();

							}
						});				
					}

				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(MyMoneyActivity.this).setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, final IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(MyMoneyActivity.this).setMessage(e.getMessage())
						.show();
					}
				});				
			}
		});
	}

	//得到账户的钱包信息
	private void getMyMoneyAccount() {
		Request request = YeServer.requestBuilderWithApi(user.getId()+"/Moneys")
				.method("get", null)
				.build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try {
					final String responseString = arg1.body().string();
					final Money data = new ObjectMapper().readValue(responseString, Money.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							money = data;
							onResponse(arg0,data);

							//							new AlertDialog.Builder(MyMoneyActivity.this).setMessage(responseString)
							//							.show();
						}
						private void onResponse(Call arg0, Money data) {
							// TODO Auto-generated method stub
							name.setText("用户名："+data.getUser().getName());
							mycash.setText(String.valueOf(data.getCash())+".00");
						}
					});	
				} catch (Exception e) {
					e.printStackTrace();
				}


			}


			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MyMoneyActivity.this,"拿取数据失败", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});

	}


	//创建客户钱包信息
	private void createMyMoneyAccount() {
		// TODO Auto-generated method stub
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash));//传任意类型的方法String.valueOf(参数)

		Request request = YeServer.requestBuilderWithApi("creatMoneyUser")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(MyMoneyActivity.this,"开通成功", Toast.LENGTH_LONG).show();
					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(MyMoneyActivity.this).setMessage(arg1.getMessage())
						.show();
					}
				});				

			}
		});	
	}
}
