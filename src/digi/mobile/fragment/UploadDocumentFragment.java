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
import org.w3c.dom.Text;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.HistoryActivity;
import digi.mobile.activity.LoginActivity;
import digi.mobile.activity.OptionActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.AdapterGridView;
import digi.mobile.adapter.PdfListAdapter;
import digi.mobile.building.AndroidMultiPartEntity;
import digi.mobile.building.ConnectionDetector;
import digi.mobile.building.DatabaseHelper;
import digi.mobile.building.DigiCompressFile;
import digi.mobile.building.IEventListener;
import digi.mobile.building.ImageItem;
import digi.mobile.building.AndroidMultiPartEntity.ProgressListener;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class UploadDocumentFragment extends Fragment implements OnClickListener {

	RelativeLayout relAfter, relUpload;
	LinearLayout llReason, llPdf;
	TextView txtReason;
	String pathCustomer;
	String nameShortDucoment;
	Operation operation = new Operation();
	ArrayList<ImageItem> arrayImageItem;

	private IEventListener listener;
	ListView listView;
	TextView txtFolder, txtFile;
	Dialog dialog;
	AnimationDrawable animation;
	AsyncHttpClient client;
	TextView txtLoading;
	String pathZip, pathSave;
	String userName, channel, cus_id, cus_name, reason, pathFile = null, idf1,
			date, dateFormat, url, nameUpload;
	long totalSize = 0;
	DatabaseHelper databaseHelper;
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

			txtFolder.setText(Constant.NAME_CUSTOMER);
			checkType(Constant.TYPE);

			// lockClick(View.GONE);

		} else {
			Log.e("ErrorFragment", "CreateDocumentFragment gone");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(
				R.layout.fragment_upload_fragment, container, false);
		listView = (ListView) myFragmentView.findViewById(R.id.lvPdf);
		txtFolder = (TextView) myFragmentView.findViewById(R.id.txtFolder);
		txtFile = (TextView) myFragmentView.findViewById(R.id.txtFile);
		llPdf = (LinearLayout) myFragmentView.findViewById(R.id.relPdf);
		llReason = (LinearLayout) myFragmentView.findViewById(R.id.relReason);
		txtReason = (TextView) myFragmentView.findViewById(R.id.txtReason);
		relAfter = (RelativeLayout) myFragmentView.findViewById(R.id.relAfter);
		relUpload = (RelativeLayout) myFragmentView
				.findViewById(R.id.relUpload);
		relAfter.setOnClickListener(this);
		relUpload.setOnClickListener(this);
		databaseHelper = new DatabaseHelper(getActivity());
		try {
			databaseHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
		
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
		return myFragmentView;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.relAfter:

			listener.sendDataToActivity(Constant.Step_1);

			break;
		case R.id.relUpload:
			// checkUpload();
			uploadZipFile();
			break;
		default:
			break;
		}
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

	private void checkType(int type) {
		switch (type) {
		case 1:
			llReason.setVisibility(View.GONE);
			showPdf();
			break;
		case 2:
			// llReason.setVisibility(View.VISIBLE);
			showPdf();
			txtReason.setText(Constant.REASON);
			break;
		case 3:
			txtReason.setText(Constant.REASON);
			llPdf.setVisibility(View.GONE);
			break;

		}

	}

	private void checkUpload() {
		switch (Constant.TYPE) {
		case 1:// new
				// checkListView();
			uploadZipFile();
			// if (checkLvPDF()) {
			// uploadZipFile();
			// compressFilesToZip(pathCustomer, pathSave, "thai" + ".zip");
			// }
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

	private boolean checkEtReason() {
		if (txtReason.getText().toString().isEmpty()) {
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
		txtLoading = (TextView) dialog.findViewById(R.id.textViewLoading);
		txtLoading.setText("Uploading 0%");
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

											// reason = edReason.getText()
											// .toString();
											uploadFileToServer2(userName,
													channel, "HOSOMOI", null,
													pathFile, cus_id, cus_name,
													null,date);

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

										animation.stop();
										dialog.dismiss();

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

											reason = txtReason.getText()
													.toString();
											idf1 = Constant.IDF1;
											uploadFileToServer2(userName,
													channel, "HOSOBOSUNG",
													reason, pathFile, cus_id,
													cus_name, idf1,date);

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
										animation.stop();
										dialog.dismiss();

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
						reason = txtReason.getText().toString();
						idf1 = Constant.IDF1;

						uploadFileToServer2(userName, channel, "HOSOBOSUNG",
								reason, "", cus_id, cus_name, idf1,date);

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
		DigiCompressFile compressFile = new DigiCompressFile();
		try {
			return compressFile.compressFolderToZip(pathFolder, pathSave,
					zipName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void uploadFileToServer2(final String userName,
			final String channel, final String upType, final String reason,
			final String pathFile, final String cus_id, final String cus_name,
			final String idf1,final String date

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
				txtLoading.setText("Uploading " + values[0] + "%");
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
								
				final File file = new File(pathFile);
				file.delete();	
				 databaseHelper.insertStory(date, cus_name,
						 cus_id,
				 upType);
				getActivity().finish();

//				final Dialog dialog = new Dialog(getActivity(),
//						R.style.MyTheme_Dialog_Action);
//				dialog.setContentView(R.layout.dialog_signout);
//				dialog.show();
//
//				// init button OK and Cancel
//				Button btnOk = (Button) dialog.findViewById(R.id.button1);
//				Button btnCancel = (Button) dialog.findViewById(R.id.button2);
//				TextView txtTitle = (TextView) dialog
//						.findViewById(R.id.textViewTitle);
//				TextView txtContent = (TextView) dialog
//						.findViewById(R.id.TextView1);
//				txtTitle.setText(getString(R.string.upload_successfull));
//
//				final File file = new File(pathFile);
//				if (Constant.TYPE == 3) {
//					txtContent.setText(getString(R.string.update_content));
//					btnOk.setVisibility(View.GONE);
//					btnCancel.setText(getString(R.string.ok));
//
//				} else {
//					txtContent.setText(getString(R.string.delete_file) + " "
//							+ file.getName() + " file?");
//
//					// handling clicks
//					btnOk.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							getActivity().finish();
//							file.delete();
//							dialog.dismiss();
//						}
//					});
//
//				}
//				btnCancel.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						getActivity().finish();
//						dialog.dismiss();
//					}
//				});

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
									publishProgress((int) ((num / (float) totalSize) * 100));
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

					totalSize = entity.getContentLength();
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
