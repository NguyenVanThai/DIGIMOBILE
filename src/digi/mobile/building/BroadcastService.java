package digi.mobile.building;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.activity.R;
import digi.mobile.util.Constant;

public class BroadcastService extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;
	ConnectionDetector cnnDec;

	@Override
	public void onCreate() {
		super.onCreate();
		cnnDec = new ConnectionDetector(getApplicationContext());

		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handler.removeCallbacks(getUpdateInfo);
		handler.postDelayed(getUpdateInfo, 1000000); // 1 second
	}

	private Runnable getUpdateInfo = new Runnable() {
		public void run() {
			// DisplayLoggingInfo();
			boolean isInternetPresent = cnnDec.isConnectingToInternet();
			if (isInternetPresent) {
				String user = null;
				SharedPreferences sharedpreferences = getSharedPreferences(
						Constant.DIGI_LOGIN_PREFERENCES, Context.MODE_PRIVATE);

				if (sharedpreferences.contains(Constant.FLAG_KEY)) {
					user = sharedpreferences
							.getString(Constant.USER_NAME, null);
					if (user != null) {
						getInfo(user);
					}
				}

			} else {
				// Toast.makeText(getApplicationContext(),
				// "No internet connection!", Toast.LENGTH_LONG).show();
			}

			handler.postDelayed(this, 60000); // 10 seconds
		}
	};

	// private void DisplayLoggingInfo() {
	// Log.d(TAG, "entered DisplayLoggingInfo");
	//
	// intent.putExtra("time", new Date().toLocaleString());
	// intent.putExtra("counter", String.valueOf(++counter));
	//
	// sendBroadcast(intent);
	// }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(getUpdateInfo);
		super.onDestroy();
	}

	private void getInfo(String user) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://www.digi-texx.vn/vpbank/getlack.php?username="
				+ user, new AsyncHttpResponseHandler() {
			// When the response returned by REST has Http response code
			// '200'
			@Override
			public void onSuccess(String response) {
				try {
					// JSON Object
					JSONObject obj = new JSONObject(response);
					// When the JSON response has status boolean value
					// assigned with true
					if (obj.getString("status").equals("1")) {
						//
						// Toast.makeText(getApplicationContext(),
						// response.toString(), Toast.LENGTH_LONG).show();

						final NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

						Notification note = new Notification(R.drawable.logo,
								"DIGI-TEXX Message!", System
										.currentTimeMillis());

						// This pending intent will open after
						// notification click
						PendingIntent i = PendingIntent.getActivity(
								getBaseContext(), 0, new Intent(
										getBaseContext(), MyLack.class), 0);

						note.setLatestEventInfo(getBaseContext(),
								"Message from DIGI-TEXX", "Supplementary info",
								i);

						mgr.notify(1337, note);

					}
					// Else display error message
					else {
						Toast.makeText(getApplicationContext(),
								obj.getString("error_msg"), Toast.LENGTH_LONG)
								.show();
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
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(),
							"Requested resource not found", Toast.LENGTH_LONG)
							.show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(),
							"Something went wrong at server end",
							Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(
							getApplicationContext(),
							"Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
