package digi.mobile.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.building.BroadcastService;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class MainActivity extends FragmentActivity implements OnClickListener {
	// declare button
	private Button btnNew, btnSupplement, btnCamera, btnUpload;
	private Button btnProfile, btnHelp, btnSignOut;

	// declare object Operation
	private Operation operation;

	// Path of Application
	private String appPath;

	// Path of Sales
	private String pathMyFolder = "";

	// Dialog Loading
	private ProgressDialog dialog;

	// Declare the service for send require from device to server
	Intent intentService;

	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init button
		btnNew = (Button) findViewById(R.id.gallery);
		btnSupplement = (Button) findViewById(R.id.supplement);
		btnCamera = (Button) findViewById(R.id.camera);
		btnUpload = (Button) findViewById(R.id.upload);
		btnProfile = (Button) findViewById(R.id.profile);
		btnHelp = (Button) findViewById(R.id.help);
		btnSignOut = (Button) findViewById(R.id.about);

		// set onclick for button
		btnNew.setOnClickListener(this);
		btnSupplement.setOnClickListener(this);
		btnCamera.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnProfile.setOnClickListener(this);
		btnHelp.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);

		// init object operation
		operation = new Operation();
		intentService = new Intent(MainActivity.this, BroadcastService.class);

		sharedPreferences = getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
		
		
//		Editor editor = sharedPreferences.edit();
//		editor.putString(Constant.USER_NAME, "CC999999");
//		editor.putString(Constant.PASSWORD, "123456");
//		editor.putString(Constant.CHANNEL,
//				"AAA");
//		editor.putBoolean(Constant.FLAG_KEY, true);
//		editor.commit();
		
		
		
		// create folder of application
		if (operation.createDirIfNotExists(Constant.APP_FOLDER, 0)) {
			appPath = Constant.getPathRoot(Constant.APP_FOLDER);
		} else {
			Toast.makeText(MainActivity.this,
					getString(R.string.create_folder_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//
		// if (resultCode != RESULT_OK) {
		// return;
		// }
		//
		// switch (requestCode) {
		// case Constant.REQUEST_CODE_GALLERY:
		// String path = data.getStringExtra("path");
		//
		// break;
		// }
	}

	/** The broadcast receiver for receiver message from the server */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		// send message from server
//		 startService(intentService);
//		registerReceiver(broadcastReceiver, new IntentFilter(
//		 BroadcastService.BROADCAST_ACTION));

		// check Sign In
		checkLogIn();
		super.onResume();
	}

	private void checkLogIn() {
		// sharedPreferences = getSharedPreferences(
		// Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
		if (!sharedPreferences.getBoolean(Constant.FLAG_KEY, false)) {
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(i);
		} else {
			Constant.NAME_USER = sharedPreferences.getString(
					Constant.USER_NAME, null);
			// Toast.makeText(getApplicationContext(), Constant.NAME_USER,
			// Toast.LENGTH_LONG).show();
		}

	}

	private void newCustomer() {

	}

	private void signOut() {
		// init Dialog Notification
		final Dialog dialog = new Dialog(MainActivity.this,
				R.style.MyTheme_Dialog_Action);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_signout);
		dialog.show();

		// init button OK and Cancel
		Button btnOk = (Button) dialog.findViewById(R.id.button1);
		Button btnCancel = (Button) dialog.findViewById(R.id.button2);

		// handling clicks
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Editor editor = sharedPreferences.edit();
				// editor.clear();
				editor.putBoolean(Constant.FLAG_KEY, false);
				// editor.commit();
				editor.apply();
				dialog.dismiss();
				dialog.cancel();
				checkLogIn();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	private void profile() {
		// init Dialog Notification
		final Dialog dialog = new Dialog(MainActivity.this,
				R.style.MyTheme_Dialog_Action);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_info);
		dialog.show();

		// init button OK and Cancel
		Button btnOk = (Button) dialog.findViewById(R.id.button1);
		TextView txtUserName = (TextView) dialog.findViewById(R.id.textView1);
		TextView txtSalesChannel = (TextView) dialog
				.findViewById(R.id.TextView02);

		txtUserName.setText(sharedPreferences.getString(Constant.USER_NAME,
				null));
		txtSalesChannel.setText(sharedPreferences.getString(Constant.CHANNEL,
				null));

		// handling clicks
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.gallery:
			Constant.TYPE = 1;
			createCustomer();
			break;
		case R.id.supplement:
			Constant.TYPE = 2;
			createCustomer();
			break;
		case R.id.camera:
			Constant.TYPE = 3;
			createCustomer();
			break;
		case R.id.upload:
			Constant.TYPE = 4;
			Intent intent = new Intent(MainActivity.this,
					FileSelectionActivity.class);
			startActivityForResult(intent, Constant.REQUEST_CODE_GALLERY);
			break;
		case R.id.profile:
//			profile();
			history();
			break;
		case R.id.help:
			help();
			break;
		case R.id.about:
			signOut();
			break;

		}
	}

	private void createCustomer() {
		Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
		startActivity(intent);
	}

	private void help(){
		
		final Dialog dialog = new Dialog(MainActivity.this,
				R.style.MyTheme_Dialog_Action);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_help);
		dialog.show();
		
		TextView txt = (TextView) dialog.findViewById(R.id.textView1);
		String data = "";
		StringBuffer sbuffer = new StringBuffer();
		InputStream is = this.getResources().openRawResource(
				R.drawable.help);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		if (is != null) {
			try {
				while ((data = reader.readLine()) != null) {
					sbuffer.append(data + "\n");
				}
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		txt.setText( sbuffer.toString());
//		Toast.makeText(getBaseContext(), sbuffer.toString(), Toast.LENGTH_LONG)
//				.show();
	}
	
	private void history(){
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
	}
}
