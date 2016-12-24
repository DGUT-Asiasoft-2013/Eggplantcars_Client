package com.rotk.eggplantcars;



import Fragment.MainTabbarFragment;
import Fragment.MainTabbarFragment.OnSelectedListener;
import Fragment.pages.ItemFragment;
import Fragment.pages.MainFragment;
import Fragment.pages.MyFragment;
import Fragment.pages.ConcernFragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

public class HelloWorldActivity extends Activity{

	private static boolean isExit = false;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	MainFragment mainpage=new MainFragment();
	ItemFragment itempage=new ItemFragment();
	ConcernFragment searchspage=new ConcernFragment();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 3000);
		} else {
			finish();
			System.exit(0);
		}
	}
}

