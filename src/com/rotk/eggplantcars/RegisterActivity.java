package com.rotk.eggplantcars;



import java.io.IOException;

import com.rotk.eggplantcars.api.Server;
import inputcells.PictureInputCellFragment;
import inputcells.SimpleTextInputCellFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {
	SimpleTextInputCellFragment fragInputCellAccount;
	SimpleTextInputCellFragment fragInputName;
	SimpleTextInputCellFragment fragInputEmailAddress;
	SimpleTextInputCellFragment fragInputCellPassword;
	SimpleTextInputCellFragment fragInputCellPasswordRepeat;
	PictureInputCellFragment fragInputAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		fragInputCellAccount = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragInputEmailAddress = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragInputName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_name);
		fragInputCellPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
		fragInputCellPasswordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);
		fragInputAvatar = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_avatar);

		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		fragInputCellAccount.setLabelText("’Àªß√˚");{
			fragInputCellAccount.setHintText("«Î ‰»Î’Àªß√˚");	
		}


		fragInputCellPassword.setLabelText("√‹¬Î");{
			fragInputCellPassword.setHintText("«Î ‰»Î√‹¬Î");
			fragInputCellPassword.setIsPassword(true);	
		}

		fragInputCellPasswordRepeat.setLabelText("÷ÿ∏¥√‹¬Î");{
			fragInputCellPasswordRepeat.setHintText("«Î÷ÿ∏¥ ‰»Î√‹¬Î");
			fragInputCellPasswordRepeat.setIsPassword(true);	
		}

		fragInputName.setLabelText("Í«≥∆");{
			fragInputName.setHintText("«Î ‰»ÎÍ«≥∆");
		}

		fragInputEmailAddress.setLabelText("µÁ◊”” º˛");{
			fragInputEmailAddress.setHintText("«Î ‰»ÎµÁ◊”” œ‰");
		}
	}

	void submit(){
		String password = fragInputCellPassword.getText();
		String passwordRepeat = fragInputCellPasswordRepeat.getText();

		if(!password.equals(passwordRepeat)){

			new AlertDialog.Builder(RegisterActivity.this)
			.setMessage("√‹¬Î≤ª“ª÷¬")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setNegativeButton("∫√", null)
			.show();

			return;
		}

		password = MD5.getMD5(password);
		
		String account = fragInputCellAccount.getText();
		String name = fragInputName.getText();
		String email = fragInputEmailAddress.getText();

		OkHttpClient client = Server.getsharedClient();

		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("account", account)
				.addFormDataPart("name", name)
				.addFormDataPart("email", email)
				.addFormDataPart("passwordHash", password);
		
		if(fragInputAvatar.getPngData()!=null){
			requestBodyBuilder
			.addFormDataPart(
					"avatar",
					"avatar",
					RequestBody
					.create(MediaType.parse("image/png"),
							fragInputAvatar.getPngData()));
		}

		Request request = Server.requestBuilderWithApi("register")
				.method("post", null)
				.post(requestBodyBuilder.build())
				.build();

		final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
		progressDialog.setMessage("«Î…‘∫Ú...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				final String responseString = arg1.body().string(); 
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						
						try {
							RegisterActivity.this.onResponse(arg0, responseString);
						} catch (Exception e) {
							e.printStackTrace();
							RegisterActivity.this.onFailure(arg0, e);
						}
					}
				});
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						
						RegisterActivity.this.onFailure(arg0, arg1);
					}
				});
			}
		});
	}

	void onResponse(Call arg0, String responseBody){
		new AlertDialog.Builder(this)
		.setTitle("◊¢≤·≥…π¶")
		.setMessage(responseBody)
		.setPositiveButton("∫√", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.show();
	}

	void onFailure(Call arg0, Exception arg1) {
		new AlertDialog.Builder(this)
		.setTitle("◊¢≤· ß∞‹")
		.setMessage(arg1.getLocalizedMessage())
		.setNegativeButton("∫√", null)
		.show();
	}
}