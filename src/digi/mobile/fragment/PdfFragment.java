package digi.mobile.fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.DocumentTypeActivity;
import digi.mobile.activity.LoginActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.PdfListAdapter;
import digi.mobile.building.AndroidMultiPartEntity;
import digi.mobile.building.AndroidMultiPartEntity.ProgressListener;
import digi.mobile.building.ConnectionDetector;
import digi.mobile.building.DigiCompressFile;
import digi.mobile.building.IEventListener;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class PdfFragment extends Fragment {

	RelativeLayout relNext, relUpload;
	LinearLayout lReason, lDucoment;
	private IEventListener listener;
	Operation operation = new Operation();
	ListView listView;
	EditText edReason;
	String pathCustomer;
	String pathSave;
	String user;
	Dialog dialog;
	AnimationDrawable animation;
	String pathZip;

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
			String path = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER + File.separator
					+ Constant.NAME_CUSTOMER;
			String pathUser = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER;
			pathCustomer = Constant.getPathRoot(path);
			pathSave = Constant.getPathRoot(pathUser);
			showPdf();
		} else {
			// Log.d("frag2", "HIde");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.pdf_fragment,
				container, false);
		edReason = (EditText) myFragmentView.findViewById(R.id.edReason);
		listView = (ListView) myFragmentView.findViewById(R.id.lvPdf);
		lReason = (LinearLayout) myFragmentView.findViewById(R.id.lReason);
		lDucoment = (LinearLayout) myFragmentView.findViewById(R.id.lDocument);
		relNext = (RelativeLayout) myFragmentView
				.findViewById(R.id.RelativeLayout_Next);
		relUpload = (RelativeLayout) myFragmentView
				.findViewById(R.id.RelativeLayout_Upload);

		//
		checkType();

		relNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), DocumentTypeActivity.class);

				startActivityForResult(intent,
						Constant.REQUEST_CODE_DUCOMENT_TYPE_ACTIVITY);

			}
		});

		relUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// checkUpload();
				checkUpload();
			}
		});
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
		case Constant.REQUEST_CODE_DUCOMENT_TYPE_ACTIVITY:
			int position = data.getIntExtra(Constant.POSITION, 100);
			Constant.NAME_SHORT_DUCOMENT = Constant.ARRAY_APP_SHORT_ITMES[position];
			// listener.sendDataToActivity(Constant.Step_3);
			// Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT)
			// .show();
			sendDataToActivity(Constant.Step_3);
			break;
		}

	}

	public void sendDataToActivity(int step) {
		// do whatever you want
		listener.sendDataToActivity(step);
	}

	private void showPdf() {
		// TODO Auto-generated method stub
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

	private void checkType() {
		switch (Constant.TYPE) {
		case 1:// new
			lReason.setVisibility(View.GONE);
			break;
		case 2:// hard
			lReason.setVisibility(View.VISIBLE);
			lDucoment.setVisibility(View.VISIBLE);
			break;
		case 3:// soft
			lReason.setVisibility(View.VISIBLE);
			lDucoment.setVisibility(View.GONE);
			relNext.setVisibility(View.GONE);

			break;
		}
	}

	private void checkUpload() {
		switch (Constant.TYPE) {
		case 1:// new
				// checkListView();
			if (checkLvPDF()) {
				uploadZipFile();
				// compressFilesToZip(pathCustomer, pathSave, "thai" + ".zip");
			}
			break;
		case 2:// hard
			if (checkEtReason() && checkLvPDF()) {
				uploadZipFile();
			}

			break;
		case 3:// soft
			if (checkEtReason()) {
				uploadZipFile();
			}
			break;
		}
	}

	private boolean checkLvPDF() {
		int count = listView.getCount();
		int temp = 0;
		if (count > 0) {
			if (Constant.TYPE == 1) {
				for (int i = 0; i < count; i++) {
					TextView txt = (TextView) listView.getChildAt(i)
							.findViewById(R.id.item);
					switch (txt.getText().toString()) {
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
					Toast.makeText(
							getActivity(),
							"Please create full 3 documents: DN.pdf, HK.pdf, ID.pdf",
							Toast.LENGTH_LONG).show();
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			Toast.makeText(getActivity(), "Please create Document",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private boolean checkEtReason() {
		if (edReason.getText().toString().isEmpty()) {
			Toast.makeText(getActivity(), "Please fill in a reason",
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}
	}

	// private String getNameCustomer() {
	// switch (Constant.TYPE) {
	// case 1:
	// return txtReview.getText().toString();
	//
	// case 2:
	// case 3:
	// return txtReview.getText().toString() + "_"
	// + edCode.getText().toString();
	//
	// }
	// return null;
	// }

	private void uploadZipFile() {

		// init dialog
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
		// using Animation for ImageLoading
		animation = (AnimationDrawable) imageLoading.getBackground();
		dialog.show();
		animation.start();

		// Check Internet
		ConnectionDetector cnnDec = new ConnectionDetector(getActivity());
		boolean isInternetPresent = cnnDec.isConnectingToInternet();
		if (isInternetPresent) {
			SharedPreferences sharedPerferences = getActivity()
					.getSharedPreferences(Constant.DIGI_LOGIN_PREFERENCES,
							Context.MODE_PRIVATE);
			if (sharedPerferences.contains(Constant.FLAG_KEY)) {
				user = sharedPerferences.getString(Constant.USER_NAME, null);
				if (user != null) {
					switch (Constant.TYPE) {
					case 1:
						uploadFileToServer(Constant.NAME_CUSTOMER, user,
								Constant.NAME_CUSTOMER, pathCustomer, pathSave,
								null);
						break;
					case 2:
						String reason2 = edReason.getText().toString();
						uploadFileToServer(Constant.NAME_CUSTOMER, user,
								Constant.NAME_CUSTOMER, pathCustomer, pathSave,
								reason2);
						break;
					case 3:
						String reason3 = edReason.getText().toString();
						uploadFileToServer(null, user, Constant.NAME_CUSTOMER,
								pathCustomer, pathSave, reason3);
						break;
					}
					// String reason3
					// if(edReason.getText().toString().isEmpty()){
					//
					// }
					// = edReason.getText().toString();
					// uploadFileToServer("thai", user, Constant.NAME_CUSTOMER,
					// pathCustomer, pathSave, reason3);
					// uploadFileToServer(pathCustomer, pathCustomer,
					// pathCustomer, pathCustomer, pathCustomer, pathCustomer);
				}
			} else {
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(i, Constant.REQUEST_CODE_LOGIN_ACTIVITY);
			}
		} else {
			animation.stop();
			dialog.dismiss();
			Toast.makeText(getActivity(), "No Internet Connection",
					Toast.LENGTH_LONG).show();
		}
	}

	private void upload(final String nameUpload) {
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
		// using Animation for ImageLoading
		animation = (AnimationDrawable) imageLoading.getBackground();

		// compressFilesToZip(getListFdfFile(), pathCustomer, nameUpload);

		switch (Constant.TYPE) {
		case 1:
		case 2:
			new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {
					// TODO Auto-generated method stub

					return pathZip = compressFilesToZip(pathCustomer, pathSave,
							nameUpload + ".zip");
					// String pathsave = Constant
					// .getPathRoot(Constant.APP_FOLDER);
					// return pathZip = createZipFolder(pathCustomer,
					// pathsave, Constant.NAME_MY_FOLDER);
				}

				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					dialog.show();
					animation.start();

				}

				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (result.length() > 0) {
						uploadHandle(result);

						animation.stop();
						dialog.dismiss();
					}

				}

			}.execute();
			break;
		case 3:
			uploadHandle("");
			break;
		}

	}

	private String compressFilesToZip(String pathFolder, String pathSave,
			String zipName) {
		// List<String> fileNames = operation.listImagebyCategory(pathFolder,
		// ".jpg", "path");
		// for (int i = 0; i < fileNames.size(); i++) {
		// File file = new File(fileNames.get(i));
		// file.delete();
		// }

		DigiCompressFile compressFile = new DigiCompressFile();
		try {
			// return compressFile.compressFilesToZip(arrPdfFile, "123456",
			// pathCustomer, nameCustomer + ".zip");
			// String pathSave = Constant.getPathRoot(Constant.APP_FOLDER);
			// return compressFile.compressFilesToZip(arr, pathSave, zipName);
			return compressFile.compressFolderToZip(pathFolder, pathSave,
					zipName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void uploadHandle(String pathZip) {
		// TODO Auto-generated method stub
		ConnectionDetector cnnDec = new ConnectionDetector(getActivity());
		boolean isInternetPresent = cnnDec.isConnectingToInternet();
		if (isInternetPresent) {
			SharedPreferences sharedPerferences = getActivity()
					.getSharedPreferences(Constant.DIGI_LOGIN_PREFERENCES,
							Context.MODE_PRIVATE);
			if (sharedPerferences.contains(Constant.FLAG_KEY)) {
				user = sharedPerferences.getString(Constant.USER_NAME, null);
				if (user != null) {
					// uploadFileToServer(pathZip);
				}
			} else {
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(i, Constant.REQUEST_CODE_LOGIN_ACTIVITY);
			}
		} else {
			animation.stop();
			dialog.dismiss();
			Toast.makeText(getActivity(), "No Internet Connection",
					Toast.LENGTH_LONG).show();
		}
	}

	private void uploadFileToServer(final String nameUpload, final String user,
			final String nameCustomer, final String pathCustomer,
			final String pathSave, final String reason) {
		new AsyncTask<Void, Integer, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub

				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return uploadFile(nameUpload, user, nameCustomer, pathCustomer,
						pathSave, reason);
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
				// progressBar.setVisibility(View.VISIBLE);
				// progressBar.setProgress(values[0]);
				// txtPercentage.setText(String.valueOf(values[0]) + "%");

			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				animation.stop();
				dialog.dismiss();
				Toast.makeText(getActivity(), "Upload successful!",
						Toast.LENGTH_LONG).show();
			}

			private String uploadFile(String nameUpload, final String user,
					final String nameCustomer, String pathCustomer,
					String pathSave, String reason) {
				// TODO Auto-generated method stub
				String responseString = null;
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(Config.FILE_UPLOAD_URL);

				try {
					AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
							new ProgressListener() {

								@Override
								public void transferred(long num) {
									// TODO Auto-generated method stub
									publishProgress((int) num);
								}
							});

					// filePath = map.get(finishedUpload);
					// File sourceFile = new File(pathZip);
					// entity.addPart("myFile", new FileBody(sourceFile));
					entity.addPart("ccCode", new StringBody(user));
					entity.addPart("ccChannel", new StringBody("ABC"));
					entity.addPart("ip", new StringBody("LG"));
					entity.addPart("id", new StringBody("123456789"));
					String path;
					File sourceFile;

					switch (Constant.TYPE) {

					case 1:
						path = compressFilesToZip(pathCustomer, pathSave,
								nameUpload + ".zip");
						sourceFile = new File(path);
						entity.addPart("upFile", new FileBody(sourceFile));
						entity.addPart("upType", new StringBody("HOSOMOI"));
						break;
					case 2:
						path = compressFilesToZip(pathCustomer, pathSave,
								nameUpload + ".zip");
						sourceFile = new File(path);
						entity.addPart("upFile", new FileBody(sourceFile));

						entity.addPart("reason", new StringBody(reason));
						entity.addPart("upType", new StringBody("HOSOBOSUNG"));
						break;
					case 3:
						entity.addPart("reason", new StringBody(reason));
						entity.addPart("upType", new StringBody("HOSOBOSUNG"));
						break;
					}

					// entity.addPart("appFolder", new
					// StringBody(nameCustomer));

					// totalSize = entity.getContentLength();
					httpPost.setEntity(entity);
					HttpResponse response = httpClient.execute(httpPost);
					HttpEntity r_entity = response.getEntity();
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == 200) {
						// Server response
						responseString = EntityUtils.toString(r_entity);
					} else {
						responseString = "Error occurred! Http Status Code: "
								+ statusCode;
					}

				} catch (ClientProtocolException e) {
					responseString = e.toString();
				} catch (IOException e) {
					responseString = e.toString();
				}

				return responseString;
			}
		}.execute();
	}

	private ArrayList<File> getListFdfFile() {
		// TODO Auto-generated method stub
		ArrayList<File> arr = new ArrayList<File>();
		List<String> listPDF = operation.listImagebyCategory(pathCustomer,
				".pdf", "path");
		for (int i = 0; i < listPDF.size(); i++) {
			Log.d("pat", listPDF.get(i));
			// TextView txtView = (TextView)
			// listView.getChildAt(i).findViewById(
			// R.id.item);

			File file = new File(listPDF.get(i));

			arr.add(file);

		}

		return arr;

	}
}
