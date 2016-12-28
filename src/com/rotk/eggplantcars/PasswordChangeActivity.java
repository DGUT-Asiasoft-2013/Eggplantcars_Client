package com.rotk.eggplantcars;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import api.Server;
import api.YeServer;
import inputcells.SimpleTextInputCellFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordChangeActivity extends Activity {

	SimpleTextInputCellFragment oldpassword,newpassword,passwordrepeat;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_password_recover_step2);
		oldpassword = (SimpleTextInputCellFragment)getFragmentManager().findFragmentById(R.id.input_verify);
		newpassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		passwordrepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);

		findViewById(R.id.btn_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onchange();
			}			
		});
	}

	private void onchange() {
		if(oldpassword.getText() != null && newpassword.getText() != null && passwordrepeat.getText() != null){
			if((newpassword.getText().equals(passwordrepeat.getText()))){
				gopasswordchange();
			}
			else {
				new AlertDialog.Builder(this)
				.setTitle("错误提示")
				.setMessage("两次密码输入不一致")
				.setPositiveButton("返回", null)
				.show();
			}
		}
		else{
			new AlertDialog.Builder(this).setTitle("错误提示")
			.setMessage("不能有空")
			.setPositiveButton("返回", null)
			.show();
		}
	}

	private void gopasswordchange() {
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("password", MD5.getMD5(oldpassword.getText()))
				.addFormDataPart("passwordHash", MD5.getMD5(newpassword.getText()))
				.build();
		Request request = YeServer.requestBuilderWithApi("passwordchange").post(body).build();

		YeServer.getsharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				final String responseString = arg1.body().string();
				final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);
				if(result){
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(PasswordChangeActivity.this)
							.setTitle("密码修改")
							.setMessage("修改成功")
							.setPositiveButton("确定",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
									overridePendingTransition(0, R.anim.slide_out_bottom);
								}
							})
							.show();
						}
					});

				}
				else{
					runOnUiThread( new Runnable() {
						public void run() {
							new AlertDialog.Builder(PasswordChangeActivity.this)
							.setTitle("密码修改")
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
						new AlertDialog.Builder(PasswordChangeActivity.this)
						.setTitle("密码修改")
						.setMessage("修改失败！连接错误")
						.setNegativeButton("返回",null)
						.show();
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		oldpassword.setLabelText("原密码");{
			oldpassword.setHintText("请输入原密码");
			oldpassword.setIsPassword(true);
		}
		newpassword.setLabelText("新密码");{
			newpassword.setHintText("请输入新密码");
			newpassword.setIsPassword(true);
		}
		passwordrepeat.setLabelText("重复新密码");{
			passwordrepeat.setHintText("请重复输入新密码");
			passwordrepeat.setIsPassword(true);
		}
	}
}
