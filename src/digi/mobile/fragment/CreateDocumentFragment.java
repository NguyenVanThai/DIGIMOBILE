package digi.mobile.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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

	RelativeLayout relParentType, relParentPdf;
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
	AdapterGridView adapterGridView = null;
	ImageView imageView;
	EditText edReason;
	LinearLayout llReason;

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
			lockClick(View.GONE);
			txtType.setText(getString(R.string.choose_type));

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
		imageView = (ImageView) myFragmentView.findViewById(R.id.imageView2);
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
		relParentType = (RelativeLayout) myFragmentView
				.findViewById(R.id.reltype);
		relParentPdf = (RelativeLayout) myFragmentView
				.findViewById(R.id.relpdf);

		edReason = (EditText) myFragmentView.findViewById(R.id.edReason);

		llReason = (LinearLayout) myFragmentView.findViewById(R.id.relReason);

		checkType(Constant.TYPE);

		relType.setOnClickListener(this);
		relGallery.setOnClickListener(this);
		relCamera.setOnClickListener(this);
		relCheck.setOnClickListener(this);
		relPDF.setOnClickListener(this);
		relAfter.setOnClickListener(this);
		relBefore.setOnClickListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView nameFile = (TextView) view.findViewById(R.id.item);
				String pathFile = pathCustomer + File.separator
						+ nameFile.getText().toString();
				File file = new File(pathFile);

				if (file.exists()) {
					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(getActivity(),
								"No Application Available to View PDF",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		Log.e("Creatdocument", "onCreate");
		return myFragmentView;
	}

	private void checkType(int type) {
		switch (type) {
		case 1:
			llReason.setVisibility(View.GONE);
			break;
		case 2:
			llReason.setVisibility(View.VISIBLE);
			break;
		case 3:
			relParentPdf.setVisibility(View.GONE);
			relParentType.setVisibility(View.GONE);
			llReason.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case Constant.REQUEST_CODE_TAKE_PICTURE:
			// if(data != null){
			showOptionActiviy(Constant.fileFinal.getPath());
			// }else{
			// Log.e("Data", "data is null");
			//
			// }
			break;
		case Constant.REQUEST_CODE_GALLERY:
			if (data != null) {
				String path = data.getStringExtra("path");

				showOptionActiviy(path);
			} else {
				Log.e("Data", "data is null");
			}
			break;
		case Constant.REQUEST_CODE_OPTION_ACTIVITY:
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
			// lockClick(View.VISIBLE);
			break;
		case R.id.relGallery:
			showGallery();
			break;
		case R.id.relCamera:
			takePhoto();
			Constant.TAKE_PHOTO = true;
			break;
		case R.id.relCheck:
			check(nameShortDocument);
			break;
		case R.id.relPDF:
			createPdf();
			// lockClick(View.GONE);
			break;
		case R.id.relAfter:
			listener.sendDataToActivity(Constant.Step_0);
			break;
		case R.id.relBefore:
			if (checkAll(Constant.TYPE)) {
				// refreshForm();
				listener.sendDataToActivity(Constant.Step_2);

			}
			break;
		case R.id.imageButtonExit:
			dialog.dismiss();
			break;
		default:
			break;
		}
	}

	// private boolean checkDocumentCreate(){
	// if(txtType.getText().toString() != getString(R.string.choose_type)){
	//
	// return false;
	// }else{
	// return true;
	// }
	//
	// }

	private boolean checkAll(int type) {
		switch (type) {
		case 1:
			return checkLvPDF();
		case 2:
			return (checkLvPDF() && checkReason());
		case 3:
			return checkReason();
		default:
			return false;
		}
	}

	private boolean checkReason() {
		if (edReason.getText().toString().isEmpty()) {
			Toast.makeText(getActivity(), getString(R.string.files),
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			Constant.REASON = edReason.getText().toString();
			return true;
		}
	}

	private boolean checkLvPDF() {
		List<String> listPDF = operation.listImagebyCategory(pathCustomer,
				".pdf", "name");
		// int count = listView.getCount();
		int count = listPDF.size();
		int temp = 0;
		if (count > 0) {
			if (Constant.TYPE == 1) {
				for (int i = 0; i < count; i++) {
					// TextView txt = (TextView) listView.getChildAt(i)
					// .findViewById(R.id.item);
					// String txt = listPDF.get(i);
					switch (listPDF.get(i).toString()) {
					case "DN.pdf":
						temp++;
						break;
					case "ID.pdf":
						temp++;
						break;
					case "HK.pdf":
						temp++;
						break;
					}
				}
				if (temp < 3) {
					Toast.makeText(getActivity(),
							getString(R.string.create_document_upload),
							Toast.LENGTH_LONG).show();
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			Toast.makeText(getActivity(), getString(R.string.uploadPDF),
					Toast.LENGTH_LONG).show();
			return false;
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

		Constant.SELECT_ALL = !Constant.SELECT_ALL;
		showImage(Constant.SELECT_ALL, nameShortDocument);

		// if (Constant.SELECT_ALL) {
		// showImage(false, nameShortDocument);
		// Constant.SELECT_ALL = false;
		// } else {
		// showImage(true, nameShortDocument);
		// Constant.SELECT_ALL = true;
		// }
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
									operation.deleteFile(pathCustomer
											+ File.separator
											+ nameShortDocument + ".pdf");
									setType(nameFullDocument);
									showPdf();
									// lockClick(true);
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
					// lockClick(true);
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
		lockClick(View.VISIBLE);
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
		try {
			arrayImageItem = operation.getData(pathCustomer, nameShortDocument,
					selected);

			if (arrayImageItem != null && arrayImageItem.size() > 0
					&& !arrayImageItem.isEmpty()) {

				adapterGridView = new AdapterGridView(getActivity(), arrayImageItem);
				gridView.setAdapter(adapterGridView);
			} else {
				gridView.setAdapter(null);
			}			
			
		} catch (Exception e) {
			
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
		// Constant.bitmap = bitmap;

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

				refreshForm();

				Toast.makeText(getActivity(),
						nameShortDucoment + ".pdf" + " has created",
						Toast.LENGTH_SHORT).show();

			}

		}.execute();

		// String path = pathCustomer + File.separator + nameCustomer + ".pdf";
		// MyAsynTask myAsynTask = new MyAsynTask(path);
		// myAsynTask.execute();
	}

	private void refreshForm() {
		// adapterGridView.clear();
		gridView.setAdapter(null);
		showPdf();
		lockClick(View.GONE);
		Constant.SELECT_ALL = false;
		txtType.setText(getString(R.string.choose_type));
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
		PdfListAdapter pdfListAdapter = null;
		if (listPDF.size() > 0 && !listPDF.isEmpty()) {

			pdfListAdapter = new PdfListAdapter(getActivity(),
					convertListToArray(listPDF));
			pdfListAdapter.notifyDataSetChanged();
			listView.setAdapter(pdfListAdapter);

		} else {
			// pdfListAdapter.clear();
			listView.setAdapter(null);
		}
	}

	private String[] convertListToArray(List<String> listFolder) {
		ArrayList<String> list = (ArrayList<String>) listFolder;
		return list.toArray(new String[list.size()]);

	}

	private void lockClick(int b) {
		// relCamera.setEnabled(b);
		// relGallery.setEnabled(b);
		// relCheck.setEnabled(b);
		// relPDF.setEnabled(b);
		if (b == View.GONE) {
			// relType.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
		} else {
			// relType.setVisibility(View.GONE);
			imageView.setVisibility(View.GONE);
		}
		relCamera.setVisibility(b);
		relGallery.setVisibility(b);
		relCheck.setVisibility(b);
		relPDF.setVisibility(b);
	}
}
