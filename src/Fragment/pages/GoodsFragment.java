package Fragment.pages;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import api.Server;
import entity.Deal;
import inputcells.AvatarNewsView;

public class GoodsFragment extends Fragment{
	Deal deal;
	View view;
	AvatarNewsView avatarNewsView1;
	TextView title;
	TextView price;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null){
			view=inflater.inflate(R.layout.fragment_deal_goods, null);
			deal=(Deal) getActivity().getIntent().getSerializableExtra("data");
			avatarNewsView1=(AvatarNewsView) view.findViewById(R.id.avatarNewsView1);
			title=(TextView) view.findViewById(R.id.title);
			price=(TextView) view.findViewById(R.id.price);
			
			avatarNewsView1.load(Server.serverAddress +deal.getDealAvatar());
			title.setText(deal.getTitle());
			price.setText("гд"+deal.getPrice());
		}
		return view;
	}
}
