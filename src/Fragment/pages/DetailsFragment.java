package Fragment.pages;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import entity.Deal;

public class DetailsFragment extends Fragment{
	TextView seller;
	TextView carmodel;
	TextView traveldistance;
	TextView buydate;
	TextView text;
	Deal deal;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null){
			view=inflater.inflate(R.layout.fragment_deal_details, null);
			deal=(Deal) getActivity().getIntent().getSerializableExtra("data");
			carmodel=(TextView) view.findViewById(R.id.carmodel);
			seller=(TextView) view.findViewById(R.id.seller);
			traveldistance=(TextView) view.findViewById(R.id.traveldistance);
			buydate=(TextView) view.findViewById(R.id.buydate);
			text=(TextView) view.findViewById(R.id.text);
			
			seller.setText("卖家："+deal.getSellerName());
			carmodel.setText("汽车类型："+deal.getCarModel());
			traveldistance.setText("已行驶里程："+deal.getTravelDistance());
			buydate.setText("已被使用："+deal.getBuyDate()+"年");
			text.setText("卖家介绍："+deal.getText());
		}
		return view;
	}
}
