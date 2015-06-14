package digi.mobile.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.edmodo.cropper.CropImageView;

import digi.mobile.util.Constant;

public class CropActivity extends Activity {

	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private static final int ROTATE_NINETY_DEGREES = 90;
	private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
	private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
	private static final int ON_TOUCH = 1;

	// Instance variables
	private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
	private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	Bitmap croppedImage;

	private CropImageView cropImageView;

	// Saves the state upon rotating the screen/restarting the activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		cropImageView = (CropImageView) findViewById(R.id.CropImageView);

		cropImageView.setImageBitmap(Constant.bitmap);

	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
		bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
	}

	// Restores the state upon rotating the screen/restarting the activity
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
		mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.actionbar_crop_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch (id) {
		case R.id.itemSave:
			croppedImage = cropImageView.getCroppedImage();
			Constant.updateBitmap(croppedImage);
			Constant.TAKE_PHOTO = false;
			finish();
			break;
		case R.id.itemRotateLeft:
			cropImageView.rotateImage(-90);
			break;
		case R.id.itemRotateRight:
			cropImageView.rotateImage(90);
			break;
		case R.id.itemHome:
//			Intent intentHome = new Intent(CropActivity.this,
//					DigiMobiActivity.class);
//			startActivity(intentHome);
//			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
