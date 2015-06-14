package digi.mobile.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import digi.mobile.util.Constant;

public class SaveActivity extends Activity {

	Button btnOk, btnCancel;
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
		
		setFinishOnTouchOutside(false);
		btnCancel = (Button) findViewById(R.id.buttonCancel);
		btnOk = (Button) findViewById(R.id.buttonOk);
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setImageBitmap(Constant.bitmap);
		
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final FileOutputStream out;

				try {
					// File file = new File(Constant.fileFinal.getParent(),
					// "temp.bin");
					out = new FileOutputStream(Constant.fileFinal);

					Constant.bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							out);

					out.flush();
					out.close();

					// bitmap.recycle();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				setResult(RESULT_OK);
				finish();
			}
		});
	}
}
