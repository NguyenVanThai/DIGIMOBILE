package digi.mobile.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.pdf.codec.GifImage;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.DocumentTypeActivity;
import digi.mobile.activity.OptionActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.AdapterGridView;
import digi.mobile.adapter.PdfListAdapter;
import digi.mobile.building.IEventListener;
import digi.mobile.building.ImageItem;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class CreateDocumentFragment extends Fragment implements OnClickListener {

	RelativeLayout relPDF, relCheck, relType, relGallery, relCamera, relAfter,
			relBefore;
	GridView gridView;
	String pathCustomer, pathSave;
	String nameShortDocument = null, nameFullDocument = null;
	Operation operation = new Operation();
	ArrayList<ImageItem> arrayImageItem;
	public Dialog dialog;
	private IEventListener listener;
	ListView listPdf;
	ImageButton btnExit;
	TextView txtType;
	ListView listView;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			String path = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER + File.separator
					+ Constant.NAME_CUSTOMER;
			String pathUser = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER;
			pathCustomer = Constant.getPathRoot(path);
			pathSave = Constant.getPathRoot(pathUser);
			showPdf();

		} else {
			Log.e("ErrorFragment", "CreateDocumentFragment gone");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View myFragmentView = inflater.inflate(
				R.layout.fragment_create_document, container, false);
		listView = (ListView) myFragmentView.findViewById(R.id.lvPdf);
		txtType = (TextView) myFragmentView.findViewById(R.id.textType);
		gridView = (GridView) myFragmentView.findViewById(R.id.gridview);

		relType = (RelativeLayout) myFragmentView.findViewById(R.id.relType);
		relGallery = (RelativeLayout) myFragmentView
				.findViewById(R.id.relGallery);
		relCamera = (RelativeLayout) myFragmentView
				.findViewById(R.id.relCamera);
		relCheck = (RelativeLayout) myFragmentView.findViewById(R.id.relCheck);
		relPDF = (RelativeLayout) myFragmentView.findViewById(R.id.relPDF);
		relAfter = (RelativeLayout) myFragmentView.findViewById(R.id.relAfter);
		relBefore = (RelativeLayout) myFragmentView
				.findViewById(R.id.relBefore);

		relType.setOnClickListener(this);
		relGallery.setOnClickListener(this);
		relCamera.setOnClickListener(this);
		relCheck.setOnClickListener(this);
		relPDF.setOnClickListener(this);
		relAfter.setOnClickListener(this);
		relBefore.setOnClickListener(this);

		return myFragmentView;
	}

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
			showImage(false, nameShortDocument);
			break;
		default:
			// showImage();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int id = v.getId();
		switch (id) {
		case R.id.relType:
			chooseType();
			break;
		case R.id.relGallery:
			showGallery();
			break;
		case R.id.relCamera:
			takePhoto();
			break;
		case R.id.relCheck:
			check(nameShortDocument);
			break;
		case R.id.relPDF:
			createPdf();
			break;
		case R.id.relAfter:

			break;
		case R.id.relBefore:

			break;
		case R.id.imageButtonExit:
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

	private void createPdf() {

		if (gridView.getCount() > 0) {

			final List<String> listImage = new ArrayList<String>();
			final List<String> listImageChoose = new ArrayList<String>();
			for (int i = 0; i < arrayImageItem.size(); i++) {

				listImage.add(arrayImageItem.get(i).getImage());

				if (arrayImageItem.get(i).isSelected() == true) {
					listImageChoose.add(arrayImageItem.get(i).getImage());
				}
			}

			if (Constant.SELECT_ALL) {

				createPDF(pathCustomer, nameShortDocument, listImage, listImage);
			} else {
				if (listImageChoose.size() > 0) {
					createPDF(pathCustomer, nameShortDocument, listImageChoose,
							listImage);
				} else {
					Toast.makeText(getActivity(),
							getString(R.string.createPDF), Toast.LENGTH_LONG)
							.show();
				}
			}
		} else {
			Toast.makeText(getActivity(), getString(R.string.createPDF),
					Toast.LENGTH_LONG).show();

		}
	}

	private void check(String nameShortDocument) {

		if (Constant.SELECT_ALL) {
			showImage(false, nameShortDocument);
			Constant.SELECT_ALL = false;
		} else {
			showImage(true, nameShortDocument);
			Constant.SELECT_ALL = true;
		}
	}

	private void chooseType() {
		dialog = new Dialog(getActivity(), R.style.MyTheme_Dialog_Action);
		dialog.setContentView(R.layout.activity_document_type);
		listPdf = (ListView) dialog.findViewById(R.id.listViewDocument);
		btnExit = (ImageButton) dialog.findViewById(R.id.imageButtonExit);
		btnExit.setOnClickListener(this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, Constant.ARRAY_APP_ITEMS);
		listPdf.setAdapter(adapter);
		listPdf.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				nameShortDocument = Constant.ARRAY_APP_SHORT_ITMES[position];
				nameFullDocument = Constant.ARRAY_APP_ITEMS[position];
				if (operation.checkCategory(pathCustomer, nameShortDocument)) {
					AlertDialog.Builder dialogReplace = new AlertDialog.Builder(
							getActivity());
					dialogReplace.setTitle("Replace Document");
					dialogReplace.setMessage("Do you want replace '"
							+ Constant.ARRAY_APP_ITEMS[position]
							+ "' document?");
					dialogReplace.setIcon(R.drawable.ic_warning);

					dialogReplace.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									operation.deleteFile(pathCustomer
											+ File.separator
											+ nameShortDocument + ".pdf");
									setType(nameFullDocument);
								}

							});

					dialogReplace.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							});
					dialogReplace.show();
				} else {
					setType(nameFullDocument);
				}
			}
		});
		dialog.show();

	}

	private void setType(String type) {
		txtType.setText("Type document: " + type + " (" + nameShortDocument
				+ ")");
		dialog.dismiss();
		showImage(false, nameShortDocument);
	}

	private void showGallery() {
		Intent intent = new Intent(getActivity(), FileSelectionActivity.class);
		Constant.fileFinal = getOutputMediaFile(nameShortDocument);
		startActivityForResult(intent, Constant.REQUEST_CODE_GALLERY);
	}

	private void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			Uri mImageCaptureUri = null;
			if (checkStorage()) {

				mImageCaptureUri = Uri
						.fromFile(getOutputMediaFile(nameShortDocument));
			} else {
				Toast.makeText(getActivity(), "Can't create file!",
						Toast.LENGTH_SHORT).show();
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_PICTURE);

		} catch (ActivityNotFoundException e) {
			Log.d(Constant.TAG, "Can't take Picture");
		}
	}

	private File getOutputMediaFile(String nameShortDocument) {

		if (nameShortDocument != null) {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.getDefault()).format(new Date());
			File mFile = new File(pathCustomer + File.separator
					+ nameShortDocument + "_" + timeStamp + ".jpg");

			Constant.fileFinal = mFile;
			return Constant.fileFinal;
		} else {
			Log.e("ErrorNameShortDocument", "Do not input nameShortDocument");
			return null;
		}
	}

	private boolean checkStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	private void showImage(boolean selected, String nameShortDocument) {

		arrayImageItem = operation.getData(pathCustomer, nameShortDocument,
				selected);
		AdapterGridView adapterGridView = null;
		if (arrayImageItem != null && arrayImageItem.size() > 0) {

			adapterGridView = new AdapterGridView(getActivity(), arrayImageItem);
			gridView.setAdapter(adapterGridView);
		} else {
			gridView.setAdapter(null);
		}

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

	private void createPDF(final String pathCustomer,
			final String nameShortDucoment, final List<String> listImage,
			final List<String> listImageDeselect) {

		// final List<String> listImage = new ArrayList<String>();
		//
		// for (int i = 0; i < arrayImageItem.size(); i++) {
		// if (arrayImageItem.get(i).isSelected() == true) {
		// listImage.add(arrayImageItem.get(i).getImage());
		// }
		// else {
		// File file = new File(arrayImageItem.get(i).getImage());
		// file.delete();
		// }
		// }

		// if (listImage.size() <= 0) {
		// Toast.makeText(getActivity(), getString(R.string.createPDF),
		// Toast.LENGTH_LONG).show();
		// return;
		// }

		final Dialog dialog = new Dialog(getActivity(),
				R.style.Theme_D1NoTitleDim);
		dialog.setContentView(R.layout.dialog_loading_animation);
		dialog.setCanceledOnTouchOutside(false);

		// init TextViewLoading and ImageLoading
		TextView txtLoading = (TextView) dialog
				.findViewById(R.id.textViewLoading);
		txtLoading.setText("Please wait...");
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

				deleteListFile(listImage);
				animation.stop();
				dialog.cancel();
				gridView.setAdapter(null);
				showPdf();
				Toast.makeText(getActivity(),
						nameShortDucoment + ".pdf" + " has created",
						Toast.LENGTH_SHORT).show();

			}

		}.execute();

		// String path = pathCustomer + File.separator + nameCustomer + ".pdf";
		// MyAsynTask myAsynTask = new MyAsynTask(path);
		// myAsynTask.execute();
	}

	private boolean deleteListFile(List<String> listImage) {
		for (String a : listImage) {
			File file = new File(a);
			file.delete();
		}
		return true;
	}

	private void showPdf() {
		List<String> listPDF = operation.listImagebyCategory(pathCustomer,
				".pdf", "name");
		if (listPDF.size() > 0) {

			PdfListAdapter pdfListAdapter = new PdfListAdapter(getActivity(),
					convertListToArray(listPDF));
			pdfListAdapter.notifyDataSetChanged();
			listView.setAdapter(pdfListAdapter);

		}
	}

	private String[] convertListToArray(List<String> listFolder) {
		ArrayList<String> list = (ArrayList<String>) listFolder;
		return list.toArray(new String[list.size()]);

	}
}
