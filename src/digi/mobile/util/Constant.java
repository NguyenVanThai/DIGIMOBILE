package digi.mobile.util;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Constant {

	// constants integer
	public static int TYPE = 0; // 1: New // 2: Supplement
	public static boolean TAKE_PHOTO=true; // true: Camera // false: Gallery

	public static int TYPE_SHOW = 0; // 1: Show Image // 2: Show PDF and Zip file

	public static final int Step_1 = 0;
	public static final int Step_2 = 1;
	public static final int Step_3 = 2;

	// constants String
	public static final String TAG = "DIGI-MOBI";
	public static final String TAG_DIALOG = "dlg_edit_name";
	public static final String APP_FOLDER = "/Digi";
	public static final String CHECK_BITMAP = "check";
	public static final String BITMAP_CROP = "crop";
	public static final String POSITION = "position";
	public static boolean SELECT_ALL = false ;
	public static String NAME_MY_FOLDER = "";
	public static String NAME_USER = "";
	public static String NAME_CUSTOMER = null;
	public static String NAME_CUSTOMER_ONLY = null;
	public static String ID_CUSTOMER = null;
	public static String IDF1 = null;
	public static String NAME_CATEGORY = null;
	public static String NAME_SHORT_DUCOMENT = null;
	public static String PATH_MY_FOLDER_SAVE = "";
	public static String PATH_MY_FOLDER_SAVE_NEW = "";
	public static String PATH_MY_FOLDER_SAVE_SUPPLEMENT = "";
	public static String PATH = "";

	// init String information User
	public static final String DIGI_LOGIN_PREFERENCES = "Digi_Login_Preferences";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String CHANNEL = "channel";
	public static final String FLAG_KEY = "flag";
	public static final String LIST_CUSTOMER = "listCustomer";
	public static final String PATH_MY_FOLDER = "pathMyFolder";

	public static final String NAME_DUCOMENT = "nameDucoment";
	public static final String PATH_IMAGE = "path";

	public static final int REQUEST_CODE_CREATE_CUSTOMER_ACTIVITY = 0x1;
	public static final int REQUEST_CODE_LIST_CUSTOMER_ACTIVITY = 0x2;
	public static final int REQUEST_CODE_DUCOMENT_TYPE_ACTIVITY = 0x3;

	public static final int REQUEST_CODE_SAVE_ACTIVITY = 0x4;
	public static final int REQUEST_CODE_CREATE_FOLDER = 0x5;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x6;
	public static final int REQUEST_CODE_DUCOMENT_CROP_ACTIVITY = 0x7;
	public static final int REQUEST_CODE_OPTION_ACTIVITY = 0x8;
	public static final int REQUEST_CODE_GALLERY = 0x9;
	public static final int REQUEST_CODE_LOGIN_ACTIVITY = 0x10;
	public static final int REQUEST_CODE_UPLOAD_ACTIVITY = 0x11;
	public static final String NAME_CUSTOMER_PUT_EXTRA = "nameCustomer";

	// constans Array
	public static final String ARRAY_CAPTURE_ITEMS[] = { "NewApp",
			"Supplement", "FromFile" };
	public static final String ARRAY_APP_ITEMS[] = { "Don de nghi vay", "CMND",
			"Ho khau", "Hop dong", "Chu ky" };
	public static final String ARRAY_APP_SHORT_ITMES[] = { "DN", "ID", "HK",
			"HD", "CK" };

	// File
	public static File fileFinal;
	// Bitmap
	public static Bitmap bitmap = null;

	public static String getPathRoot(String nameDirectory) {
		String path = Environment.getExternalStorageDirectory() + nameDirectory;
		return path;

		// if (nameDirectory != null) {
		// return file.getPath() + nameDirectory;
		// } else {
		// return file.getPath();
		// }

	}

	public static Bitmap getBitmap(String path) {

		Bitmap b = null;
		File f = new File(path);
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();

			int IMAGE_MAX_SIZE = 512; // maximum dimension limit
			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return b;

	}

	public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {

		Bitmap bm = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public static int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	public static void updateBitmap(Bitmap bitmapInput) {
		bitmap = bitmapInput;
	}
}