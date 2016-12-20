package Fragment.pages;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.Toast;
import inputcells.ImageAdapter;

public class MainFragment extends Fragment  {

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
			
			
			///----------
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					 String sInfo=("������"+position%adapter.getResLength());
			          Toast.makeText(getActivity(), sInfo, Toast.LENGTH_SHORT).show(); 
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
			///---------
			
			
		}
		return view;
	}
}
