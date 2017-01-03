package Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rotk.eggplantcars.NewActivity;
import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerFragment extends Fragment {

	private ViewPager viewPager;
	private List<ImageView> images;
	private List<View> dots;
	private int currentItem; 
	private int oPosition = 0; //上次索引
	//图片ID地址
	private int[] imagesId = new int[]{
			R.drawable.item1,
			R.drawable.item2,
			R.drawable.item3,
			R.drawable.item4,
			R.drawable.item5,
	};
	//图片标题文字
	private String[] titles = new String[]{
		"1","2","3","4","5"
	};
	
	private TextView title;
	private ScheduledExecutorService scheduledExecutorService;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_viewpager,null);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		
		//加载图片
		images = new ArrayList<ImageView>();
		for (int i = 0; i < imagesId.length; i++) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setBackgroundResource(imagesId[i]);
			images.add(imageView);
		}
		
		//小点
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.dot_0));
		dots.add(view.findViewById(R.id.dot_1));
		dots.add(view.findViewById(R.id.dot_2));
		dots.add(view.findViewById(R.id.dot_3));
		dots.add(view.findViewById(R.id.dot_4));
		
		title = (TextView) view.findViewById(R.id.text_laber);
		title.setText(titles[0]);
		
		//先放着
		viewPager.setAdapter(ViewPagerAdapter);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				title.setText(titles[position]);
				dots.get(position).setBackgroundResource(R.drawable.dot_foucus);
				dots.get(oPosition).setBackgroundResource(R.drawable.dot_blur);
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
		
		return view;
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleWithFixedDelay(new ViewPageTask(), 3, 3, TimeUnit.SECONDS);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
			scheduledExecutorService=null;
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};
	
	//图片轮播 与   索引循环
	private class ViewPageTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			currentItem = (currentItem + 1 ) % imagesId.length;
			handler.sendEmptyMessage(0);
		}
		
	}
	
	
	PagerAdapter ViewPagerAdapter = new PagerAdapter() {
		
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
