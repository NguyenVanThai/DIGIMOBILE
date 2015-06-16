package digi.mobile.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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

	Dialog dialog;
	AnimationDrawable animation;
	String pathZip;
	String userName, channel, cus_id, cus_name, reason, pathFile = null, idf1,
			date, dateFormat, url, nameUpload;
	AsyncHttpClient client;

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

				userName = sharedPerferences
						.getString(Constant.USER_NAME, null);
				channel = sharedPerferences.getString(Constant.CHANNEL, null);
				cus_id = Constant.ID_CUSTOMER;
				cus_name = Constant.NAME_CUSTOMER_ONLY;
				date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
						.format(new Date());
				dateFormat = new SimpleDateFormat("yyyyMMdd",
						Locale.getDefault()).format(new Date());

				url = Config.UPLOAD_PER_DAY_URL + "?ccCode=" + userName
						+ "&upload_date=" + date + "&type=";

				if (userName != null) {

					switch (Constant.TYPE) {
					case 1:

						client = new AsyncHttpClient();

						client.get(url + "HOSOMOI",
								new AsyncHttpResponseHandler() {

									@Override
									public void onStart() {
										// TODO Auto-generated method stub
										super.onStart();

									}

									// When the response returned by REST has
									// Http
									// response code
									// '200'
									@Override
									public void onSuccess(String response) {
										try {

											JSONObject obj = new JSONObject(
													response);
											int count = Integer.parseInt(obj
													.getString("count")) + 1;
											// name Zip file upload
											nameUpload = userName + "_"
													+ dateFormat + "_" + count;

											// create Zip file
											pathFile = compressFilesToZip(
													pathCustomer, pathSave,
													nameUpload + ".zip");

											reason = edReason.getText()
													.toString();
											uploadFileToServer2(userName,
													channel, "HOSOMOI", null,
													pathFile, cus_id, cus_name,
													null);

										} catch (JSONException e) {
											Toast.makeText(
													getActivity(),
													"Error Occured [Server's JSON response might be invalid]!",
													Toast.LENGTH_LONG).show();
											e.printStackTrace();

										}

									}

									// When the response returned by REST has
									// Http
									// response code
									// other than '200'
									@Override
									public void onFailure(int statusCode,
											Throwable error, String content) {
										// When Http response code is '404'
										if (statusCode == 404) {
											Toast.makeText(
													getActivity(),
													"Error 404: Requested resource not found",
													Toast.LENGTH_LONG).show();
										}
										// When Http response code is '500'
										else if (statusCode == 500) {
											Toast.makeText(
													getActivity(),
													"Error 500: Something went wrong at server end",
													Toast.LENGTH_LONG).show();
										}
										// When Http response code other than
										// 404, 500
										else {
											Toast.makeText(
													getActivity(),
													"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
													Toast.LENGTH_LONG).show();
										}
									}
								});

						break;
					case 2:

						client = new AsyncHttpClient();

						client.get(url + "HOSOBOSUNG",
								new AsyncHttpResponseHandler() {

									@Override
									public void onStart() {
										// TODO Auto-generated method stub
										super.onStart();

									}

									// When the response returned by REST has
									// Http
									// response code
									// '200'
									@Override
									public void onSuccess(String response) {
										try {

											JSONObject obj = new JSONObject(
													response);
											int count = Integer.parseInt(obj
													.getString("count")) + 1;
											// name Zip file upload
											nameUpload = "QDE" + "_" + userName
													+ "_" + dateFormat + "_"
													+ count;

											// create Zip file
											pathFile = compressFilesToZip(
													pathCustomer, pathSave,
													nameUpload + ".zip");

											reason = edReason.getText()
													.toString();
											idf1 = Constant.IDF1;
											uploadFileToServer2(userName,
													channel, "HOSOBOSUNG",
													reason, pathFile, cus_id,
													cus_name, idf1);

										} catch (JSONException e) {
											Toast.makeText(
													getActivity(),
													"Error Occured [Server's JSON response might be invalid]!",
													Toast.LENGTH_LONG).show();
											e.printStackTrace();

										}

									}

									// When the response returned by REST has
									// Http
									// response code
									// other than '200'
									@Override
									public void onFailure(int statusCode,
											Throwable error, String content) {
										// When Http response code is '404'
										if (statusCode == 404) {
											Toast.makeText(
													getActivity(),
													"Error 404: Requested resource not found",
													Toast.LENGTH_LONG).show();
										}
										// When Http response code is '500'
										else if (statusCode == 500) {
											Toast.makeText(
													getActivity(),
													"Error 500: Something went wrong at server end",
													Toast.LENGTH_LONG).show();
										}
										// When Http response code other than
										// 404, 500
										else {
											Toast.makeText(
													getActivity(),
													"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
													Toast.LENGTH_LONG).show();
										}
									}
								});

						break;
					case 3:
						reason = edReason.getText().toString();
						idf1 = Constant.IDF1;

						uploadFileToServer2(userName, channel, "HOSOBOSUNG",
								reason, pathFile, cus_id, cus_name, idf1);

						break;
					}
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

	@SuppressWarnings("unused")
	private void uploadHandle(String pathZip) {
		// TODO Auto-generated method stub
		ConnectionDetector cnnDec = new ConnectionDetector(getActivity());
		boolean isInternetPresent = cnnDec.isConnectingToInternet();
		if (isInternetPresent) {
			SharedPreferences sharedPerferences = getActivity()
					.getSharedPreferences(Constant.DIGI_LOGIN_PREFERENCES,
							Context.MODE_PRIVATE);
			if (sharedPerferences.contains(Constant.FLAG_KEY)) {
				userName = sharedPerferences
						.getString(Constant.USER_NAME, null);
				if (userName != null) {
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

	private void uploadFileToServer2(final String userName,
			final String channel, final String upType, final String reason,
			final String pathFile, final String cus_id, final String cus_name,
			final String idf1

	) {
		new AsyncTask<Void, Integer, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub

				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return uploadFile(userName, channel, upType, reason, pathFile,
						cus_id, cus_name, idf1);
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

			@SuppressWarnings("deprecation")
			private String uploadFile(final String userName,
					final String channel, final String upType,
					final String reason, final String pathFile,
					final String cus_id, final String cus_name,
					final String idf1) {
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
					entity.addPart("ccCode", new StringBody(userName));
					entity.addPart("ccChannel", new StringBody(channel));
					entity.addPart("cus_id", new StringBody(cus_id));
					entity.addPart("cus_name", new StringBody(cus_name));
					
					File sourceFile;

					switch (Constant.TYPE) {

					case 1:

						sourceFile = new File(pathFile);
						entity.addPart("upFile", new FileBody(sourceFile));
						entity.addPart("upType", new StringBody(upType));
						break;
					case 2:

						sourceFile = new File(pathFile);
						entity.addPart("upFile", new FileBody(sourceFile));
						entity.addPart("reason", new StringBody(reason));
						entity.addPart("upType", new StringBody(upType));
						entity.addPart("idf1", new StringBody(idf1));
						break;
					case 3:
						entity.addPart("reason", new StringBody(reason));
						entity.addPart("upType", new StringBody(upType));
						entity.addPart("idf1", new StringBody(idf1));
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

}
