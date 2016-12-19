package inputcells;

import java.io.ByteArrayOutputStream;

import com.rotk.eggplantcars.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PictureInputCellFragment extends BaseInputCellFragment {

	final int REQUESTCODE_CAMERA = 1;
	final int REQUESTCODE_ALBUM = 2;
	byte[] pngData;

	ImageView imageView;
	TextView labelText1;
	TextView hintText1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_inputcell_picture, container);
		imageView = (ImageView) view.findViewById(R.id.image_pic);
		labelText1 = (TextView) view.findViewById(R.id.label);
		hintText1 = (TextView) view.findViewById(R.id.hint);

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onImageViewClicked();
			}
		});
		return view;
	}

	@Override
	public void setLabelText(String labelText) {
		// TODO Auto-generated method stub
		labelText1.setText(labelText);
	}

	@Override
	public void setHintText(String hintText) {
		// TODO Auto-generated method stub
		hintText1.setHint(hintText);
	}

	public void onImageViewClicked() {
		String[] items = { "拍照", "相册" };
		new AlertDialog.Builder(getActivity()).setTitle(labelText1.getText())
				.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							takePhoto();
							break;
						case 1:
							pickFromAlbum();
							break;

						default:
							break;
						}

					}
				})
				.setNegativeButton("取消", null)
				.show();
	}

	public void takePhoto() {
		// 两种方式 下面使用简单的一种
		Intent itnt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(itnt, REQUESTCODE_CAMERA);// 定义1为请求码 requestCode

	}

	public void pickFromAlbum() {
		// Intent itnt=new Intent(Intent.ACTION_PICK);
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);

	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED)
			return;
		if (requestCode == REQUESTCODE_CAMERA) {
			// Log.d("camera capture", data.getDataString());
			// Log.d("camera capture", data.getExtras().keySet().toString());
			// Toast.makeText(getActivity(), data.getDataString(),
			// Toast.LENGTH_LONG).show();
			Bitmap bmp = (Bitmap) data.getExtras().get("data");
			imageView.setImageBitmap(bmp);
			saveBitmap(bmp);
		} else if (requestCode == REQUESTCODE_ALBUM) {
			try {
				Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
				imageView.setImageBitmap(bmp);
				saveBitmap(bmp);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

	}
	
	public byte[] getPngData(){
		return pngData;
	}
	void saveBitmap(Bitmap bmp){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, baos);
		pngData=baos.toByteArray();
	}


}
