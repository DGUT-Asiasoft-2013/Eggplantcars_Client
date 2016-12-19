package Fragment;



import com.rotk.eggplantcars.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MainTabbarFragment extends Fragment{
	View btnNew, tabMain, tabItem, tabSearch, tabMe;
	View[] tabs;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tabbar, null);

		btnNew = view.findViewById(R.id.btn_new);
		tabMain = view.findViewById(R.id.tab_main);
		tabItem = view.findViewById(R.id.tab_item);
		tabSearch = view.findViewById(R.id.tab_search);
		tabMe = view.findViewById(R.id.tab_me);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent=new Intent(getActivity(),NewActivity.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.slide_in_bottom,0);
			}
		});
		//将tab放到数组中
		tabs = new View[] {
				tabMain, tabItem, tabSearch, tabMe
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
	//循环遍历数组，tab被点击时显示为selected
	void onTabClicked(View tab){
		int selectedIndex=-1;
		for(int i=0;i<tabs.length;i++){
			View otherTab=tabs[i];
			if (otherTab==tab){
				otherTab.setSelected(true);
				selectedIndex=i;
			}else{
				otherTab.setSelected(false);
			}
		}
		if(OnSelectedListener!=null && selectedIndex>=0){
			OnSelectedListener.OnGoTabClicked(selectedIndex);
		}
	}
	public void setSelectedItem(int index){
		if(index>=0 && index<tabs.length){
			onTabClicked(tabs[index]);
		}
	}
	//定義接口
	public static interface OnSelectedListener{
		void OnGoTabClicked(int index);
	}
	OnSelectedListener OnSelectedListener;

	public void OnSelectedListener(OnSelectedListener OnSelectedListener){
		this.OnSelectedListener = OnSelectedListener;
	}
	public int getSelectedIndex() {
		// TODO Auto-generated method stub
		for(int i=0;i<tabs.length;i++){
			if(tabs[i].isSelected())
				return i;
		}
		return -1;
	}
}
