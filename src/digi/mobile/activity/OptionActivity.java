package digi.mobile.activity;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import digi.mobile.util.Constant;

public class OptionActivity extends Activity implements OnTouchListener {

	ImageView image;
	private Matrix matrix = new Matrix();
	private Matrix saveMatrix = new Matrix();
	private Matrix matrixDefault = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();

	private float oldDist = 1f;
	private float angleStart = 0f;
	private float angleFinish = 0f;

	Bitmap bitmap;

	private String pathImage;
	File file;

	// ImageLoaderConfiguration config;
	// DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		image = (ImageView) findViewById(R.id.image);
		image.setOnTouchListener(this);
		// get the action bar
		ActionBar actionBar = getActionBar();
		//
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		// config = new ImageLoaderConfiguration.Builder(this).build();
		// options = new DisplayImageOptions.Builder().build();

		// image.setImageBitmap(bitmap);
		matrix.setScale(0.5f, 0.5f);
	
		showImage();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constant.REQUEST_CODE_SAVE_ACTIVITY:
				// Intent intent = new Intent(OptionActivity.this,
				// NewAppDetailActivity.class);
				// startActivity(intent);
				setResult(RESULT_OK);
				finish();
				break;
			case Constant.REQUEST_CODE_DUCOMENT_CROP_ACTIVITY:
				bitmap = Constant.bitmap;
				image.setImageBitmap(bitmap);
				// setResult(RESULT_OK);
				break;
			}
		}
	}

	private void showImage() {
		// TODO Auto-generated method stub
		// Display display = getWindowManager().getDefaultDisplay();
		// @SuppressWarnings("deprecation")
		// int width = display.getWidth()/2;
		// int height = display.getHeight()/2;
		//
		resetImage();
		bitmap = Constant.bitmap;
		Matrix matrixShow = new Matrix();

		// Intent intent = getIntent();
		// String path = intent.getStringExtra(Constant.PATH_IMAGE);
		// Log.d("path", path);
		// bitmap = Constant.decodeSampledBitmapFromUri(path, width,
		// height);

		if (Constant.TAKE_PHOTO) {
			if (bitmap.getWidth() > bitmap.getHeight()) {

				matrixShow.postRotate(90);
//				matrixDefault.set(matrixShow);

			}
			// Reset INPUT_BITMAP is Camera

		} else {
			Constant.TAKE_PHOTO = false;
		}

		// Intent intent = getIntent();
		// String path = intent.getStringExtra(Constant.PATH_IMAGE);
		// bitmap = Constant.getBitmap(path);
		matrixShow.setScale(0.5f, 0.5f);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrixShow, true);

		image.setImageBitmap(bitmap);
	}

	private void resetImage() {
		// TODO Auto-generated method stub
		matrix.set(matrixDefault);
		image.setImageMatrix(matrix);
	}

	private void saveImage() {
		// TODO Auto-generated method stub
		// matrix.setScale(0.5f, 0.5f);
		Bitmap bitmapMatrix = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Constant.bitmap = bitmapMatrix;
		Intent intent = new Intent(OptionActivity.this, SaveActivity.class);
		startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_ACTIVITY);
		// finish();
	}

	private void cropImage() {
		// TODO Auto-generated method stub

		Bitmap bitmapMatrix = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Constant.updateBitmap(bitmapMatrix);
		Intent intent = new Intent(OptionActivity.this, CropActivity.class);
		startActivityForResult(intent,
				Constant.REQUEST_CODE_DUCOMENT_CROP_ACTIVITY);
	}

	private void rotateRight() {
		// TODO Auto-generated method stub

		matrix.postRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		image.setImageMatrix(matrix);
	}

	private void rotateLeft() {
		// TODO Auto-generated method stub

		matrix.postRotate(-90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		image.setImageMatrix(matrix);
	}

	/*
	 * (using split action bar)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.actionbar_option_activity, menu);
		return true;
	}

	/*
	 * (handling clicks on action items )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch (id) {
		// case android.R.id.home:
		// Intent intent = new Intent(OptionActivity.this,
		// NewAppDetailActivity.class);
		// startActivity(intent);
		// finish();
		// break;
		// case R.id.itemHome:
		// Intent intentHome = new Intent(OptionActivity.this,
		// DigiMobiActivity.class);
		// startActivity(intentHome);
		// finish();
		// break;
		case R.id.itemRefresh:
			resetImage();
			break;
		case R.id.itemSave:
			saveImage();
			break;
		case R.id.itemRotateLeft:
			rotateLeft();
			break;
		case R.id.itemRotateRight:
			rotateRight();
			break;
		case R.id.itemCrop:
			cropImage();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		ImageView image = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			saveMatrix.set(matrix);

			// matrixDefault.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;

			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				saveMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				angleStart = rotation(event);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(saveMatrix);
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;
				matrix.postTranslate(dx, dy);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(saveMatrix);
					float scale = (newDist / oldDist);
					// matrix.postScale(scale, scale, mid.x, mid.y);

					angleFinish = rotation(event);
					float angleChange = angleStart - angleFinish;
					matrix.postRotate(angleChange, mid.x, mid.y);
				}

			}
			break;
		}

		image.setImageMatrix(matrix);
		// logImageViewMatrixInfos(matrix, image);
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);

	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set((x / 2), (y / 2));
	}

	private float rotation(MotionEvent event) {
		double deltaX = event.getX(0) - event.getX(1);
		double deltaY = event.getY(0) - event.getY(1);
		double radian = Math.atan2(deltaX, deltaY);
		return (float) Math.toDegrees(radian);
	}

	private void deleteFile() {
		// TODO Auto-generated method stub
		file = new File(pathImage);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// deleteFile();
	}

}