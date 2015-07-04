package digi.mobile.activity;

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
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.lowagie.text.pdf.ArabicLigaturizer;

import digi.mobile.adapter.ZipListAdapter;
import digi.mobile.util.Config;
import digi.mobile.util.Constant;

public class HistoryActivity extends Activity {

	Button btnDate;
	// Dialog Loading User
	Dialog dialog;

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
	LinearLayout llList;
	private int mYear, mMonth, mDay, mHour, mMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		llList = (LinearLayout) findViewById(R.id.llList);
		btnDate = (Button) findViewById(R.id.btnDate);
		radioGroup = (RadioGroup) findViewById(R.id.orientation);
		lvFile = (ListView) findViewById(R.id.lvFile);
		edDate = (EditText) findViewById(R.id.edDate);
		// set Sales channel
		sharedPreferences = getSharedPreferences(
				Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

		btnDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openDialog();
			}
		});
	}

	private void getHistory(String ccCode, String date, String type) {
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
						setListView(list);

					} else {
						// Toast.makeText(HistoryActivity.this,
						// obj.getString("ID invalid"), Toast.LENGTH_LONG)
						// .show();
						dialog.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(HistoryActivity.this, "ID invalid.",
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

	private void setListView(List<String> list) {

//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, list);
		ZipListAdapter adapter = new ZipListAdapter(this, list);
		lvFile.setAdapter(adapter);
		llList.setVisibility(View.VISIBLE);
	}

	private void openDialog() {
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
						if (radioGroup.getCheckedRadioButtonId() == 0) {
							type = "HOSOMOI";
						} else {
							type = "HOSOBOSUNG";
						}

						edDate.setText(date);
						getHistory("DG230002", date, type);
					}
				}, mYear, mMonth, mDay);

		dpd.show();
	}

}
