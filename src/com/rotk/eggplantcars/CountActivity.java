package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
	TextView buyer_phone;
	TextView countprice;
	ListView list;

	Money money;
	User user;
	Address a;
	Button buy;

	String name;
	String adrs;
	String phone;
	int count = 0;  

	ArrayList<ShoppingCar> data = new ArrayList<ShoppingCar>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_count);
		//取得数据
		data=(ArrayList<ShoppingCar>) getIntent().getSerializableExtra("data");
		name=(String) getIntent().getExtras().get("name");
		adrs=(String) getIntent().getExtras().get("address");
		phone=(String) getIntent().getExtras().get("phone");
		//获取控件
		countprice=(TextView) findViewById(R.id.count);
		back=(ImageButton) findViewById(R.id.back);
		address=(ImageButton) findViewById(R.id.select_address);
		buyer_name=(TextView) findViewById(R.id.buyer_name);
		buyer_address=(TextView) findViewById(R.id.buyer_address);
		buyer_phone=(TextView) findViewById(R.id.buyer_phone);
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
				itnt.putExtra("data", data);
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
			.setPositiveButton("确定", null)
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
								order();
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


	void order(){
		ProgressDialog dlg = new ProgressDialog(this);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage("正在为您下单，请稍候...");
		dlg.show();

		for (int i = 0; i < data.size(); i++) { 
			Integer deal_id=data.get(i).getId().getDeal().getId();
			delectShoppingCar(deal_id);
			delectDeal(deal_id);
			addOrder(data,i);
		}

		dlg.dismiss();
		new AlertDialog.Builder(CountActivity.this)
		.setTitle("提示")
		.setMessage("下单成功")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				order();
				finish();
				overridePendingTransition(0, R.anim.slide_out_bottom);
			}
		})
		.show();
	}

	void delectShoppingCar(Integer deal_id) {
		// TODO Auto-generated method stub
		OkHttpClient client=Server.getsharedClient();

		Request Request=Server.requestBuilderWithApi("myshoppingcar/"+deal_id+"/delect")
				.get()
				.build();
		client.newCall(Request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

						}
					});
				}catch (final Exception e) {
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
				new AlertDialog.Builder(CountActivity.this)
				.setMessage("链接失败，请检查您的网络")
				.show();
			}
		});
	}

	void delectDeal(Integer deal_id) {
		// TODO Auto-generated method stub
		OkHttpClient client=Server.getsharedClient();

		Request Request=Server.requestBuilderWithApi("deal/"+deal_id+"/delect")
				.get()
				.build();
		client.newCall(Request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

						}
					});
				}catch (final Exception e) {
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
				new AlertDialog.Builder(CountActivity.this)
				.setMessage("链接失败，请检查您的网络")
				.show();
			}
		});
	}

	void addOrder(ArrayList<ShoppingCar> data, int i) {
		// TODO Auto-generated method stub
		String putaddress=(String) buyer_address.getText();
		String putname=(String) buyer_name.getText();
		String putphone=(String) buyer_phone.getText();

		String title=data.get(i).getId().getDeal().getTitle();
		String carmodel=data.get(i).getId().getDeal().getCarModel();
		String buydate=data.get(i).getId().getDeal().getBuyDate();
		String traveldistance=data.get(i).getId().getDeal().getTravelDistance();
		String price=data.get(i).getId().getDeal().getPrice();
		String text=data.get(i).getId().getDeal().getText();
		String orderAvatar=data.get(i).getId().getDeal().getDealAvatar();

		OkHttpClient client=Server.getsharedClient();

		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("buyerAddress", putaddress)
				.addFormDataPart("buyerName", putname)
				.addFormDataPart("buyerPhone", putphone)
				.addFormDataPart("orderTitle", title)
				.addFormDataPart("orderCarModel", carmodel)
				.addFormDataPart("orderBuyDate", buydate)
				.addFormDataPart("orderTravelDistance", traveldistance)
				.addFormDataPart("orderPrice", price)
				.addFormDataPart("orderText", text)
		        .addFormDataPart("orderAvatar",orderAvatar);
		
		okhttp3.Request Request=Server.requestBuilderWithApi("order")
				.method("post",null)
				.post(requestBodyBuilder.build())
				.build();
		
		client.newCall(Request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try{
					final String arg = arg1.body().string();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
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
						Toast.makeText(CountActivity.this, arg1.getLocalizedMessage() , Toast.LENGTH_LONG).show();
					}
				});
			}
		});
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

		if(adrs==null&&name==null&&phone==null){//判断地址是否为空
			getAddress();//获取最晚地址
		}else{
			buyer_address.setText(adrs);
			buyer_name.setText(name);
			buyer_phone.setText(phone);
		}



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
		buyer_address.setText(address.getText());
		buyer_name.setText(address.getName());
		buyer_phone.setText(address.getPhoneNumber());
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
