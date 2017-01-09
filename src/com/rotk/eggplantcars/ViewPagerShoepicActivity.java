package com.rotk.eggplantcars;

import java.util.ArrayList;
import java.util.List;

import com.rotk.eggplantcars.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import api.Server;
import inputcells.AvatarNewsView;

public class ViewPagerShoepicActivity extends Activity{

	private ViewPager viewPager;
	private List<AvatarNewsView> images;
	private int currentItem;
	private int oPosition=0;
	private int itemPosition;
	
	private TextView pictitle;
	private TextView goback;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_showpic_viewpager);
	
		String Img = getIntent().getStringExtra("newsImgurl");
		final String[] newsImg = Img.split("\\|");
		int picposition = getIntent().getIntExtra("picposition", 1);
		itemPosition =picposition;
	
		pictitle = (TextView) findViewById(R.id.pic_num1); // 3/3ох╥еве
		pictitle.setText(itemPosition+"/"+newsImg.length);
		viewPager = (ViewPager) findViewById(R.id.showpicviewpager);
		
		images = new ArrayList<AvatarNewsView>();
		for (int i = 0; i < newsImg.length; i++) {
			AvatarNewsView aView = new AvatarNewsView(this);
			aView.load(Server.serverAddress+newsImg[i]);
			images.add(aView);			
		}
		
		goback = (TextView) findViewById(R.id.pic_cancle);
		goback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		viewPager.setAdapter(PicViewPagerAdapter);
		viewPager.setCurrentItem(picposition-1);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				pictitle.setText((position+1)+"/"+newsImg.length);
				oPosition = position;
				currentItem = position;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	PagerAdapter PicViewPagerAdapter = new PagerAdapter() {
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.size();
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(images.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(images.get(position));
			return images.get(position);
		}
	};
	
	
}
