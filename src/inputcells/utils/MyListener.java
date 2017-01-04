package inputcells.utils;

import android.os.Handler;
import android.os.Message;
import inputcells.utils.PullToRefreshLayout.OnRefreshListener;

public class MyListener implements OnRefreshListener {

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		// 下拉刷新操作
				new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						// 千万别忘了告诉控件刷新完毕了哦！
						//pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
					}
				}.sendEmptyMessageDelayed(0, 5000);
		
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		// 加载操作
				new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						// 千万别忘了告诉控件加载完毕了哦！
						//pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
					}
				}.sendEmptyMessageDelayed(0, 5000);
	}

}
