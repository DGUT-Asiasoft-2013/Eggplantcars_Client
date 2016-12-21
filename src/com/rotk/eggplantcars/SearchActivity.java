package com.rotk.eggplantcars;

import android.R.animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchActivity extends Activity{
	EditText edit;
	ImageButton search;
	ImageButton back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		edit=(EditText) findViewById(R.id.edit_search);
		search=(ImageButton) findViewById(R.id.btn_search);
		back=(ImageButton) findViewById(R.id.btn_back);
		
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchOnClick();
				
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				backOnClick();
			}
		});
	}
	void searchOnClick() {
		// TODO Auto-generated method stub
		String keyword=edit.getText().toString();
		if(keyword!=null && !keyword.isEmpty()){
			Intent itnt=new Intent(SearchActivity.this,SearchListActivity.class);
			itnt.putExtra("keyword",keyword);
			startActivity(itnt);
		}else{
			new AlertDialog.Builder(SearchActivity.this)
			.setMessage("关键词不能为空")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setNegativeButton("好", null)
			.show();
			return ;
		}
		
	}
	void backOnClick() {
		// TODO Auto-generated method stub
		SearchActivity.this.finish();
	}
}
