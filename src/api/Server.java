package api;

import java.net.CookiePolicy;

import android.webkit.CookieManager;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Server {

	static OkHttpClient client;

	static {
		java.net.CookieManager cookieManager = new java.net.CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

		client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
	}



	public static String serverAddress = "http://172.27.0.40:8080/membercenter/";



	// 通过 公开 getsharedClient类提供给其他类使用
	public static OkHttpClient getsharedClient() {
		return client;
	}

	// 方面 省去了其他调用很长的前面部分
	public static Request.Builder requestBuilderWithApi(String api) {
		return new Request.Builder().url(serverAddress + "api/" + api);
	}
}
