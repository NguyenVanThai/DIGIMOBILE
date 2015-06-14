package digi.mobile.fragment;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
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

	RelativeLayout relativeLayout;
	EditText edCustomerName, edIdCard, edSales, edCode;
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
				R.layout.activity_create_fragment, container, false);

		// Log.d("Fragment 1", "onCreateView");
		// Toast.makeText(getActivity(), "onCreateView",
		// Toast.LENGTH_LONG).show();

		relativeLayout = (RelativeLayout) myFragmentView
				.findViewById(R.id.relativeLayout);
		// init editText
		edCustomerName = (EditText) myFragmentView
				.findViewById(R.id.editTextUserName);
		edIdCard = (EditText) myFragmentView.findViewById(R.id.EditText01);
		edSales = (EditText) myFragmentView.findViewById(R.id.EditText02);
		edCode = (EditText) myFragmentView.findViewById(R.id.edCode);
		txtReview = (TextView) myFragmentView.findViewById(R.id.textView2);

		// checkType();
		checkType();

		// init Error for editText
		edCustomerName.setError(getString(R.string.error_customer_name));
		edIdCard.setError(getString(R.string.error_customer_id));
		// edSales.setError(getString(R.string.error_channel));

		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// listener.sendDataToActivity("Hello World!");

				if (edCustomerName.getError() != null
						|| edIdCard.getError() != null
						|| edSales.getError() != null) {
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

		edCustomerName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

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
				if (s.toString().length() <= 0) {
					edCustomerName
							.setError(getString(R.string.error_customer_name));
				} else {
					edCustomerName.setError(null);
				}
				String temp = s.toString();

				if (edIdCard.getText().toString().length() > 0) {

					temp = temp + "_" + edIdCard.getText().toString();
				}

				if (edSales.getText().toString().length() > 0) {

					temp = temp + "_" + edSales.getText().toString();
				}

				txtReview.setText(temp);

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

				String temp = s.toString();

				if (edCustomerName.getText().toString().length() > 0) {

					temp = edCustomerName.getText().toString() + "_" + temp;
				}

				if (edSales.getText().toString().length() > 0) {

					temp = temp + "_" + edSales.getText().toString();
				}

				txtReview.setText(temp);
			}
		});

		edSales.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				int length = s.toString().length();
				if (length == 3) {
					edSales.setError(null);

				} else {
					edSales.setError(getString(R.string.error_channel));
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
				String temp = s.toString();

				if (edCustomerName.getText().toString().length() > 0) {

					temp = edCustomerName.getText().toString() + "_" + temp;
				}

				if (edIdCard.getText().toString().length() > 0) {

					temp = edCustomerName.getText().toString() + "_"
							+ edIdCard.getText().toString() + "_"
							+ s.toString();
				}

				txtReview.setText(temp);

				// if (edCustomerName.getText().toString().length() > 0) {
				//
				// temp = edCustomerName.getText().toString() + "_" + temp;
				// }
				//
				// if (edIdCard.getText().toString().length() > 0) {
				//
				// temp = edIdCard.getText().toString() + "_" + temp;
				// }

				txtReview.setText(temp);
			}
		});

		// btn = (Button) myFragmentView.findViewById(R.id.button1);
		//
		// btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// listener.sendDataToActivity("Hello World!");
		// }
		// });

		return myFragmentView;
	}

	public void refreshView() {
		// do whatever you want
		Log.d("Create customer", "Create customer");

	}

	@Override
	public void update(String handing) {
		// TODO Auto-generated method stub

	}

	private void createFolderUser(String name) {
		// create folder of application
		if (!operation.createDirIfNotExists(name, 0)) {
			Toast.makeText(getActivity(),
					getString(R.string.create_folder_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// // TODO Auto-generated method stub
	// // MenuInflater menuInflater = getActivity().getMenuInflater();
	//
	// inflater.inflate(R.menu.action_newappdetail_activity, menu);
	// getActivity().getWindow().setUiOptions(
	// ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
	//
	// super.onCreateOptionsMenu(menu, inflater);
	// }

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// // handling clicks on action items
	// MenuInflater menuInflater = getMenuInflater();
	// menuInflater.inflate(R.menu.action_newappdetail_activity, menu);
	//
	// return super.onCreateOptionsMenu(menu);
	// }
	private void checkType() {
		switch (Constant.TYPE) {
		case 1:
			edCode.setVisibility(View.GONE);
			break;
		case 2:
		case 3:
			edCode.setVisibility(View.VISIBLE);
			break;
		}
	}

	private String getNameCustomer() {
		switch (Constant.TYPE) {
		case 1:
			return txtReview.getText().toString();

		case 2:
		case 3:
			return txtReview.getText().toString() + "_"
					+ edCode.getText().toString();

		}
		return null;
	}
}
