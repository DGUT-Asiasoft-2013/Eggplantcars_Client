package Fragment;

import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class DealBarFragment extends Fragment{
	TextView goods;
	TextView details;
	View[] tabs;
	ImageButton back;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_deal_bar, null);

		goods = (TextView) view.findViewById(R.id.btn_goods);
		details = (TextView) view.findViewById(R.id.btn_details);
		back=(ImageButton) view.findViewById(R.id.btn_back);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		//将tab放到数组中
		tabs = new View[] {
				goods, details
		};
		//循环遍历数组，为每个tab设置监听器
		for(final View tab : tabs){
			tab.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					onTabClicked(tab);
				}
			});			
		}

		return view;
	}
	void onTabClicked(View tab) {
		// TODO Auto-generated method stub
		int DealselectedIndex=-1;
		for(int i=0;i<tabs.length;i++){
			View otherTab=tabs[i];
			if (otherTab==tab){
				otherTab.setSelected(true);
				DealselectedIndex=i;
			}else{
				otherTab.setSelected(false);
			}
		}
		if(OnDealSelectedListener!=null && DealselectedIndex>=0){
			OnDealSelectedListener.OnGoTabClicked(DealselectedIndex);
		}
	}
	public void setDealSelectedItem(int index){
		if(index>=0 && index<tabs.length){
			onTabClicked(tabs[index]);
		}
	}
	//定義接口
	public static interface OnDealSelectedListener{
		void OnGoTabClicked(int index);
	}
	OnDealSelectedListener OnDealSelectedListener;

	public void OnDealSelectedListener(OnDealSelectedListener OnDealSelectedListener){
		this.OnDealSelectedListener = OnDealSelectedListener;
	}
	public int getDealSelectedIndex() {
		// TODO Auto-generated method stub
		for(int i=0;i<tabs.length;i++){
			if(tabs[i].isSelected())
				return i;
		}
		return -1;
	}
}
