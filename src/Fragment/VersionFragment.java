package Fragment;



import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VersionFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view= inflater.inflate(R.layout.fragment_version, null);
		TextView textVersion=(TextView) view.findViewById(R.id.text);
		
		PackageManager pkgm =getActivity().getPackageManager();
		
		try {
			PackageInfo appinf =pkgm.getPackageInfo(getActivity().getPackageName(), 0);
			textVersion.setText(appinf.packageName +" "+appinf.versionName);
		} catch (NameNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
			textVersion.setText("ERROR");
		}
		return view;
	}

	
	
}