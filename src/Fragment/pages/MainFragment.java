package Fragment.pages;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import inputcells.ImageAdapter;

public class MainFragment extends Fragment {

	View view;
	private int[] res = { R.drawable.item1, R.drawable.item2, R.drawable.item3, R.drawable.item4, R.drawable.item5,
			R.drawable.item6 };
	private ImageAdapter adapter;
	private Gallery gallery;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_main, null);
			gallery = (Gallery) view.findViewById(R.id.img_gallery);
			adapter = new ImageAdapter(res, getActivity());
			gallery.setAdapter(adapter);
		}
		return view;
	}
}
