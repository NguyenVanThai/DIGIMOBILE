package digi.mobile.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import digi.mobile.util.Constant;

public class OptionActivity extends Activity implements OnTouchListener {

	ImageView image;
	ZoomControls zoomControl;
	RelativeLayout rel;
	private Matrix matrix = new Matrix();
	private Matrix saveMatrix = new Matrix();
	private Matrix matrixDefault = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private PointF center = new PointF();
	private PointF midFinal = new PointF();
	private float oldDist = 1f;
	private float angleStart = 0f;
	private float angleFinish = 0f;
	private int zoom = 0;
	// private float coordinatesX;
	// private float coordinatesY;
	int width, height, relWidth, relHeight;
	Bitmap bitmap = null;

	private String pathImage;
	File file;
	float scale;

	// ImageLoaderConfiguration config;
	// DisplayImageOptions options;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		width = image.getWidth();
		height = image.getHeight();
		relWidth = rel.getWidth();
		relHeight = rel.getHeight(); 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		image = (ImageView) findViewById(R.id.image);
		zoomControl = (ZoomControls) findViewById(R.id.zoomControls1);
		rel = (RelativeLayout) findViewById(R.id.rel);
		
		ViewTreeObserver vto = image.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				height = image.getMeasuredHeight();
				width = image.getMeasuredWidth();
//				 Log.e("Error", width + "_" + height);
				return true;
			}
		});

		image.setOnTouchListener(this);

		// get the action bar
		ActionBar actionBar = getActionBar();
		//
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		// config = new ImageLoaderConfiguration.Builder(this).build();
		// options = new DisplayImageOptions.Builder().build();

		// image.setImageBitmap(bitmap);
//		matrix.setScale(0.5f, 0.5f);
		

		showImage();
//		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(bitmap.getWidth(),bitmap.getHeight());
//		parms.setMargins((relWidth - width)/2, (relHeight - height)/2, (relWidth - width)/2, (relHeight - height)/2);
//		image.setLayoutParams(parms);
//		
		zoomControl.setOnZoomInClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (zoom <= 8) {
					matrix.postScale(1.1f, 1.1f, center.x, center.y);
					image.setImageMatrix(matrix);
					++zoom;
				} else {
					zoom = 8;
				}
			}
		});

		zoomControl.setOnZoomOutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (zoom >= -10) {
					matrix.postScale(0.9f, 0.9f, center.x, center.y);
					image.setImageMatrix(matrix);
					--zoom;
				} else {
					zoom = -10;
				}
			}
		});
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
				zoom = 0;
				setResult(RESULT_OK);
				finish();
				break;
			case Constant.REQUEST_CODE_DUCOMENT_CROP_ACTIVITY:
				// bitmap = Constant.bitmap;
				// image.setImageBitmap(bitmap);
				// setResult(RESULT_OK);
				zoom = 0;
				showImage();
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
		
		// Matrix matrixShow = new Matrix();

		// Intent intent = getIntent();
		// String path = intent.getStringExtra(Constant.PATH_IMAGE);
		// Log.d("path", path);
		// bitmap = Constant.decodeSampledBitmapFromUri(path, width,
		// height);

		// Intent intent = getIntent();
		// String path = intent.getStringExtra(Constant.PATH_IMAGE);
		// bitmap = Constant.getBitmap(path);


		// bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
		// bitmap.getHeight(), matrixShow, true);
		// image.setImageBitmap(bitmap);
		
		
		if (Constant.TAKE_PHOTO) {
			if (bitmap.getWidth() > bitmap.getHeight()) {

				// matrixShow.postRotate(90);
				// matrixDefault.set(matrixShow);
				Matrix matrixShow = new Matrix();

				matrixShow.postRotate(90, bitmap.getWidth() / 2,
						bitmap.getHeight() / 2);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrixShow, true);
				Constant.TAKE_PHOTO = false;
				// image.setImageMatrix(matrix);

			}
			// Reset INPUT_BITMAP is Camera
			//
		} else {
			// Constant.TAKE_PHOTO = false;
			// image.setImageBitmap(bitmap);
		}
		
		center.set(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

		image.setImageBitmap(bitmap);
//	

	}

	private void resetImage() {
		zoom = 0;
		matrix.set(matrixDefault);
		image.setImageMatrix(matrix);
		if (bitmap != null) {
			center.set(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		}
	}

	private void saveImage() {

		// use image from cache
		image.setDrawingCacheEnabled(true);
		image.buildDrawingCache(true); // this might hamper performance use
										// hardware acc if available. see:
										// http://developer.android.com/reference/android/view/View.html#buildDrawingCache(boolean)

		// create the bitmaps
//		Bitmap zoomedBitmap = Bitmap.createScaledBitmap(
//				image.getDrawingCache(true), width,
//				height, true);
		
		Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

		image.setDrawingCacheEnabled(false);
		File myFile = Constant.fileFinal; // have a look at the android api docs
											// for File for proper explanation
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(myFile);
			zoomedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					fileOutputStream);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		showImage();
		setResult(RESULT_OK);
		finish();
		/*
		 * 
		 * // matrix.setScale(0.5f, 0.5f); Bitmap bitmapMatrix =
		 * Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
		 * bitmap.getHeight(), matrix, true); Constant.bitmap = bitmapMatrix;
		 * Intent intent = new Intent(OptionActivity.this, SaveActivity.class);
		 * startActivityForResult(intent, Constant.REQUEST_CODE_SAVE_ACTIVITY);
		 * // finish();
		 */

	}
	
	private void saveImage_dm() {

		final Dialog dialog = new Dialog(OptionActivity.this,
				R.style.Theme_D1NoTitleDim);
		dialog.setContentView(R.layout.dialog_loading_animation);
		dialog.setCanceledOnTouchOutside(false);

		// init TextViewLoading and ImageLoading
		TextView txtLoading = (TextView) dialog
				.findViewById(R.id.textViewLoading);
		txtLoading.setText("Please wait...");
		ImageView imageLoading = (ImageView) dialog
				.findViewById(R.id.imageViewLoading);
		imageLoading.setBackgroundResource(R.drawable.animation_loading);
		//
		// // using Animation for ImageLoading
		final AnimationDrawable animation = (AnimationDrawable) imageLoading
				.getBackground();
		//
		// dialog.show();
		// animation.start();
		//
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if (dialog.isShowing()) {
					dialog.cancel();
				} else {
					dialog.show();
					animation.start();
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				// use image from cache
				image.setDrawingCacheEnabled(true);
				image.buildDrawingCache(true); // this might hamper performance use
												// hardware acc if available. see:
												// http://developer.android.com/reference/android/view/View.html#buildDrawingCache(boolean)

				// create the bitmaps
//				Bitmap zoomedBitmap = Bitmap.createScaledBitmap(
//						image.getDrawingCache(true), width,
//						height, true);
				
				Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);

				image.setDrawingCacheEnabled(false);
				File myFile = Constant.fileFinal; // have a look at the android api docs
													// for File for proper explanation
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(myFile);
					zoomedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							fileOutputStream);
					fileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;				

			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				setResult(RESULT_OK);				
				animation.stop();
				dialog.cancel();
				finish();
//
//				Toast.makeText(OptionActivity.this,
//						"Completed save image...",
//						Toast.LENGTH_SHORT).show();

			}

		}.execute();

		// String path = pathCustomer + File.separator + nameCustomer + ".pdf";
		// MyAsynTask myAsynTask = new MyAsynTask(path);
		// myAsynTask.execute();
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

		matrix.postRotate(10, center.x, center.y);
		image.setImageMatrix(matrix);

	}

	private void rotateLeft() {
		// TODO Auto-generated method stub

		matrix.postRotate(-10, center.x, center.y);
		image.setImageMatrix(matrix);
		// matrix.postScale(1.5f, 1.5f, center.x, center.y);
		// image.setImageMatrix(matrix);
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
			// Log.e("midPoint", mid.x + " _ " + mid.y);
			break;
		case MotionEvent.ACTION_UP:
			float dxCenter;
			float dyCenter;
			if (mode == DRAG) {
				dxCenter = event.getX() - start.x;
				dyCenter = event.getY() - start.y;
				center.set(center.x + dxCenter, center.y + dyCenter);
				// } else {

				// float dxCenter1;
				// float dyCenter1;
				//
				// dxCenter1 = midFinal.x - mid.x;
				// dyCenter1 = midFinal.y - mid.y;
				// center.set(center.x + dxCenter1, center.y + dyCenter1);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;

			break;
		case MotionEvent.ACTION_MOVE:

			if (mode == DRAG) {
				matrix.set(saveMatrix);
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;

				matrix.postTranslate(dx, dy);

			}
			// else if (mode == ZOOM) {
			// midPoint(midFinal, event);
			// Log.e("midPointFinal", midFinal.x + " _ " + midFinal.y);
			//
			// float newDist = spacing(event);
			// if (newDist > 10f) {
			// matrix.set(saveMatrix);
			// scale = (newDist / oldDist);
			//
			// matrix.postScale(scale, scale, mid.x, mid.y);
			//
			// // angleFinish = rotation(event);
			// // float angleChange = angleStart - angleFinish;
			// // matrix.postRotate(angleChange, mid.x, mid.y);
			// }
			//
			// }
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