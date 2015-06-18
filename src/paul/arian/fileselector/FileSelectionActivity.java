package paul.arian.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Driver;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.activity.R;
import digi.mobile.activity.UploadActivity;
import digi.mobile.building.AndroidMultiPartEntity;
import digi.mobile.building.AndroidMultiPartEntity.ProgressListener;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;

public class FileSelectionActivity extends Activity {

	private static final String TAG = "FileSelection";
	private static final String FILES_TO_UPLOAD = "upload";
	File mainPath = new File(Environment.getExternalStorageDirectory() + "");
	private ArrayList<File> resultFileList;

	private ListView directoryView;
	private ArrayList<File> directoryList = new ArrayList<File>();
	private ArrayList<String> directoryNames = new ArrayList<String>();
	// private ListView fileView;
	private ArrayList<File> fileList = new ArrayList<File>();
	private ArrayList<String> fileNames = new ArrayList<String>();
	Button ok, all, cancel, storage, New;
	TextView path;
	Boolean Switch = false;

	Boolean switcher = false;
	String primary_sd;
	String secondary_sd;

	int index = 0;
	int top = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_selection);
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		directoryView = (ListView) findViewById(R.id.directorySelectionList);
		// ok = (Button) findViewById(R.id.ok);
		// all = (Button) findViewById(R.id.all);
		cancel = (Button) findViewById(R.id.cancel);
		storage = (Button) findViewById(R.id.storage);
		New = (Button) findViewById(R.id.New);
		path = (TextView) findViewById(R.id.folderpath);

		loadLists();
		New.setEnabled(false);

		ExtStorageSearch();
		if (secondary_sd == null) {
			storage.setEnabled(false);
		}

		directoryView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				index = directoryView.getFirstVisiblePosition();
				View v = directoryView.getChildAt(0);
				top = (v == null) ? 0 : v.getTop();

				File lastPath = mainPath;

				try {
					if (position < directoryList.size()) {
						mainPath = directoryList.get(position);
						loadLists();
					}
				} catch (Throwable e) {
					mainPath = lastPath;
					loadLists();
				}
				Log.e("path", mainPath.getPath());

				// String path = lastPath.getPath() + File.separator
				// + directoryView.getItemAtPosition(position).toString();
				// // Log.d("^_^", path);
				// openDialog(path, position);
				//
				// if (directoryView.isItemChecked(position)) {
				// String path = lastPath.getPath()
				// + File.separator
				// + directoryView.getItemAtPosition(position)
				// .toString();
				// Log.d("^_^", path);
				// openDialog(path, position);
				// File file = new File(path);
				// if (checkImage(file)) {
				//
				// openDialog(path);
				// } else {
				//
				// }
				//
				// // }

			}
		});

		// ok.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// ok();
		// }
		// });

		directoryView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = mainPath.getPath() + File.separator
						+ directoryView.getItemAtPosition(position).toString();
				openDialog(path, position);
				return false;
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		storage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (!switcher) {
						mainPath = new File(secondary_sd);
						loadLists();
						switcher = true;
						storage.setText(getString(R.string.Int));
					} else {
						mainPath = new File(primary_sd);
						loadLists();
						switcher = false;
						storage.setText(getString(R.string.ext));
					}
				} catch (Throwable e) {

				}
			}
		});

		// all.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// if (!Switch) {
		// for (int i = directoryList.size(); i < directoryView
		// .getCount(); i++) {
		// directoryView.setItemChecked(i, true);
		// }
		// all.setText(getString(R.string.none));
		// Switch = true;
		// } else if (Switch) {
		// for (int i = directoryList.size(); i < directoryView
		// .getCount(); i++) {
		// directoryView.setItemChecked(i, false);
		// }
		// all.setText(getString(R.string.all));
		// Switch = false;
		// }
		// }
		//
		// });
	}

	public void onBackPressed() {
		try {
			if (mainPath.equals(Environment.getExternalStorageDirectory()
					.getParentFile().getParentFile())) {
				finish();
			} else {
				File parent = mainPath.getParentFile();
				mainPath = parent;
				loadLists();
				directoryView.setSelectionFromTop(index, top);
			}

		} catch (Throwable e) {

		}
	}

	public void ok() {
		Log.d(TAG, "Upload clicked, finishing activity");

		resultFileList = new ArrayList<File>();

		for (int i = 0; i < directoryView.getCount(); i++) {
			if (directoryView.isItemChecked(i)) {
				resultFileList.add(fileList.get(i - directoryList.size()));
			}
		}
		if (resultFileList.isEmpty()) {
			Log.d(TAG, "Nada seleccionado");
			finish();
		}

		Intent result = this.getIntent();
		result.putExtra(FILES_TO_UPLOAD, resultFileList);
		setResult(Activity.RESULT_OK, result);

		finish();
	}

	private void loadLists() {
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		// if(mainPath.exists() && mainPath.length()>0){
		// Lista de directorios
		File[] tempDirectoryList = mainPath.listFiles(directoryFilter);

		if (tempDirectoryList != null && tempDirectoryList.length > 1) {
			Arrays.sort(tempDirectoryList, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}

		directoryList = new ArrayList<File>();
		directoryNames = new ArrayList<String>();
		for (File file : tempDirectoryList) {
			directoryList.add(file);
			directoryNames.add(file.getName());
		}
		ArrayAdapter<String> directoryAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, directoryNames);

		// Lista de ficheros
		File[] tempFileList = mainPath.listFiles(fileFilter);

		if (tempFileList != null && tempFileList.length > 1) {
			Arrays.sort(tempFileList, new Comparator<File>() {
				@Override
				public int compare(File object1, File object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}

		fileList = new ArrayList<File>();
		fileNames = new ArrayList<String>();
		for (File file : tempFileList) {
			if (Constant.TYPE == 1 || Constant.TYPE == 2 || Constant.TYPE == 3) {
				if (checkImage(file)) {
					fileList.add(file);
					fileNames.add(file.getName());
				}
			} else {
				if (checkZip(file)) {
					fileList.add(file);
					fileNames.add(file.getName());
				}
			}
		}

		path.setText(mainPath.toString());
		iconload();
		setTitle(mainPath.getName());
		// }
	}

	/**
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 *           getMenuInflater().inflate(R.menu.activity_file_selection,
	 *           menu); return true; }
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 *           (item.getItemId()) { case android.R.id.home:
	 *           NavUtils.navigateUpFromSameTask(this); return true; } return
	 *           super.onOptionsItemSelected(item); }
	 **/

	public void iconload() {
		String[] foldernames = new String[directoryNames.size()];
		foldernames = directoryNames.toArray(foldernames);

		String[] filenames = new String[fileNames.size()];
		filenames = fileNames.toArray(filenames);

		CustomListSingleOnly adapter1 = new CustomListSingleOnly(
				FileSelectionActivity.this,
				directoryNames.toArray(foldernames), mainPath.getPath());
		CustomList adapter2 = new CustomList(FileSelectionActivity.this,
				fileNames.toArray(filenames), mainPath.getPath());

		MergeAdapter adap = new MergeAdapter();

		adap.addAdapter(adapter1);
		adap.addAdapter(adapter2);

		directoryView.setAdapter(adap);
	}

	public void ExtStorageSearch() {
		String[] extStorlocs = { "/storage/sdcard1", "/storage/extsdcard",
				"/storage/sdcard0/external_sdcard", "/mnt/extsdcard",
				"/mnt/sdcard/external_sd", "/mnt/external_sd",
				"/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc",
				"/storage/external_SD", "/storage/ext_sd",
				"/storage/removable/sdcard1", "/data/sdext", "/data/sdext2",
				"/data/sdext3", "/data/sdext4", "/storage/sdcard0" };

		// First Attempt
		primary_sd = System.getenv("EXTERNAL_STORAGE");
		secondary_sd = System.getenv("SECONDARY_STORAGE");

		if (primary_sd == null) {
			primary_sd = Environment.getExternalStorageDirectory() + "";
		}
		if (secondary_sd == null) {// if fail, search among known list of
									// extStorage Locations
			for (String string : extStorlocs) {
				if ((new File(string)).exists()
						&& (new File(string)).isDirectory()) {
					secondary_sd = string;
					break;
				}
			}
		}

	}

	public boolean checkImage(File file) {
		String[] okFileExtensions = new String[] { "jpg", "png" };
		for (String extension : okFileExtensions) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkZip(File file) {
		String[] okFileExtensions = new String[] { "zip", "rar", "pdf" };
		for (String extension : okFileExtensions) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	public void openDialog(final String path, final int position) {

		final Dialog dialog = new Dialog(FileSelectionActivity.this,
				R.style.MyTheme_Dialog_Action);
		// dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_signout);
		dialog.show();

		File file = new File(path);
		// init button OK and Cancel
		Button btnOk = (Button) dialog.findViewById(R.id.button1);
		Button btnCancel = (Button) dialog.findViewById(R.id.button2);
		TextView txtTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
		TextView txtContent = (TextView) dialog.findViewById(R.id.TextView1);
		if (file.isFile()) {
			if (Constant.TYPE == 4) {
				if (!(FilenameUtils.getExtension(path).toLowerCase()
						.equals("zip") || FilenameUtils.getExtension(path)
						.toLowerCase().equals("rar"))) {
					btnCancel.setVisibility(View.GONE);
					txtContent.setText("Type file khong sp");
					btnOk.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					return;
				}
			}
		}

		if (Constant.TYPE == 1 || Constant.TYPE == 2 || Constant.TYPE == 3) {
			txtTitle.setText(getString(R.string.title_open_file));
			txtContent.setText(getString(R.string.open_file));

			// handling clicks
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					returnData(path);

				}
			});

			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		} else {
			txtTitle.setText(getString(R.string.upload));
			txtContent.setText(getString(R.string.choose_type));
			btnOk.setText(getString(R.string.New));
			btnCancel.setText(getString(R.string.supplement));

			// handling clicks
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Constant.TYPE = 1;
					/*
					 * client = new AsyncHttpClient();
					 * 
					 * client.get(url + "HOSOMOI", new
					 * AsyncHttpResponseHandler() {
					 * 
					 * @Override public void onStart() { // TODO Auto-generated
					 * method stub super.onStart();
					 * 
					 * }
					 * 
					 * // When the response returned by REST has // Http //
					 * response code // '200'
					 * 
					 * @Override public void onSuccess(String response) { try {
					 * 
					 * JSONObject obj = new JSONObject(response); int count =
					 * Integer.parseInt(obj .getString("count")) + 1; // name
					 * Zip file upload nameUpload = userName + "_" + dateFormat
					 * + "_" + count;
					 * 
					 * // // uploadFileToServer2(userName, channel, //
					 * "HOSOMOI", null, pathFile, cus_id, // cus_name, null);
					 * 
					 * File file = new File(path); Log.e("file",
					 * file.getPath()); // is ZIp file if (file.isFile()) { File
					 * fileUpload = new File(file.getParent() + nameUpload);
					 * Log.e("fileUpload", fileUpload.getPath()); if
					 * (file.renameTo(fileUpload)) { Log.e("file",
					 * file.getPath()); uploadFileToServer2(userName, channel,
					 * "HOSOMOI", null, file.getPath(), null, null, null); } }
					 * else { // is directory
					 * 
					 * }
					 * 
					 * } catch (JSONException e) { Toast.makeText(
					 * FileSelectionActivity.this,
					 * "Error Occured [Server's JSON response might be invalid]!"
					 * , Toast.LENGTH_LONG).show(); e.printStackTrace();
					 * 
					 * }
					 * 
					 * }
					 * 
					 * // When the response returned by REST has // Http //
					 * response code // other than '200'
					 * 
					 * @Override public void onFailure(int statusCode, Throwable
					 * error, String content) { // When Http response code is
					 * '404' if (statusCode == 404) { Toast.makeText(
					 * FileSelectionActivity.this,
					 * "Error 404: Requested resource not found",
					 * Toast.LENGTH_LONG).show(); } // When Http response code
					 * is '500' else if (statusCode == 500) { Toast.makeText(
					 * FileSelectionActivity.this,
					 * "Error 500: Something went wrong at server end",
					 * Toast.LENGTH_LONG).show(); } // When Http response code
					 * other than // 404, 500 else { Toast.makeText(
					 * FileSelectionActivity.this,
					 * "Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]"
					 * , Toast.LENGTH_LONG).show(); } } }); dialog.dismiss();
					 */
					dialog.dismiss();
					openUpload("HOSOMOI", path);
					// returnData(path);

				}
			});

			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Constant.TYPE = 2;
					dialog.dismiss();
					openUpload("HOSOBOSUNG", path);
					// returnData(path);
				}
			});

		}
		//
		// AlertDialog.Builder alertDialog = new AlertDialog.Builder(
		// FileSelectionActivity.this);
		// alertDialog.setTitle("Open file");
		// alertDialog.setMessage("Are you sure you want to open it?");
		// alertDialog.setIcon(R.drawable.ic_launcher);
		// alertDialog.setPositiveButton("YES",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// returnData(path);
		// }
		// });
		// alertDialog.setNegativeButton("NO",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// directoryView.setItemChecked(position, false);
		// dialog.cancel();
		// }
		// });
		// alertDialog.show();
	}

	public void returnData(String path) {
		Intent intent = this.getIntent();
		intent.putExtra("path", path);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public void openUpload(String type, String path) {
		Intent intent = new Intent(FileSelectionActivity.this,
				UploadActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("path", path);
		startActivity(intent);
	}

}
