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

		// set Sales channel
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(Constant.DIGI_LOGIN_PREFERENCES,
						Context.MODE_PRIVATE);
		String channel = sharedPreferences.getString(Constant.CHANNEL, null)
				.toString();
		edSales.setText(channel);

		// checkType();
		checkType(Constant.TYPE);

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

					switch (Constant.TYPE) {
					case 1:
					case 2:
						listener.sendDataToActivity(Constant.Step_1);
						break;
					case 3:
						listener.sendDataToActivity(Constant.Step_2);
						break;
					}

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
					if (length == 7) {
						edID.setError(null);
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
			edID.setVisibility(View.GONE);
			txtReview.setText(getString(R.string.exNew));
			edID.setError(null);
			break;
		case 2:
		case 3:
			edID.setVisibility(View.VISIBLE);
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

}
