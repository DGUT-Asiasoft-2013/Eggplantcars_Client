package com.rotk.eggplantcars;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import api.Server;
import api.YeServer;
import entity.OrderForm;
import entity.User;
import inputcells.AvatarNewsView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MyOrdersActivity extends Activity {

	User user;
	ImageButton btn_back;
	List<OrderForm> data;

	ListView listview;
	int i = 1;
	Button btn_buyer;
	Button btn_seller;
	TextView text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_myorders);

		user = (User)getIntent().getSerializableExtra("user");

		listview = (ListView)findViewById(R.id.list_orders);
		listview.setAdapter(listAdapter);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		btn_buyer = (Button)findViewById(R.id.btn_buyer);
		btn_seller = (Button)findViewById(R.id.btn_seller);
		text = (TextView)findViewById(R.id.text);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onNewsItemClicked(position);
			}

		});

		//返回
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		//我购买的
		btn_buyer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buyer();
			    i = 1;
			    text.setText("买家订单");
			}

		});
		//我卖出的
		btn_seller.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				seller();
				i = 2;
				text.setText("卖家订单");
			}
		});
	}


	private void seller() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(String.valueOf(user.getId())+"/sellergetorder").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final List<OrderForm> data = new ObjectMapper().readValue(a,
							new TypeReference<List<OrderForm>>() {
					});
					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							MyOrdersActivity.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {

					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(MyOrdersActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(MyOrdersActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	private void buyer() {
		// TODO Auto-generated method stub
		Request request = YeServer.requestBuilderWithApi(String.valueOf(user.getId())+"/buyergetorder").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final List<OrderForm> data = new ObjectMapper().readValue(a,
							new TypeReference<List<OrderForm>>() {
					});
					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							MyOrdersActivity.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {

					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(MyOrdersActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(MyOrdersActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		text.setText("买家订单");
		i = 1;
		Request request = YeServer.requestBuilderWithApi(String.valueOf(user.getId())+"/buyergetorder").get().build();
		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				try {
					String a = arg1.body().string();
					final List<OrderForm> data = new ObjectMapper().readValue(a,
							new TypeReference<List<OrderForm>>() {
					});
					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							//							try {
							//								//new AlertDialog.Builder(MyOrdersActivity.this).setMessage(arg1.body().string()).show();
							//							} catch (IOException e) {
							//								// TODO Auto-generated catch block
							//								e.printStackTrace();
							//							}

							MyOrdersActivity.this.data = data;
							listAdapter.notifyDataSetInvalidated();
						}
					});
				} catch (final Exception e) {

					MyOrdersActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(MyOrdersActivity.this).setMessage(e.getMessage()).show();

						}
					});
				}

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(MyOrdersActivity.this).setMessage(arg1.getMessage()).show();

					}
				});

			}
		});
	}

	private void onNewsItemClicked(int position) {
		// TODO Auto-generated method stub

		if(i == 1){
			OrderForm order = data.get(position);
			Intent intent = new Intent(MyOrdersActivity.this, OrderDetailsActivity.class);	
			intent.putExtra("order", order);
			startActivity(intent);
		}
			
		if(i == 2){
			OrderForm order = data.get(position);
			Intent intent = new Intent(MyOrdersActivity.this, SellerActivity.class);	
			intent.putExtra("order", order);
			startActivity(intent);
		}
		
	}

	//适配器
	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.list_order, null);
			} else {
				view = convertView;
			}


			TextView order_type = (TextView) view.findViewById(R.id.order_type);//交易状态
			TextView order_money = (TextView) view.findViewById(R.id.order_money);//交易金额
			TextView order_date = (TextView) view.findViewById(R.id.order_date);//交易时间
			TextView order_name = (TextView) view.findViewById(R.id.order_name);//交易物品名称
			AvatarNewsView avatar = (AvatarNewsView)view.findViewById(R.id.avatar);

			OrderForm order = data.get(position);

			order_type.setText(String.valueOf(order.getType()));
			order_money.setText("￥"+String.valueOf(order.getDeal().getPrice()));
			String dateStr = DateFormat.format("yyyy-MM-dd", order.getCreateDate()).toString();
			order_date.setText(dateStr);
			order_name.setText(String.valueOf(order.getDeal().getTitle()));
			avatar.load(Server.serverAddress+order.getDeal().getDealAvatar());

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
			return data == null ? 0 : data.size();
		}
	};
}
