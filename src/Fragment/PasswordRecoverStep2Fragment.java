package Fragment;


import inputcells.SimpleTextInputCellFragment;

import com.rotk.eggplantcars.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PasswordRecoverStep2Fragment extends Fragment {
	SimpleTextInputCellFragment fragPassword,fragPasswordRepeat,fragVerify;
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(view==null){
			view = inflater.inflate(R.layout.fragment_password_recover_step2, null);
			fragPassword = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
			fragPasswordRepeat = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);
			fragVerify = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_verify);
			
			view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onSubmitClicked();
				}
			});
		}
		
		return view;
	}
	
	public String getText(){
		return fragPassword.getText();
	}
	
	public static interface OnSubmitClickedListener{
		void onSubmitClicked();
	}
	
	OnSubmitClickedListener onSubmitClickedListener;
	
	public void setOnSubmitClickedListener(OnSubmitClickedListener onSubmitClickedListener) {
		this.onSubmitClickedListener = onSubmitClickedListener;
	}
	
	void onSubmitClicked(){
		if(fragPassword.getText().equals(fragPasswordRepeat.getText())){
			if(onSubmitClickedListener!=null){
				onSubmitClickedListener.onSubmitClicked();
			}
		}else{
			new AlertDialog.Builder(getActivity())
			.setMessage("���벻һ��")
			.show();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		fragVerify.setLabelText("��֤��");{
			fragVerify.setHintText("��������֤��");
		}
		fragPassword.setLabelText("������");{
			fragPassword.setHintText("������������");
		}
		fragPasswordRepeat.setLabelText("�ظ�������");{
			fragPasswordRepeat.setHintText("���ظ�����������");
		}

	}

}