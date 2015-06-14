package digi.mobile.building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import digi.mobile.activity.R;

public class MyLack extends Activity {

	TextView tv_myinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_uload);

		tv_myinfo = (TextView) findViewById(R.id.tv_myinfo);
		getInfo();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.my_uload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	private void getInfo() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(
				"http://www.digi-texx.vn/vpbank/getlack.php?username=tccuong",
				new AsyncHttpResponseHandler() {
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
								JSONArray jsonMainNode = obj
										.optJSONArray("info");
								int lengthJsonArr = jsonMainNode.length();
								String OutputData = "";
								for (int i = 0; i < lengthJsonArr; i++) {
									/****** Get Object for each JSON node. ***********/
									JSONObject jsonChildNode = jsonMainNode
											.getJSONObject(i);

									/******* Fetch node values **********/
									String lack_image = jsonChildNode
											.optString("lack_image").toString();
									String app_name = jsonChildNode.optString(
											"app_name").toString();
									String comment = jsonChildNode.optString(
											"comment").toString();

									OutputData += "Image : " + lack_image
											+ " \n " + "AppName : " + app_name
											+ " \n " + "Comment : " + comment
											+ " \n "
											+ "-------------------------\n";

									// Log.i("JSON parse", song_name);
								}
								tv_myinfo.setText(OutputData);
							}
							// Else display error message
							else {
								Toast.makeText(getApplicationContext(),
										obj.getString("error_msg"),
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
						// When Http response code is '404'
						if (statusCode == 404) {
							Toast.makeText(getApplicationContext(),
									"Requested resource not found",
									Toast.LENGTH_LONG).show();
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
