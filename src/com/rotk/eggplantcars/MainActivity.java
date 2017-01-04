package com.rotk.eggplantcars;

import java.io.IOException;

import api.Server;

import android.R.menu;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Handler handler=new Handler();
		// handler.postDelayed(new Runnable() {
		// private int abcd=0;//内部类对象只能在内部类中访问
		//
		// public void run() {
		// //内部类中访问外部对象
		// //MainActivity.this.------
		// startLoginActivity();
		// }
		// }, 2000);

//		OkHttpClient client = new OkHttpClient();// 创建客户端实例对象
//													// 记得在manifest中家中联网user-permission
//		Request request = new Request.Builder().url("http://172.27.0.21:8080/membercenter/api/hello")// 创建请求
//				.method("GET", null).build();// 请求类型，一般有GET POST PUT DELECT
		OkHttpClient client = Server.getsharedClient();
		Request request = Server.requestBuilderWithApi("hello")
				.method("GET", null).build();
		

		
		
		client.newCall(request).enqueue(new Callback() { // newCall().execute()是同步调用，在这里可能会卡主前端UI画面，所以使用enqueue,将请求插入后台，后台处理后回调一个callback方法返回结果

			@Override
			public void onResponse(Call arg0, final Response arg1) throws IOException {
				MainActivity.this.runOnUiThread(new Runnable() { // 异步线程

					@Override
					public void run() {
						try {
							//Toast.makeText(MainActivity.this,"获取到内容："+ arg1.body().string(), Toast.LENGTH_LONG).show();// 别忘了吧吐司show出来						
						} catch (Exception e) {
							e.printStackTrace();
						}						
						Handler handler=new Handler();
						 handler.postDelayed(new Runnable() {
						
						 public void run() {
						 //内部类中访问外部对象
						 //MainActivity.this.------
						 startLoginActivity();
						 }
						 }, 1500);
						
					}
				});

			}

			@Override
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_LONG).show();

					}
				});
			}
		});

	}

	// 构造启动画面转移方法
	void startLoginActivity() {
		Intent itnt = new Intent(this, LoginActivity.class);
		startActivity(itnt);
		finish();// 转移后结束自己
	}
}
