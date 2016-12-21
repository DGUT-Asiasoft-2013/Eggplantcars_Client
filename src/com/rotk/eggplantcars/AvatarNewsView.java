package com.rotk.eggplantcars;


import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import api.Server;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AvatarNewsView extends View {

	public AvatarNewsView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AvatarNewsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AvatarNewsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	Handler mainThreadHandler = new Handler();;
	Paint paint;
	float srcWidth,srcHeight;
	public void setBitmap(Bitmap bmp) {
		if(bmp==null){
			paint = new Paint();
			paint.setColor(Color.GRAY);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
		    paint.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
		    paint.setAntiAlias(true);
		}else{
			paint = new Paint();
			paint.setShader(new BitmapShader(bmp, TileMode.REPEAT, TileMode.REPEAT));
			paint.setAntiAlias(true);
			srcWidth=bmp.getWidth();
			srcHeight=bmp.getHeight();
		}
		
		invalidate();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (paint != null) {
			canvas.save();
			float dstWidth = getWidth();
			float dstHeight= getHeight();
			
			float scaleX = srcWidth/dstWidth;
			float scaleY = srcHeight/dstHeight;
			
			canvas.scale(1/scaleX, 1/scaleY);
			RectF rect = new RectF(0, 0,250, 250);
			canvas.drawRect(rect, paint);
			canvas.restore();
		}

	}
	
	
	
	public void load(String url){
		OkHttpClient client = Server.getsharedClient();
		
		Request request = new Request.Builder()
				.url(url)
				.method("get", null)
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try {
					byte[] bytes = arg1.body().bytes();
					final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
					mainThreadHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setBitmap(bitmap);
						}
					});
				} catch (Exception e) {
					mainThreadHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							setBitmap(null);
						}
					});
				}
				
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				mainThreadHandler.post(new Runnable() {
					public void run() {
						setBitmap(null);
					}
});
			}
		});
	}

	public void load(User user) {
		// TODO Auto-generated method stub
		load(Server.serverAddress+user.getAvatar());
	}
}
