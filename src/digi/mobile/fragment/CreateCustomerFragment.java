package digi.mobile.fragment;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.LoginActivity;
import digi.mobile.activity.R;
import digi.mobile.building.IEventListener;
import digi.mobile.building.UpdateableFragment;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class CreateCustomerFragment extends Fragment implements
		UpdateableFragment {

	RelativeLayout relativeLayoutHome, relativeLayoutBefore;
	EditText edCustomerName, edIdCard, edSales, edID;
	TextView txtReview;
	Operation operation;
	LinearLayout llID;
	Button btnCheckID;
	// Dialog Loading User
	Dialog dialog;

	// TextView and ImageView display % Loading
	TextView txtLoading;
	ImageView imageLoading;

	// Animation for ImageView loading
	AnimationDrawable animation;
	SharedPreferences sharedPreferences;
	private IEventListener listener;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// checkType();
			Log.e("UserVisibleHint", "UserVisibleHint");
		} else {
			// Log.d("frag1", "HIde");
		}
	}

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		operation = new Operation();

		View myFragmentView = inflater.inflate(
				R.layout.fragment_create_customer, container, false);

		relativeLayoutBefore = (RelativeLayout) myFragmentView
				.findViewById(R.id.LinearLayoutBefore);
		relativeLayoutHome = (RelativeLayout) myFragmentView
				.findViewById(R.id.LinearLayoutHome);
		// init editText
		edCustomerName = (EditText) myFragmentView
				.findViewById(R.id.editTextUserName);
		edIdCard = (EditText) myFragmentView.findViewById(R.id.EditText01);
		edSales = (EditText) myFragmentView.findViewById(R.id.EditText02);
		edID = (EditText) myFragmentView.findViewById(R.id.edCode);
		txtReview = (TextView) myFragmentView.findViewById(R.id.textView2);
		llID = (LinearLayout) myFragmentView.findViewById(R.id.llID);
		btnCheckID = (Button) myFragmentView.findViewById(R.id.btnCheckID);
		// set Sales channel
		sharedPreferences = getActivity().getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);
		String channel = sharedPreferences.getString(Constant.CHANNEL, null)
				.toString();
		edSales.setText(channel);

		// checkType();
		checkType(Constant.TYPE);

		btnCheckID.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkID();
			}
		});

		relativeLayoutBefore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// listener.sendDataToActivity("Hello World!");
				Log.e("Error",
						edCustomerName.getError() + "_" + edIdCard.getError()
								+ "_" + edID.getError());
				Log.e("onCreateView", "Type: " + Constant.TYPE);
				if (edCustomerName.getError() == null
						&& edIdCard.getError() == null
						&& edID.getError() == null) {

					Constant.NAME_CUSTOMER = getNameCustomer();
					String pathNameCustomer = Constant.APP_FOLDER
							+ File.separator + Constant.NAME_USER
							+ File.separator + Constant.NAME_CUSTOMER;
					createFolderUser(pathNameCustomer);

					listener.sendDataToActivity(Constant.Step_1);

				} else {

					final Dialog dialog = new Dialog(getActivity(),
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

				}
			}
		});

		relativeLayoutHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle(getString(R.string.dialog_exit));
				dialog.setMessage(getString(R.string.warning_exit));
				dialog.setIcon(R.drawable.ic_warning);
				dialog.setNegativeButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getActivity().finish();
							}
						});
				dialog.setPositiveButton("NO",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog.show();
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
			}

			@Override
			public void afterTextChanged(Editable et) {
				// TODO Auto-generated method stub

				String s = et.toString();
				if (!s.equals(s.toUpperCase())) {
					s = s.toUpperCase();
					edCustomerName.setText(s);
				}
				edCustomerName.setSelection(edCustomerName.getText().length());

			}
		});

		edIdCard.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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

			}
		});

		edSales.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		edID.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (Constant.TYPE != 1) {
					int length = s.toString().length();
					if (length >= 7) {
						edID.setError("Please check ID.");

					} else {

						edID.setError(getString(R.string.error_id));
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// if(edID.getText().toString().length() ==7){
				// edID.setError("Please check ID.");
				// }
			}
		});

		// Log.e("onCreateView","Type: " + Constant.TYPE );
		return myFragmentView;
	}

	public void refreshView() {
		// do whatever you want
		Log.d("Create customer", "Create customer");

	}

	@Override
	public void update(String handing) {

	}

	private void createFolderUser(String name) {
		// create folder of application
		if (!operation.createDirIfNotExists(name, 0)) {
			Toast.makeText(getActivity(),
					getString(R.string.create_folder_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void checkType(int type) {
		// init Error for editText
		edCustomerName.setError(getString(R.string.error_customer_name));
		edIdCard.setError(getString(R.string.error_customer_id));

		switch (type) {
		case 1:
			llID.setVisibility(View.GONE);
			txtReview.setText(getString(R.string.exNew));
			edID.setError(null);
			break;
		case 2:
		case 3:
			llID.setVisibility(View.VISIBLE);
			edCustomerName.setEnabled(false);
			edIdCard.setEnabled(false);
			txtReview.setText(getString(R.string.exSupplenment));
			edID.setError(getString(R.string.error_id));
			break;
		}
	}

	private String getNameCustomer() {
		String name = edCustomerName.getText().toString() + "_"
				+ edIdCard.getText().toString() + "_"
				+ edSales.getText().toString();
		Constant.NAME_CUSTOMER_ONLY = edCustomerName.getText().toString();
		Constant.ID_CUSTOMER = edIdCard.getText().toString();

		switch (Constant.TYPE) {
		case 1:
			return name;

		case 2:
		case 3:
			Constant.IDF1 = edID.getText().toString();
			return name + "_" + edID.getText().toString();

		}
		return null;
	}

	private void checkID() {
		// final String userName = edUserName.getText().toString();
		// final String passWord = edPassword.getText().toString();

		dialog = new Dialog(getActivity(), R.style.Theme_D1NoTitleDim);
		dialog.setContentView(R.layout.dialog_loading_animation);

		// dialog.setCanceledOnTouchOutside(false);
		// init TextViewLoading and ImageLoading
		txtLoading = (TextView) dialog.findViewById(R.id.textViewLoading);
		txtLoading.setText("Checking ID...");
		imageLoading = (ImageView) dialog.findViewById(R.id.imageViewLoading);
		imageLoading.setBackgroundResource(R.drawable.animation_loading);

		// using Animation for ImageLoading
		animation = (AnimationDrawable) imageLoading.getBackground();

		String url = Config.IDF1_URL + "?idf1=" + edID.getText().toString()
				+ "&CCcode="
				+ sharedPreferences.getString(Constant.USER_NAME, null);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();

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

						edCustomerName.setText(obj.getString("customer_name"));
						edIdCard.setText(obj.getString("customer_id"));
						Toast.makeText(getActivity(), "ID valid.",
								Toast.LENGTH_LONG).show();
						edID.setError(null);

					}
					// Else display error message
					else {
						Toast.makeText(getActivity(),
								obj.getString("ID invalid"), Toast.LENGTH_LONG)
								.show();
						dialog.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getActivity(), "ID invalid.",
							Toast.LENGTH_LONG).show();
					dialog.dismiss();
					e.printStackTrace();

				}

			}

			// When the response returned by REST has Http response code
			// other than '200'
			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {
				dialog.dismiss();
				if (dialog.isShowing())
					dialog.dismiss();
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getActivity(),
							"Error 404: Requested resource not found",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getActivity(),
							"Error 500: Something went wrong at server end",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							getActivity(),
							"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
