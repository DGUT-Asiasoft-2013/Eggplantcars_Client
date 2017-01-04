package com.rotk.eggplantcars;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.Server;
import api.YeServer;
import entity.Money;
import entity.OrderForm;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailsActivity extends Activity {

	OrderForm order;
	ImageButton btn_back;
	Button btn_get;
	Button btn_rejected;
	LinearLayout deald;
	Money buyermoney;
	Money sellermoney;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_orderdetails);

		order = (OrderForm)getIntent().getSerializableExtra("order");
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		deald = (LinearLayout)findViewById(R.id.deald);
		btn_get = (Button)findViewById(R.id.btn_get);
		btn_rejected = (Button)findViewById(R.id.btn_rejected);//退款

		//返回
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		//申请退款
		btn_rejected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mess = null;
				if(order.getType().equals("买家已下单")){
					mess = "是否申请退款";
				}
				if(order.getType().equals("买家申请退款")){
					mess = "是否取消退单";
				}
				new AlertDialog.Builder(OrderDetailsActivity.this)
				.setTitle("提示")
				.setMessage(mess)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(order.getType().equals("买家申请退款")){
							rejectedtype("买家已下单");
						}
						if(order.getType().equals("买家已下单")){
							rejectedtype("买家申请退款");
						}
					}

				})
				.setNegativeButton("返回", null)
				.show();

			}
		});

		//收货
		btn_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				recipient();//收货方法
			}
		});

		deald.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(OrderDetailsActivity.this,DetailsActivity.class);
				intent.putExtra("data", order.getDeal());
				startActivity(intent);
			}
		});
	}

	private void rejectedtype(final String type) {
		// TODO Auto-generated method stub
		int orderid = order.getId();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("orderid", String.valueOf(orderid))
				.addFormDataPart("type", type);

		Request request = YeServer.requestBuilderWithApi("changeorder")
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
							getorderbyid();//得到订单更新信息
							if(type.equals("买家已下单")){
								Toast.makeText(OrderDetailsActivity.this,"取消退单成功，等待卖家接收订单", Toast.LENGTH_LONG).show();
							}
							if(type.equals("买家申请退款")){
								Toast.makeText(OrderDetailsActivity.this,"申请退款成功，等待卖家取消订单", Toast.LENGTH_LONG).show();
							}
							
						}
						else{
							new AlertDialog.Builder(OrderDetailsActivity.this)
							.setMessage("失败")
							.show();
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
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//收货方法
	private void recipient() {

		if(order.getType().equals("已受理")){
			changetype("订单完成");
		}else {
			return ;
		}
	}

	private void changetype(String type) {

		int orderid = order.getId();
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("orderid", String.valueOf(orderid))
				.addFormDataPart("type", type);

		Request request = YeServer.requestBuilderWithApi("changeorder")
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

							final EditText et = new EditText(OrderDetailsActivity.this);
							et.setBackgroundColor(Color.WHITE);
							//在Activity中设置passowrd
							et.setTransformationMethod(PasswordTransformationMethod.getInstance());
							new AlertDialog.Builder(OrderDetailsActivity.this).setTitle("请输入密码")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(et)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									runOnUiThread(new Runnable() {
										public void run() {
											//金额变化
											String password = String.valueOf(et.getText());
											password = MD5.getMD5(password);
											getmoney(password);
										}
									});
								}
							})
							.setNegativeButton("取消", null)
							.show();

						}
						else{
							new AlertDialog.Builder(OrderDetailsActivity.this)
							.setMessage("收货失败")
							.show();
						}
					}

				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {


				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//收账
	private void getmoney(String password) {
		// TODO Auto-generated method stub
		//传任意类型的方法String.valueOf(参数)
		int cash = sellermoney.getCash()+ Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("cash", String.valueOf(cash))
				.addFormDataPart("sellerid", String.valueOf(order.getDeal().getSellerId()))
				.addFormDataPart("password", password);

		Request request = YeServer.requestBuilderWithApi("CashOrder")
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
							saverecord();//保存消费记录
						}
						else {
							Toast.makeText(OrderDetailsActivity.this,"收货失败!", Toast.LENGTH_LONG).show();
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
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}

	//得到订单更新信息
	private void getorderbyid() {

		Request request = YeServer.requestBuilderWithApi(String.valueOf(order.getId())+"/getorderbyid").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final OrderForm data = new ObjectMapper().readValue(a, OrderForm.class);
					OrderDetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							order = data;
							onResume();
						}
					});
				} catch (final Exception e) {

					OrderDetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(OrderDetailsActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	//保存消费记录
	private void saverecord() {
		// TODO Auto-generated method stub
		String record_type = "收账";
		String text = "订单交易完成";
		int my_cash = sellermoney.getCash() + Integer.valueOf(order.getDeal().getPrice());
		int record_cash =Integer.valueOf(order.getDeal().getPrice());
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.addFormDataPart("record_type", record_type)
				.addFormDataPart("text", text)
				.addFormDataPart("my_cash", String.valueOf(my_cash))
				.addFormDataPart("record_cash", String.valueOf(record_cash))
				.addFormDataPart("sellerid", order.getSeller().getId());

		Request request = YeServer.requestBuilderWithApi("sellerrecordsave")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {


				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getorderbyid();//得到订单更新信息
					}
				});				
			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new AlertDialog.Builder(OrderDetailsActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});				
			}
		});	
	}


	//得到卖家的余额
	private void getselletmoney() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(order.getDeal().getSellerId()+"/Moneys")
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
							sellermoney = data;
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
						Toast.makeText(OrderDetailsActivity.this,"拿取数据失败", Toast.LENGTH_LONG).show();
					}
				});		
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getselletmoney();

		TextView order_name = (TextView)findViewById(R.id.order_name);
		order_name.setText(order.getDeal().getTitle());

		TextView order_money = (TextView)findViewById(R.id.order_money);
		order_money.setText("￥"+order.getDeal().getPrice());

		TextView order_date = (TextView)findViewById(R.id.order_date);
		String dateStr = DateFormat.format("yyyy-MM-dd", order.getCreateDate()).toString();
		order_date.setText(dateStr);

		TextView order_type = (TextView)findViewById(R.id.order_type);
		order_type.setText(order.getType());

		AvatarNewsView avatar = (AvatarNewsView)findViewById(R.id.avatar);
		avatar.load(Server.serverAddress+order.getDeal().getDealAvatar());

		TextView buyer_type = (TextView)findViewById(R.id.buyer_type);

		if(order.getType().equals("已受理")){
			btn_get.setVisibility(View.VISIBLE);
			btn_get.setClickable(true);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("卖家已受理，请等待卖家联系");
		}
		if(order.getType().equals("订单完成")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("买家已确认收货，订单完成");
		}
		if(order.getType().equals("买家申请退款")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.VISIBLE);
			btn_rejected.setClickable(true);
			btn_rejected.setText("取消退单");
			buyer_type.setText("已申请退款，等待卖家取消订单");
		}
		if(order.getType().equals("买家已下单")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.VISIBLE);
			btn_rejected.setClickable(true);
			btn_rejected.setText("申请退款");
			buyer_type.setText("已下单——等待卖家联系");
		}
		if(order.getType().equals("订单已取消")){
			btn_get.setVisibility(View.INVISIBLE);
			btn_get.setClickable(false);
			btn_rejected.setVisibility(View.INVISIBLE);
			btn_rejected.setClickable(false);
			buyer_type.setText("卖家已取消订单，请查收退款");
		}
	}

}
