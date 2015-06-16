package digi.mobile.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import com.lowagie.text.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

		// create folder of application
		if (operation.createDirIfNotExists(Constant.APP_FOLDER, 0)) {
			appPath = Constant.getPathRoot(Constant.APP_FOLDER);
		} else {
			Toast.makeText(MainActivity.this,
					getString(R.string.create_folder_error), Toast.LENGTH_SHORT)
					.show();
		}
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
		// startService(intentService);
		// registerReceiver(broadcastReceiver, new IntentFilter(
		// BroadcastService.BROADCAST_ACTION));

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

			break;
		case R.id.profile:

			break;
		case R.id.help:

			break;
		case R.id.about:
			signOut();
			break;

		}
	}

	private void createCustomer() {
		Intent intent = new Intent(MainActivity.this, NewCustomerActivity.class);
		startActivity(intent);
	}
}
