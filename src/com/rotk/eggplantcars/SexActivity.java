package com.rotk.eggplantcars;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SexActivity extends Activity {
private TextView txt=null;
private RadioGroup sex=null;
private RadioButton male=null;
private RadioButton female=null;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_register);


this.txt=(TextView) findViewById(R.id.txt);
this.sex=(RadioGroup) findViewById(R.id.sex);
this.male=(RadioButton) findViewById(R.id.male);
this.female=(RadioButton) findViewById(R.id.female);


this.sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		String temp=null;
		if(SexActivity.this.male.getId()==checkedId){
		temp="男";
		}
		else if(SexActivity.this.female.getId()==checkedId){
		temp="女";
		}
		SexActivity.this.txt.setText("您的性别是"+temp);	
	}
});
	   
	 
}

}

