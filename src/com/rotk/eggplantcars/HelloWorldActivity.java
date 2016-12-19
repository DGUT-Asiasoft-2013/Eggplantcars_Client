package com.rotk.eggplantcars;



import Fragment.MainTabbarFragment;
import Fragment.MainTabbarFragment.OnSelectedListener;
import Fragment.pages.ItemFragment;
import Fragment.pages.MainFragment;
import Fragment.pages.MyFragment;
import Fragment.pages.SearchFragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class HelloWorldActivity extends Activity{
	MainFragment mainpage=new MainFragment();
	ItemFragment itempage=new ItemFragment();
	SearchFragment searchspage=new SearchFragment();
	MyFragment mypage=new MyFragment();
	MainTabbarFragment tab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helloworld);
		tab=(MainTabbarFragment) getFragmentManager().findFragmentById(R.id.tab);
		tab.OnSelectedListener(new OnSelectedListener(){

			public void OnGoTabClicked(int index) {
				// TODO Auto-generated method stub
				onChange(index);
			}
		});


	}
	public void onResume(){
		super.onResume();
		if (tab.getSelectedIndex()<0){
			tab.setSelectedItem(0);
		}
	}
	void onChange(int index) {
		// TODO Auto-generated method stub
		Fragment newfrag=null;
		switch (index) {
		case 0:newfrag=mainpage; break;
		case 1:newfrag=itempage; break;
		case 2:newfrag=searchspage; break;
		case 3:newfrag=mypage; break;
		default:break;
		}
		if(newfrag==null)return;
		getFragmentManager()
		.beginTransaction()
		.replace(R.id.content, newfrag)
		.commit();
	}
}

