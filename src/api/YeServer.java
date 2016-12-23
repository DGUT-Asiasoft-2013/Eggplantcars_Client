package api;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class YeServer {

	// 通过 公开 getsharedClient类提供给其他类使用
	public static OkHttpClient getsharedClient() {
		return Server.getsharedClient();
	}

	// 方面 省去了其他调用很长的前面部分
	public static Request.Builder requestBuilderWithApi(String api) {
		return new Request.Builder().url(Server.serverAddress + "ye/" + api);
	}
}