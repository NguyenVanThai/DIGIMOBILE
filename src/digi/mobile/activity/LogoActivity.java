package digi.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.AsyncTaskCompat;
import android.widget.RelativeLayout;

public class LogoActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 2500;
	RelativeLayout relLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		relLoading = (RelativeLayout) findViewById(R.id.relLoading);
		relLoading.setBackgroundResource(R.drawable.android_screen_loading);
//		Thread t = new Thread() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					sleep(2500);
//					Intent intent = new Intent(LogoActivity.this,
//							MainActivity.class);
//					startActivity(intent);
//					finish();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				super.run();
//			}
//
//		};
//		t.start();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent intent = new Intent(LogoActivity.this,
						MainActivity.class);
				LogoActivity.this.startActivity(intent);
				LogoActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
		

	}
}
