package digi.mobile.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Directory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.building.AndroidMultiPartEntity;
import digi.mobile.building.AndroidMultiPartEntity.ProgressListener;
import digi.mobile.building.DigiCompressFile;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;

public class UploadActivity extends Activity {

	String path, type;
	String userName, channel, cus_id, cus_name, reason, pathFile = null, idf1,
			date, dateFormat, url, nameUpload, name, pathSave;
	AsyncHttpClient client;
	RelativeLayout relativeLayout;
	EditText edCustomerName, edIdCard, edSales, edID, edReason;
	TextView txtReview, txtTypeFile;
	File fileUpload, file;
	String nameFile;
	String[] arr;
	Dialog dialog;
	AnimationDrawable animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
		// init editText
		edCustomerName = (EditText) findViewById(R.id.editTextUserName);
		edIdCard = (EditText) findViewById(R.id.EditText01);
		edSales = (EditText) findViewById(R.id.EditText02);
		edID = (EditText) findViewById(R.id.edCode);
		edReason = (EditText) findViewById(R.id.edReason);
		txtReview = (TextView) findViewById(R.id.textView2);
		txtTypeFile = (TextView) findViewById(R.id.textView1);
		edCustomerName.setError(getString(R.string.error_customer_name));
		edIdCard.setError(getString(R.string.error_customer_id));
		// edSales.setError(getString(R.string.error_channel));

		Intent intent = getIntent();
		path = intent.getStringExtra("path");
		type = intent.getStringExtra("type");

		SharedPreferences sharedPerferences = getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

		userName = sharedPerferences.getString(Constant.USER_NAME, null);
		channel = sharedPerferences.getString(Constant.CHANNEL, null);
		cus_id = Constant.ID_CUSTOMER;
		cus_name = Constant.NAME_CUSTOMER_ONLY;
		date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(new Date());
		dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
				.format(new Date());

		url = Config.UPLOAD_PER_DAY_URL + "?ccCode=" + userName
				+ "&upload_date=" + date + "&type=";

		pathSave = Constant.getPathRoot(Constant.APP_FOLDER + File.separator
				+ userName);

		file = new File(path);
		nameFile = file.getName();
		txtReview.setText(nameFile);
		// if (file.isFile()) {
		// if (FilenameUtils.getExtension(path).toLowerCase().equals("zip")
		// || FilenameUtils.getExtension(path).toLowerCase()
		// .equals("rar")) {
		// edCustomerName.setVisibility(View.GONE);
		// edIdCard.setVisibility(View.GONE);
		// edSales.setVisibility(View.GONE);
		// edID.setVisibility(View.GONE);
		// txtReview.setText(nameFile);
		//
		// }
		// } else {
		switch (type) {
		case "HOSOMOI":
			url = url + "HOSOMOI";
			edID.setVisibility(View.GONE);
			edReason.setVisibility(View.GONE);
			// arr = nameFile.split("_");
			// for (int i = 0; i < arr.length; i++) {
			//
			// switch (i) {
			// case 0:
			// edCustomerName.setText(arr[0]);
			// break;
			// case 1:
			// edIdCard.setText(arr[1]);
			// break;
			// case 2:
			// edSales.setText(arr[2]);
			// break;
			//
			// }
			// }

			break;
		case "HOSOBOSUNG":

			url = url + "HOSOBOSUNG";

			edID.setError(getString(R.string.error_id));
			edReason.setError(getString(R.string.error_empty));
			edReason.setText("");
			// arr = nameFile.split("_");
			//
			// for (int i = 0; i < arr.length; i++) {
			//
			// switch (i) {
			// case 0:
			// edCustomerName.setText(arr[0]);
			// break;
			// case 1:
			// edIdCard.setText(arr[1]);
			// break;
			// case 2:
			// edSales.setText(arr[2]);
			// break;
			//
			// case 3:
			// edID.setText(arr[3]);
			// break;
			// }
			// }
			// break;
		}

		// }

		edReason.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.toString().length() <= 0) {
					edReason.setError(getString(R.string.error_empty));
				} else {
					edReason.setError(null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

				// if (s.toString().length() <= 0) {
				// edCustomerName.setError("Customer name can NOT be empty");
				// } else {
				// edCustomerName.setError(null);
				// }
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// String temp = s.toString();
				// if (!temp.equals(temp.toUpperCase())) {
				// edCustomerName.setText(temp.toUpperCase());
				//
				// }
				// edCustomerName.setSelection(edCustomerName.getText().length());
				//
				//
				// String temp = s.toString();
				//
				// if (edIdCard.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edIdCard.getText().toString();
				// }
				//
				// if (edSales.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edSales.getText().toString();
				// }
				//
				// txtReview.setText(temp);

			}
		});

		edCustomerName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.toString().length() <= 0) {
					edCustomerName
							.setError(getString(R.string.error_customer_name));
				} else {
					edCustomerName.setError(null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

				// if (s.toString().length() <= 0) {
				// edCustomerName.setError("Customer name can NOT be empty");
				// } else {
				// edCustomerName.setError(null);
				// }
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// String temp = s.toString();
				// if (!temp.equals(temp.toUpperCase())) {
				// edCustomerName.setText(temp.toUpperCase());
				//
				// }
				// edCustomerName.setSelection(edCustomerName.getText().length());
				//
				//
				// String temp = s.toString();
				//
				// if (edIdCard.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edIdCard.getText().toString();
				// }
				//
				// if (edSales.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edSales.getText().toString();
				// }
				//
				// txtReview.setText(temp);

			}
		});

		edIdCard.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				int length = s.toString().length();
				if (length == 8 || length == 9 || length == 12) {
					edIdCard.setError(null);
				} else {

					edIdCard.setError(getString(R.string.error_customer_id));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				// String temp = s.toString();
				//
				// if (edCustomerName.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_" + temp;
				// }
				//
				// if (edSales.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edSales.getText().toString();
				// }
				//
				// txtReview.setText(temp);
			}
		});

		edSales.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				// int length = s.toString().length();
				// if (length == 3) {
				// edSales.setError(null);
				//
				// } else {
				// edSales.setError(getString(R.string.error_channel));
				// }

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// String temp = s.toString();
				//
				// if (edCustomerName.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_" + temp;
				// }
				//
				// if (edIdCard.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_"
				// + edIdCard.getText().toString() + "_"
				// + s.toString();
				// }
				//
				// txtReview.setText(temp);

				// if (edCustomerName.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_" + temp;
				// }
				//
				// if (edIdCard.getText().toString().length() > 0) {
				//
				// temp = edIdCard.getText().toString() + "_" + temp;
				// }

				// txtReview.setText(temp);
			}
		});

		edID.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				int length = s.toString().length();
				if (length == 7) {
					edID.setError(null);
				} else {

					edID.setError(getString(R.string.error_id));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				// String temp = s.toString();
				//
				// if (edCustomerName.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_" + temp;
				// }
				//
				// if (edSales.getText().toString().length() > 0) {
				//
				// temp = temp + "_" + edSales.getText().toString();
				// }
				//
				//
				// txtReview.setText(temp);
			}
		});

		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (edCustomerName.getError() != null
						|| edIdCard.getError() != null
						|| edSales.getError() != null
						|| edID.getError() != null
						|| edReason.getError() != null) {
					final Dialog dialog = new Dialog(UploadActivity.this,
							R.style.MyTheme_Dialog_Action);
					dialog.setContentView(R.layout.dialog_warning);
					Button btnOk = (Button) dialog.findViewById(R.id.btnCreate);
					btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					// init dialog
					dialog = new Dialog(UploadActivity.this,
							R.style.Theme_D1NoTitleDim);
					dialog.setContentView(R.layout.dialog_loading_animation);
					dialog.setCanceledOnTouchOutside(false);

					// init TextViewLoading and ImageLoading
					TextView txtLoading = (TextView) dialog
							.findViewById(R.id.textViewLoading);
					txtLoading.setText("Loading...");
					ImageView imageLoading = (ImageView) dialog
							.findViewById(R.id.imageViewLoading);
					imageLoading
							.setBackgroundResource(R.drawable.animation_loading);
					// using Animation for ImageLoading
					animation = (AnimationDrawable) imageLoading
							.getBackground();

					client = new AsyncHttpClient();

					client.get(url, new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							dialog.show();
							animation.start();
						}

						// When the response returned by REST has
						// Http
						// response code
						// '200'
						@Override
						public void onSuccess(String response) {
							try {

								JSONObject obj = new JSONObject(response);
								int count = Integer.parseInt(obj
										.getString("count")) + 1;
								Log.e("coutn", count + "");
								// name Zip file upload
								nameUpload = userName + "_" + dateFormat + "_"
										+ count;

								switch (type) {
								case "HOSOMOI":
									fileUpload = new File(file.getParent()
											+ File.separator + nameUpload
											+ ".zip");

									break;
								case "HOSOBOSUNG":
									nameUpload = "QDE_" + nameUpload;
									fileUpload = new File(file.getParent()
											+ File.separator + nameUpload
											+ ".zip");

									break;
								}

								if (file.isFile()) {
									if (FilenameUtils.getExtension(path)
											.toLowerCase().equals("zip")
											|| FilenameUtils.getExtension(path)
													.toLowerCase()
													.equals("rar")) {

										txtTypeFile.setText("File: ");

										if (!fileUpload.exists()
												&& file.renameTo(fileUpload)) {

											uploadFileToServer2(userName,
													channel, type, edReason
															.getText()
															.toString(),
													fileUpload.getPath(),
													edIdCard.getText()
															.toString(),
													edCustomerName.getText()
															.toString(), edID
															.getText()
															.toString(), dialog);
										} else {
											Log.e("error", "can't rename");
										}

									}
								} else {
									txtTypeFile.setText("Folder: ");
									name = edCustomerName.getText().toString()
											+ "_"
											+ edIdCard.getText().toString()
											+ "_"
											+ edSales.getText().toString();
									switch (type) {
									case "HOSOMOI":

										fileUpload = new File(file.getParent()
												+ File.separator + name);
										break;
									case "HOSOBOSUNG":
										name = name + "_"
												+ edID.getText().toString();
										fileUpload = new File(file.getParent()
												+ File.separator + name);
										break;
									}

									if (file.getName().equals(
											fileUpload.getName())) {
										uploadFileToServer2(
												userName,
												channel,
												type,
												edReason.getText().toString(),
												compressFilesToZip(
														fileUpload.getPath(),
														pathSave, nameUpload
																+ ".zip"),
												edIdCard.getText().toString(),
												edCustomerName.getText()
														.toString(), edID
														.getText().toString(),
												dialog);
									} else if (fileUpload.exists()) {

										try {
											FileUtils
													.deleteDirectory(fileUpload);
										} catch (IOException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
										}

										if (file.renameTo(fileUpload)) {
											Log.e("OK", "OK");
											uploadFileToServer2(
													userName,
													channel,
													type,
													edReason.getText()
															.toString(),
													compressFilesToZip(
															fileUpload
																	.getPath(),
															pathSave,
															nameUpload + ".zip"),
													edIdCard.getText()
															.toString(),
													edCustomerName.getText()
															.toString(), edID
															.getText()
															.toString(), dialog);

										} else {
											Log.e("NO", "NO");
										}
									} else {
										Log.e("Directory", "NoExist");
										if (file.renameTo(fileUpload)) {

											uploadFileToServer2(
													userName,
													channel,
													type,
													edReason.getText()
															.toString(),
													compressFilesToZip(
															fileUpload
																	.getPath(),
															pathSave,
															nameUpload + ".zip"),
													edIdCard.getText()
															.toString(),
													edCustomerName.getText()
															.toString(), edID
															.getText()
															.toString(), dialog);
											// Log.e("error", "can't rename");
										}
									}

								}

							} catch (JSONException e) {
								Toast.makeText(
										UploadActivity.this,
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
						public void onFailure(int statusCode, Throwable error,
								String content) {
							// When Http response code is '404'
							if (statusCode == 404) {
								Toast.makeText(
										UploadActivity.this,
										"Error 404: Requested resource not found",
										Toast.LENGTH_LONG).show();
							}
							// When Http response code is '500'
							else if (statusCode == 500) {
								Toast.makeText(
										UploadActivity.this,
										"Error 500: Something went wrong at server end",
										Toast.LENGTH_LONG).show();
							}
							// When Http response code other than
							// 404, 500
							else {
								Toast.makeText(
										UploadActivity.this,
										"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
										Toast.LENGTH_LONG).show();
							}
						}
					});

				}

			}
		});

	}

	private void uploadFileToServer2(final String userName,
			final String channel, final String upType, final String reason,
			final String pathFile, final String cus_id, final String cus_name,
			final String idf1, final Dialog dialog) {
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
				// animation.stop();
				dialog.dismiss();
				// Toast.makeText(getActivity(), "Upload successful!",
				// Toast.LENGTH_LONG).show();

				final Dialog dialog = new Dialog(UploadActivity.this,
						R.style.MyTheme_Dialog_Action);
				// dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(R.layout.dialog_signout);
				dialog.show();

				// init button OK and Cancel
				Button btnOk = (Button) dialog.findViewById(R.id.button1);
				Button btnCancel = (Button) dialog.findViewById(R.id.button2);
				TextView txtTitle = (TextView) dialog
						.findViewById(R.id.textViewTitle);
				TextView txtContent = (TextView) dialog
						.findViewById(R.id.TextView1);
				txtTitle.setText(getString(R.string.upload_successfull));
				final File file = new File(pathFile);
				txtContent.setText(getString(R.string.delete_file) + " "
						+ file.getName() + " file?");
				// handling clicks
				btnOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						file.delete();
						dialog.dismiss();

						setResult(RESULT_OK);
						finish();
					}
				});

				btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						setResult(RESULT_OK);
						finish();
					}
				});
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

					switch (type) {

					case "HOSOMOI":

						sourceFile = new File(pathFile);
						entity.addPart("upFile", new FileBody(sourceFile));
						entity.addPart("upType", new StringBody(upType));
						break;
					case "HOSOBOSUNG":

						sourceFile = new File(pathFile);
						entity.addPart("upFile", new FileBody(sourceFile));
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
}
