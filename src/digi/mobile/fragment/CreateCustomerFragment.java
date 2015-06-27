package digi.mobile.fragment;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.R;
import digi.mobile.building.IEventListener;
import digi.mobile.building.UpdateableFragment;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class CreateCustomerFragment extends Fragment implements
		UpdateableFragment {

	RelativeLayout relativeLayoutHome, relativeLayoutBefore;
	EditText edCustomerName, edIdCard, edSales, edID;
	TextView txtReview;
	Operation operation;
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// setHasOptionsMenu(true);
	// // get the action bar
	// ActionBar actionBar = getActivity().getActionBar();
	//
	// // // Enabling Back navigation on Action Bar icon
	// // actionBar.setDisplayHomeAsUpEnabled(true);
	// }
	//
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// // TODO Auto-generated method stub
	// super.onCreateContextMenu(menu, v, menuInfo);
	//
	// }
	private IEventListener listener;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// checkType();
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

		if (Constant.TYPE == 1) {
			txtReview.setText(getString(R.string.exNew));
		} else {
			txtReview.setText(getString(R.string.exSupplenment));
		}

		// set Sales channel
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(Constant.DIGI_LOGIN_PREFERENCES,
						Context.MODE_PRIVATE);
		String channel = sharedPreferences.getString(Constant.CHANNEL, null)
				.toString();
		edSales.setText(channel);

		// checkType();
		checkType();

		// init Error for editText
		edCustomerName.setError(getString(R.string.error_customer_name));
		edIdCard.setError(getString(R.string.error_customer_id));
		// edSales.setError(getString(R.string.error_channel));
		if (Constant.TYPE != 1) {
			edID.setError(getString(R.string.error_id));
		} else {
			edID.setError(null);
		}

		relativeLayoutBefore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// listener.sendDataToActivity("Hello World!");

				if (edCustomerName.getError() != null
						|| edIdCard.getError() != null
						|| edSales.getError() != null
						|| edID.getError() != null) {
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
				} else {
					// resultData(txtReview.getText().toString());

					// Constant.NAME_CUSTOMER = txtReview.getText().toString();
					Constant.NAME_CUSTOMER = getNameCustomer();
					String pathNameCustomer = Constant.APP_FOLDER
							+ File.separator + Constant.NAME_USER
							+ File.separator + Constant.NAME_CUSTOMER;
					createFolderUser(pathNameCustomer);
					listener.sendDataToActivity(Constant.Step_2);
				}
			}
		});

		relativeLayoutHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
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

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

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

	private void checkType() {
		switch (Constant.TYPE) {
		case 1:
			edID.setVisibility(View.GONE);
			break;
		case 2:
		case 3:
			edID.setVisibility(View.VISIBLE);
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

}
