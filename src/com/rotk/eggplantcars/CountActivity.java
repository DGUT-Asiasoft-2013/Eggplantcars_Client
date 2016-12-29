package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Fragment.pages.MyFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import api.Server;
import api.YeServer;
import entity.Address;
import entity.Money;
import entity.ShoppingCar;
import entity.User;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountActivity extends Activity{
	ImageButton back;
	ImageButton address;
	TextView buyer_name;
	TextView buyer_address;
	TextView countprice;
	ListView list;

	Money money;
	User user;
	Address a;
	Button buy;
	int count = 0;  

	ArrayList<ShoppingCar> data = new ArrayList<ShoppingCar>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_count);
		data=(ArrayList<ShoppingCar>) getIntent().getSerializableExtra("data");
		countprice=(TextView) findViewById(R.id.count);
		back=(ImageButton) findViewById(R.id.back);
		address=(ImageButton) findViewById(R.id.select_address);
		buyer_name=(TextView) findViewById(R.id.buyer_name);
		buyer_address=(TextView) findViewById(R.id.buyer_address);
		buy=(Button) findViewById(R.id.buy);

		list=(ListView)findViewById(R.id.list);
		list.setAdapter(listAdapter);
		back.setOnClickListener(new OnClickListener() {//返回按键

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		address.setOnClickListener(new OnClickListener() {//选择地址按键

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent itnt=new Intent(CountActivity.this,SelectAddressActivity.class);
				startActivity(itnt);
			}
		});
		buy.setOnClickListener(new OnClickListener() {//购物按键

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buyClick();
			}
		});
	}

	//结算
	void buyClick() {

		changeMoney();//金额交易变化
	}


	//金额交易变化
	private void changeMoney() {
		// TODO Auto-generated method stub
		final EditText et = new EditText(this);
		et.setBackgroundColor(Color.WHITE);
		//在Activity中设置passowrd
		et.setTransformationMethod(PasswordTransformationMethod.getInstance());
		new AlertDialog.Builder(this).setTitle("请输入密码")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setView(et)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						//金额变化
						String password = String.valueOf(et.getText());
						password = MD5.getMD5(password);
						gochangemoney(password);
					}
				});
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}

	//金额变化
	private void gochangemoney(String password) {
		// TODO Auto-generated method stub
		int cash =  money.getCash() - count;

		if(cash<0){
			new AlertDialog.Builder(this)
			.setTitle("余额不足！")
			.setMessage("请及时充值")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						public void run() {
							CountActivity.this.finish();
							Intent intent = new Intent(CountActivity.this,DepositActivity.class);
							intent.putExtra("money", money);
							startActivity(intent);
						}
					});
				}
			})
			.show();
		}
		else{
			
			//传任意类型的方法String.valueOf(参数)
			MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
					.addFormDataPart("cash", String.valueOf(cash))
					.addFormDataPart("password", password);

			Request request = YeServer.requestBuilderWithApi("CashDeposit")
					.method("post", null)
					.post(requestBodyBuilder.build())
					.build();
			YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, final Response arg1) throws IOException {

					final boolean result = new ObjectMapper().readValue(arg1.body().string(), Boolean.class);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if(result){
								new AlertDialog.Builder(CountActivity.this)
								.setTitle("提示")
								.setMessage("购买成功")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										finish();
										overridePendingTransition(0, R.anim.slide_out_bottom);
									}
								})
								.show();
							}
							else {
								Toast.makeText(CountActivity.this,"购买失败,密码错误!", Toast.LENGTH_LONG).show();
							}
						}
					});				
				}

				@Override
				public void onFailure(Call arg0, final IOException arg1) {
					// TODO Auto-generated method stub

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new AlertDialog.Builder(CountActivity.this)
							.setMessage(arg1.getMessage())
							.show();
						}
					});				
				}
			});	
		}
	}


	//得到我的余额
	private void getmymoney() {
		// TODO Auto-generated method stub
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
						Toast.makeText(CountActivity.this,"拿取数据失败", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doSum(data);//计算总价
		getAddress();//获取最晚地址
		getUser();//得到当前用户

	}

	//得到当前用户
	private void getUser() {
		// TODO Auto-generated method stub
		OkHttpClient client = Server.getsharedClient();
		Request request = Server.requestBuilderWithApi("me")
				.method("get", null)
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {
					final User u = new ObjectMapper().readValue(arg1.body().bytes(), User.class);
					runOnUiThread(new Runnable() {
						public void run() {
							user = u;
							getmymoney();//得到我的余额
						}
					});					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {

						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {

					}
				});
			}
		});
	}

	//得到收货地址
	void getAddress() {
		// TODO Auto-generated method stub
		Request request =Server.requestBuilderWithApi("getlastaddress")
				.get()
				.build();
		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final Address address = new ObjectMapper().readValue(arg1.body().bytes(), Address.class);
					runOnUiThread(new Runnable() {
						public void run() {
							a=address;
							setAddress(arg0,address);//设置地址文本
						}

					});					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(CountActivity.this)
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

	private void setAddress(Call arg0, Address address) {
		// TODO Auto-generated method stub
		buyer_address.setText("收货地址："+address.getText());
		buyer_name.setText("收件人："+address.getName());
	}

	void doSum(List<ShoppingCar> data) {
		// TODO Auto-generated method stub
		for (int i = 0; i < data.size(); i++) {  
			int price = Integer.parseInt(data.get(i).getId().getDeal().getPrice());
			count += price;
		}  
		countprice.setText("￥"+count); 
	}  
	BaseAdapter listAdapter=new BaseAdapter() {

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.listview, null);	
			}else{
				view = convertView;
			}

			TextView textTitle = (TextView) view.findViewById(R.id.title);
			TextView textPrice = (TextView) view.findViewById(R.id.text);
			AvatarNewsView avatar=(AvatarNewsView) view.findViewById(R.id.avatar);

			avatar.load(Server.serverAddress+data.get(position).getId().getDeal().getDealAvatar());
			textTitle.setText(data.get(position).getId().getDeal().getTitle());
			textPrice.setText("￥"+data.get(position).getId().getDeal().getPrice());

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
