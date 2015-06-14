package digi.mobile.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class LoginActivity extends Activity implements OnClickListener {

	// Declare ImageButton Exit
	ImageButton btnExit;

	// Declare Button Login
	Button btnLogin;

	// Declare EditText Username and Password
	EditText edUserName, edPassword;

	// Declare CheckBox show password
	CheckBox checkBox;

	// Declare SharedPreferences storage information User
	SharedPreferences sharedPreferences;

	// Dialog Loading User
	Dialog dialog;

	// TextView and ImageView display % Loading
	TextView txtLoading;
	ImageView imageLoading;

	// Animation for ImageView loading
	AnimationDrawable animation;

	Operation operation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setFinishOnTouchOutside(false);

		// init Operation
		operation = new Operation();
		// init ImageButton Exit
		btnExit = (ImageButton) findViewById(R.id.imageButtonExit);

		// init Button Login
		btnLogin = (Button) findViewById(R.id.btnLogin);

		// init TextView Username and Password
		edUserName = (EditText) findViewById(R.id.editTextCustomerName);
		edPassword = (EditText) findViewById(R.id.editTextPass);

		// init CheckBox show password
		checkBox = (CheckBox) findViewById(R.id.checkBoxShowPass);

		// init SharedPreferences storage information User
		sharedPreferences = getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

		// handling clicks for button
		btnLogin.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					edPassword
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());

				} else {
					edPassword
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				}
				edPassword.setSelection(edPassword.getText().length());
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btnLogin:
			loginUser();
			break;

		case R.id.imageButtonExit:
			finish();
			break;
		}
	}

	private void createFolderUser(String nameUser) {
		// create folder of application
		if (!operation.createDirIfNotExists(Constant.APP_FOLDER
				+ File.separator + nameUser, 0)) {
			Toast.makeText(LoginActivity.this,
					getString(R.string.create_folder_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void loginUser() {
		final String userName = edUserName.getText().toString();
		final String passWord = edPassword.getText().toString();
		Log.d("password", passWord);
		String url = "http://www.digi-texx.vn/vpbank/ws_checkUser.php?username="
				+ userName + "&password=" + passWord;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();

				dialog = new Dialog(LoginActivity.this,
						R.style.Theme_D1NoTitleDim);
				dialog.setContentView(R.layout.dialog_loading_animation);

				// dialog.setCanceledOnTouchOutside(false);
				// init TextViewLoading and ImageLoading
				txtLoading = (TextView) dialog
						.findViewById(R.id.textViewLoading);
				txtLoading.setText("Login...");
				imageLoading = (ImageView) dialog
						.findViewById(R.id.imageViewLoading);
				imageLoading
						.setBackgroundResource(R.drawable.animation_loading);

				// using Animation for ImageLoading
				animation = (AnimationDrawable) imageLoading.getBackground();
				animation.start();
				dialog.show();

			}

			// When the response returned by REST has Http response code
			// '200'
			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				try {
					if (dialog.isShowing())
						dialog.dismiss();
					// JSON Object
					JSONObject obj = new JSONObject(response);
					// When the JSON response has status boolean value
					// assigned with true
					if (obj.getString("status").equals("1")) {
						Toast.makeText(getApplicationContext(),
								"You are successfully logged in!",
								Toast.LENGTH_LONG).show();
						Editor editor = sharedPreferences.edit();
						editor.putString(Constant.USER_NAME, userName);
						editor.putString(Constant.PASSWORD, passWord);
						editor.putBoolean(Constant.FLAG_KEY, true);
						editor.commit();

						//
						createFolderUser(userName);

						// moveTaskToBack(true);
						LoginActivity.this.finish();
					}
					// Else display error message
					else {
						Toast.makeText(
								getApplicationContext(),
								obj.getString("The user name or password is incorrect"),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(
							getApplicationContext(),
							"Error Occured [Server's JSON response might be invalid]!",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();

				}

			}

			// When the response returned by REST has Http response code
			// other than '200'
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				// TODO Auto-generated method stub
				if (dialog.isShowing())
					dialog.dismiss();
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(),
							"Error 404: Requested resource not found",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(),
							"Error 500: Something went wrong at server end",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							getApplicationContext(),
							"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
