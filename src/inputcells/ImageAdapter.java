package inputcells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


public class ImageAdapter extends BaseAdapter{

	private int[] res;
	private Context context;
	public ImageAdapter(int []res,Context convert) {
		this.res = res;
		this.context = convert;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return res[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image  = new ImageView(context);
		image.setImageResource(res[position%res.length]);
		image.setScaleType(ScaleType.FIT_XY);
		return image;
	}

}
