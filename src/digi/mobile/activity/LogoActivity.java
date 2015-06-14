package digi.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LogoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					sleep(1000);
					Intent intent = new Intent(LogoActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}

		};
		t.start();
	}
}
