package digi.mobile.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.adapter.CustomerListAdapter;
import digi.mobile.adapter.ZipListAdapter;
import digi.mobile.building.DatabaseHelper;
import digi.mobile.building.History;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;

public class HistoryActivity extends Activity implements OnClickListener {

	Button btnDate;
	// Dialog Loading User
	Dialog dialog;
	RadioButton raNew, raQDE;
	// TextView and ImageView display % Loading
	TextView txtLoading;
	ImageView imageLoading;

	// Animation for ImageView loading
	AnimationDrawable animation;
	SharedPreferences sharedPreferences;
	CalendarView calendarView;
	RadioGroup radioGroup;
	ListView lvFile;
	EditText edDate;
	LinearLayout llList, llType;

	RelativeLayout relUpload, relCustomer;
	private int mYear, mMonth, mDay;
	private String ccCode;
	DatabaseHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		llList = (LinearLayout) findViewById(R.id.llList);
		llType = (LinearLayout) findViewById(R.id.llType);
		btnDate = (Button) findViewById(R.id.btnDate);
		radioGroup = (RadioGroup) findViewById(R.id.orientation);
		lvFile = (ListView) findViewById(R.id.lvFile);
		edDate = (EditText) findViewById(R.id.edDate);
		relUpload = (RelativeLayout) findViewById(R.id.relUpload);
		relCustomer = (RelativeLayout) findViewById(R.id.relCustomer);
		raNew = (RadioButton) findViewById(R.id.horizontal);
		raQDE = (RadioButton) findViewById(R.id.vertical);
		// set Sales channel
		sharedPreferences = getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

		btnDate.setOnClickListener(this);
		relUpload.setOnClickListener(this);
		relCustomer.setOnClickListener(this);

		relUpload.setSelected(true);

		ccCode = sharedPreferences.getString(Constant.USER_NAME, null);
		setDefault("UPLOAD");

		databaseHelper = new DatabaseHelper(HistoryActivity.this);
		try {
			databaseHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				edDate.setText("");
				lvFile.setAdapter(null);
				llList.setVisibility(View.GONE);
			}
		});
	}

	private void getUpload(String ccCode, String date, String type) {
		// final String userName = edUserName.getText().toString();
		// final String passWord = edPassword.getText().toString();

		dialog = new Dialog(this, R.style.Theme_D1NoTitleDim);
		dialog.setContentView(R.layout.dialog_loading_animation);

		// dialog.setCanceledOnTouchOutside(false);
		// init TextViewLoading and ImageLoading
		txtLoading = (TextView) dialog.findViewById(R.id.textViewLoading);
		txtLoading.setText("Checking ID...");
		imageLoading = (ImageView) dialog.findViewById(R.id.imageViewLoading);

		imageLoading.setBackgroundResource(R.drawable.animation_loading);

		// using Animation for ImageLoading
		animation = (AnimationDrawable) imageLoading.getBackground();

		String url = Config.HISTORY_URL + "?ccCode=" + ccCode + "&date=" + date
				+ "&type=" + type;
		// String url =
		// "http://vpb.digi-texx.vn:1080/AndroidUpload/androidUploadPerDay.php?ccCode=DG230002&date=2015-06-17&type=HOSOMOI";
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

					JSONArray obj = new JSONArray(response);

					if (obj.length() > 0) {

						List<String> list = new ArrayList<>();
						for (int i = 0; i < obj.length(); i++) {
							list.add(obj.getString(i));

						}
						Log.e("typeHistory", list.size() + "");
						setListView(list);

					} else {
						lvFile.setAdapter(null);
						llList.setVisibility(View.GONE);
						Toast.makeText(HistoryActivity.this, "No data found.",
								Toast.LENGTH_LONG).show();
						dialog.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(HistoryActivity.this, "History error.",
							Toast.LENGTH_LONG).show();
					dialog.dismiss();
					e.printStackTrace();
					;
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
					Toast.makeText(HistoryActivity.this,
							"Error 404: Requested resource not found",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(HistoryActivity.this,
							"Error 500: Something went wrong at server end",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							HistoryActivity.this,
							"Error: Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void getCustomer(String date, String type) {
		if (databaseHelper.getList(date, type).size() > 0) {
			CustomerListAdapter adapter = new CustomerListAdapter(this,
					databaseHelper.getList(date, type));
			lvFile.setAdapter(adapter);
			llList.setVisibility(View.VISIBLE);

		} else {
			lvFile.setAdapter(null);
			llList.setVisibility(View.GONE);
			Toast.makeText(HistoryActivity.this, "No data found.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void setListView(List<String> list) {

		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, list);
		if (list.size() > 0) {
			ZipListAdapter adapter = new ZipListAdapter(this, list);
			lvFile.setAdapter(adapter);
			llList.setVisibility(View.VISIBLE);

		} else {
			lvFile.setAdapter(null);
			llList.setVisibility(View.GONE);
			Toast.makeText(HistoryActivity.this, "No data found.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void getDate(final String typeHistory) {
		// Process to get Current Date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// Launch Date Picker Dialog
		DatePickerDialog dpd = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						monthOfYear++;
						String month, day, date, type;
						if (monthOfYear < 10) {
							month = "0" + monthOfYear;
						} else {
							month = "" + monthOfYear;
						}
						if (dayOfMonth < 10) {
							day = "0" + dayOfMonth;
						} else {
							day = "" + dayOfMonth;
						}

						date = year + "-" + month + "-" + day;
						if (raNew.isChecked()) {
							type = "HOSOMOI";
						} else {
							type = "HOSOBOSUNG";
						}

						edDate.setText(date);

						switch (typeHistory) {

						case "UPLOAD":

							getUpload(ccCode, date, type);

							break;
						case "QDE":
							getCustomer(date, type);
							break;
						default:
							break;
						}

					}
				}, mYear, mMonth, mDay);

		dpd.show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnDate:

			if (relUpload.isSelected()) {

				getDate("UPLOAD");
			} else {
				getDate("QDE");
			}
			// getDate();
			// databaseHelper.insertStory("2015-07-05", "Nguyen Van C",
			// "331698129",
			// "HOSOBOSUNG");

			// List<History> listHistory = databaseHelper.getList("2015-07-05",
			// "HOSOMOI");
			// List<String> list = new ArrayList<>();
			// for (int i = 0; i < listHistory.size(); i++) {
			// list.add(listHistory.get(i).getDate() + "_"
			// + listHistory.get(i).getName() + "_"
			// + listHistory.get(i).getId() + "_"
			// + listHistory.get(i).getType());
			// }
			// setListView(list);

			break;
		case R.id.relUpload:

			setColor(0, true);
			// llType.setVisibility(View.VISIBLE);
			setDefault("UPLOAD");
			break;
		case R.id.relCustomer:
			// llType.setVisibility(View.GONE);
			setColor(1, false);
			setDefault("CUSTOMER");

			break;
		}
	}

	private void setDefault(String typeHistory) {
		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(new Date());
		edDate.setText(date);
		radioGroup.check(R.id.horizontal);
		if (typeHistory.equals("UPLOAD")) {
			getUpload(ccCode, date, "HOSOMOI");
		} else {
			getCustomer(date, "HOSOMOI");
		}
	}

	public void setColor(int step, boolean b) {
		relUpload.setBackgroundColor(getResources().getColor(R.color.black_1));
		relCustomer
				.setBackgroundColor(getResources().getColor(R.color.black_1));
		relUpload.setSelected(b);
		relCustomer.setSelected(!b);
		switch (step) {
		case 0:
			relUpload.setBackgroundColor(getResources()
					.getColor(R.color.blue_3));

			break;
		case 1:
			relCustomer.setBackgroundColor(getResources().getColor(
					R.color.blue_3));
			break;

		default:
			break;
		}
	}
}
