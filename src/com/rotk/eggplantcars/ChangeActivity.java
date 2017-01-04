package com.rotk.eggplantcars;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import api.Server;
import entity.User;
import inputcells.AvatarView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChangeActivity extends Activity{
	User user;
	TextView account;
	TextView name;
	TextView email;
	FrameLayout btn_changename;
	FrameLayout btn_changeemail;
	AvatarView avatar;
	ImageButton back;

	final int REQUESTCODE_CAMERA = 1;
	final int REQUESTCODE_ALBUM = 2;
	byte[] pngData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change);
		user = (User)getIntent().getSerializableExtra("user");
		account=(TextView) findViewById(R.id.account);
		name=(TextView) findViewById(R.id.name);
		email=(TextView) findViewById(R.id.email);
		btn_changeemail=(FrameLayout) findViewById(R.id.btn_changeemail);
		btn_changename=(FrameLayout) findViewById(R.id.btn_changename);
		avatar=(AvatarView) findViewById(R.id.my_avatar);
		back=(ImageButton) findViewById(R.id.back);

		account.setText("账户："+user.getAccount());
		name.setText("昵称："+user.getName());
		email.setText("邮箱："+user.getEmail());
		avatar.load(Server.serverAddress+user.getAvatar());

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btn_changeemail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent intent = new Intent(ChangeActivity.this,ChangeEmailActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
			}
		});

		btn_changename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent intent = new Intent(ChangeActivity.this,ChangeNameActivity.class);
				intent.putExtra("user",user);
				startActivity(intent);
			}
		});

		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onImageViewClicked();
			}
		});
	}

	public void onImageViewClicked() {
		String[] items = { "拍照", "相册" };
		new AlertDialog.Builder(ChangeActivity.this)
		.setTitle("选择头像路径")
		.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					takePhoto();
					break;
				case 1:
					pickFromAlbum();
					break;

				default:
					break;
				}

			}
		})
		.setNegativeButton("取消", null)
		.show();
	}

	public void takePhoto() {
		// 两种方式 下面使用简单的一种
		Intent itnt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(itnt, REQUESTCODE_CAMERA);// 定义1为请求码 requestCode

	}

	public void pickFromAlbum() {
		// Intent itnt=new Intent(Intent.ACTION_PICK);
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (requestCode == REQUESTCODE_CAMERA) {
			// Log.d("camera capture", data.getDataString());
			// Log.d("camera capture", data.getExtras().keySet().toString());
			// Toast.makeText(getActivity(), data.getDataString(),
			// Toast.LENGTH_LONG).show();
			Bitmap bmp = (Bitmap) data.getExtras().get("data");
			saveBitmap(bmp);
		} else if (requestCode == REQUESTCODE_ALBUM) {
			try {
				Bitmap bmp = MediaStore.Images.Media.getBitmap(ChangeActivity.this.getContentResolver(), data.getData());
				saveBitmap(bmp);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	void saveBitmap(Bitmap bmp){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, baos);
		pngData=baos.toByteArray();
		goChangeAvatar();
	}

	public byte[] getPngData(){
		return pngData;
	}

	void goChangeAvatar() {
		// TODO Auto-generated method stub
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);
		
		if(getPngData()!=null){
			requestBodyBuilder
			.addFormDataPart(
					"avatar",
					"avatar",
					RequestBody
					.create(MediaType.parse("image/png"),getPngData()));
		}
		Request request = Server.requestBuilderWithApi("avatarchange")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();

		Server.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
				if(result){
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(ChangeActivity.this)
							.setMessage("修改成功")
							.setPositiveButton("确定",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									avatar.load(Server.serverAddress+user.getAvatar());
								}
							})
							.show();
						}
					});

				}
				else{
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(ChangeActivity.this)
							.setMessage("修改失败")
							.setNegativeButton("返回",null)
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(ChangeActivity.this)
						.setMessage("修改失败！连接错误")
						.setNegativeButton("返回",null)
						.show();
					}
				});
			}
		});
	}
}


