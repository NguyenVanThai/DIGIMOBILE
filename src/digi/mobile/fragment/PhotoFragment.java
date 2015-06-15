package digi.mobile.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.OptionActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.AdapterGridView;
import digi.mobile.building.IEventListener;
import digi.mobile.building.ImageItem;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class PhotoFragment extends Fragment implements OnClickListener {

	RelativeLayout relPDF, relCheck, relUnCheck, relGallery, relCamera;
	GridView gridView;
	String pathCustomer;
	String nameShortDucoment;
	Operation operation = new Operation();
	ArrayList<ImageItem> arrayImageItem;
	public static Dialog dialog;
	private IEventListener listener;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case Constant.REQUEST_CODE_TAKE_PICTURE:
			showOptionActiviy(Constant.fileFinal.getPath());
			break;
		case Constant.REQUEST_CODE_GALLERY:
			String path = data.getStringExtra("path");

			showOptionActiviy(path);
		case Constant.REQUEST_CODE_OPTION_ACTIVITY:

			// Toast.makeText(getActivity(), "CropMayacc", Toast.LENGTH_LONG)
			// .show();
			showImage(false);
			break;
		default:
			// showImage();

		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IEventListener) {
			listener = (IEventListener) activity;
		} else {
			// Throw an error!
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// showImage();
			// init pathCustomer
			String path = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER + File.separator
					+ Constant.NAME_CUSTOMER;
			pathCustomer = Constant.getPathRoot(path);

			nameShortDucoment = Constant.NAME_SHORT_DUCOMENT;

			showImage(false);
		} else {
			Log.d("frag3", "HIde");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.photo_fragment,
				container, false);
		gridView = (GridView) myFragmentView.findViewById(R.id.gridview);

		relPDF = (RelativeLayout) myFragmentView.findViewById(R.id.relPDF);
		relCheck = (RelativeLayout) myFragmentView
				.findViewById(R.id.relCheckAll);
		relUnCheck = (RelativeLayout) myFragmentView
				.findViewById(R.id.relDeselect);
		relGallery = (RelativeLayout) myFragmentView
				.findViewById(R.id.relGallery);
		relCamera = (RelativeLayout) myFragmentView
				.findViewById(R.id.relCamera);

		relPDF.setOnClickListener(this);
		relCheck.setOnClickListener(this);
		relUnCheck.setOnClickListener(this);
		relGallery.setOnClickListener(this);
		relCamera.setOnClickListener(this);

		return myFragmentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.relPDF:
			// arrayImageItem = new ArrayList<ImageItem>();
			try {
				if (arrayImageItem.size() > 0) {
					createPDF(pathCustomer, nameShortDucoment);
				}
			} catch (Exception e) {
				Toast.makeText(getActivity(), getString(R.string.createPDF),
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.relCheckAll:
			checkAll();
			break;
		case R.id.relDeselect:
			deselectAll();
			break;
		case R.id.relGallery:
			showGallery();
			break;
		case R.id.relCamera:
			Constant.TAKE_PHOTO = true;
			takePhoto();
			break;
		}
	}

	private void showGallery() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity().getApplicationContext(),
				FileSelectionActivity.class);
		Constant.fileFinal = getOutputMediaFile();
		startActivityForResult(intent, Constant.REQUEST_CODE_GALLERY);
	}

	private void takePhoto() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			Uri mImageCaptureUri = null;
			if (checkStorage()) {

				mImageCaptureUri = Uri.fromFile(getOutputMediaFile());
			} else {
				// mImageCaptureUri = CONTENT_URI;
				Toast.makeText(getActivity(), "Can't create file!",
						Toast.LENGTH_SHORT).show();
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_PICTURE);

		} catch (ActivityNotFoundException e) {
			Log.d(Constant.TAG, "Can't take Picture");
		}
	}

	private void checkAll() {
		// TODO Auto-generated method stub

		showImage(true);
	}

	private void deselectAll() {
		// TODO Auto-generated method stub
		showImage(false);
	}

	private File getOutputMediaFile() {
		// TODO Auto-generated method stub
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mFile = new File(pathCustomer + File.separator + nameShortDucoment
				+ "_" + timeStamp + ".jpg");

		Constant.fileFinal = mFile;
		return Constant.fileFinal;
	}

	private boolean checkStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	private void showImage(boolean selected) {

		arrayImageItem = operation.getData(pathCustomer, nameShortDucoment,
				selected);
		AdapterGridView adapterGridView = null;
		if (arrayImageItem != null && arrayImageItem.size() > 0) {

			adapterGridView = new AdapterGridView(getActivity(), arrayImageItem);
			gridView.setAdapter(adapterGridView);
		} else {
			gridView.setAdapter(null);
		}

		// boolean pauseOnScroll = false; // or true
		// boolean pauseOnFling = true; // or false
		// PauseOnScrollListener listener = new
		// PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll,
		// pauseOnFling);
		// gridView.setOnScrollListener(listener);
	}

	private void createPDF(final String pathCustomer,
			final String nameShortDucoment) {

		final List<String> listImage = new ArrayList<String>();

		for (int i = 0; i < arrayImageItem.size(); i++) {
			if (arrayImageItem.get(i).isSelected() == true) {
				listImage.add(arrayImageItem.get(i).getImage());
			} else {
				File file = new File(arrayImageItem.get(i).getImage());
				file.delete();
			}
		}
		//
		if (listImage.size() <= 0) {
			Toast.makeText(getActivity(), getString(R.string.createPDF),
					Toast.LENGTH_LONG).show();
			return;
		}

		dialog = new Dialog(getActivity(), R.style.Theme_D1NoTitleDim);
		dialog.setContentView(R.layout.dialog_loading_animation);
		dialog.setCanceledOnTouchOutside(false);

		// init TextViewLoading and ImageLoading
		TextView txtLoading = (TextView) dialog
				.findViewById(R.id.textViewLoading);
		txtLoading.setText("Loading...");
		ImageView imageLoading = (ImageView) dialog
				.findViewById(R.id.imageViewLoading);
		imageLoading.setBackgroundResource(R.drawable.animation_loading);
		//
		// // using Animation for ImageLoading
		final AnimationDrawable animation = (AnimationDrawable) imageLoading
				.getBackground();
		//
		// dialog.show();
		// animation.start();
		//
		new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if (dialog.isShowing()) {
					dialog.cancel();
				} else {
					dialog.show();
					animation.start();
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				// List<String> listImage = operation.listImagebyCategory(
				// pathCustomer, nameShortDucoment, "path");

				return operation.createPDF(pathCustomer, listImage,
						nameShortDucoment);

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result == true) {
					animation.stop();
					dialog.cancel();
					// Intent intentNewApp = new
					// Intent(NewAppDetailActivity.this,
					// NewAppActivity.class);
					// startActivity(intentNewApp);
					// finish();
					sendDataToActivity(Constant.Step_2);
					Toast.makeText(getActivity(),
							nameShortDucoment + ".pdf" + " has created",
							Toast.LENGTH_SHORT).show();
				}

			}

		}.execute();

		// String path = pathCustomer + File.separator + nameCustomer + ".pdf";
		// MyAsynTask myAsynTask = new MyAsynTask(path);
		// myAsynTask.execute();
	}

	private void showOptionActiviy(String path) {
		// TODO Auto-generated method stub
		// Display display = getWindowManager().getDefaultDisplay();
		//
		// int width = display.getWidth() / 2;
		// int height = display.getHeight() / 2;
		Bitmap bitmap = Constant.getBitmap(path);
		Constant.updateBitmap(bitmap);
		Intent intent = new Intent(getActivity().getApplicationContext(),
				OptionActivity.class);
		// intent.putExtra(Constant.PATH_IMAGE, path);
		startActivityForResult(intent, Constant.REQUEST_CODE_OPTION_ACTIVITY);
		// finish();
	}

	public void sendDataToActivity(int step) {
		// do whatever you want
		listener.sendDataToActivity(step);
	}
}
