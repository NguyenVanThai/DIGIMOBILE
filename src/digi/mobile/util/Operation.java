package digi.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import digi.mobile.building.ImageItem;

public class Operation {

	public boolean createDirIfNotExists(String path, int type) {
		boolean ret = true;
		File file;
		if (type == 0) {
			file = new File(Environment.getExternalStorageDirectory(), path);
		} else {
			file = new File(path);
		}
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
		
//		if (checkStorage()) {
//			if (!file.exists()) {
//
//				if (!file.mkdir()) {
//					// Log.d("ok", "fail");
//					// try {
//					// FileUtils.forceDelete(file);
//					// file.mkdir();
//					// } catch (IOException e) {
//					// // TODO Auto-generated catch block
//					// e.printStackTrace();
//					// }
//					return false;
//				} else {
//					return true;
//				}
//			} else {
//				return true;
//			}
//		} else {
//			return false;
//		}
	}

	public boolean createDirIfNotExists2(String path) {
		// boolean ret = true;
		Log.d("ok", path);
		File file = new File(path);
		if (checkStorage()) {
			if (!file.exists()) {

				if (!file.mkdir()) {
					// Log.d("ok", "fail");
					// try {
					// FileUtils.forceDelete(file);
					// file.mkdir();
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean checkStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	public File createFile(String path) throws IOException {
		File image = null;
		image = new File(path);

		return image;

	}

	// public List<ImageFileObject> getList(File parentDir,
	// String pathToParentDir, String extension) {
	//
	// ArrayList<ImageFileObject> listImObject = new
	// ArrayList<ImageFileObject>();
	// String[] fileNames = parentDir.list();
	//
	// for (String fileName : fileNames) {
	// if (fileName.toLowerCase().endsWith(extension)) {
	// ImageFileObject imOb = new ImageFileObject();
	// imOb.setName(fileName);
	// imOb.setFilepath(pathToParentDir + "/" + fileName);
	// listImObject.add(imOb);
	// } /*
	// * else { File file = new File(parentDir.getPath() + "/" +
	// * fileName); if (file.isDirectory()) { inFiles.addAll(getList(file,
	// * pathToParentDir + fileName + "/")); } }
	// */
	// }
	//
	// return listImObject;
	// }

	public ArrayList<ImageItem> getData(String parentPath,
			String nameShortCategory, boolean selected) {
		List<String> fileNames = listImagebyCategory(parentPath,
				".jpg", "name");
		if (fileNames.size() > 0) {
			ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>(
					fileNames.size());
			// Matrix matrix = new Matrix();
			// matrix.postRotate(90);
			for (int i = 0; i < fileNames.size(); i++) {
				// Bitmap bmp = BitmapFactory.decodeFile(parentPath + "/"
				// + fileNames.get(i));
				// bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				// bmp.getHeight(), matrix, true);
				imageItems.add(new ImageItem(parentPath + "/"
						+ fileNames.get(i), nameShortCategory + "_" + i, selected));
				// bmp.recycle();

			}
			return imageItems;
		}
		return null;
	}

	public List<String> listImagebyCategory(String appPath, String prefix,
			String type) {
		ArrayList<String> listImagePath = new ArrayList<String>();
		File dir = new File(appPath);
		String[] fileNames = dir.list();
		for (String fileName : fileNames) {
			if (fileName.contains(prefix)) {
				if (type.equals("name"))
					listImagePath.add(fileName);
				else if (type.equals("path")) {
					listImagePath.add(appPath + "/" + fileName);
//					Log.d("Path", appPath + "/" + fileName);
				}
			}
		}
		return listImagePath;
	}

	public List<String> listAllPdf(String appPath) {
		ArrayList<String> listPdf = new ArrayList<String>();
		File dir = new File(appPath);
		String[] fileNames = dir.list();
		for (String fileName : fileNames) {
			if (fileName.endsWith(".pdf")) {
				listPdf.add(appPath + "/" + fileName);
			}
		}
		return listPdf;
	}

	public List<String> listFilebyType(String appPath, String prefix,
			String type) {
		ArrayList<String> listImagePath = new ArrayList<String>();
		File dir = new File(appPath);
		String[] fileNames = dir.list();
		for (String fileName : fileNames) {
			if (fileName.toLowerCase().endsWith(type)) {
				if (prefix.equals("path"))
					listImagePath.add(appPath + "/" + fileName);
				else if (prefix.equals("name"))
					listImagePath.add(fileName);
			}
		}
		return listImagePath;
	}

	public ArrayList<String> getAllSubFolder(File parentDir) {
		ArrayList<String> listApp = new ArrayList<String>();
		File[] listFolder = parentDir.listFiles();
		for (File folder : listFolder) {
			if (folder.isDirectory()) {
				listApp.add(folder.getName());
			}
		}
		return listApp;
	}

	/** Check if this device has a camera */
	public boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public boolean checkSDCard() {
		boolean state = false;
		String sd = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(sd)) {
			state = true;
		}

		return state;
	}

	public boolean createPDF(String path, List<String> listImage,
			String category) {
		Boolean b = false;
		Document doc = new Document(PageSize.A4);
		try {
			File dir = new File(path);
			// if (!dir.exists())
			// dir.mkdirs();
			File file = new File(dir, category + ".pdf");
			FileOutputStream fOut = new FileOutputStream(file);

			PdfWriter writer = PdfWriter.getInstance(doc, fOut);
			writer.setFullCompression();
			// open the document
			doc.open();
			for (int i = 0; i < listImage.size(); i++) {
				Bitmap bt = decodeFile(listImage.get(i));
				Image myImg = Image.getInstance(convertBitmapToByteArray(bt));

				// myImg.set
				// myImg.scaleToFit(PageSize.A4.getWidth(),
				// PageSize.A4.getHeight());
				// myImg.scaleAbsolute(200f, 200f);

				// myImg.setRotationDegrees(-90);

				doc.setPageSize(new Rectangle(myImg.getScaledWidth(), myImg
						.getScaledHeight()));
				doc.newPage();
				myImg.setAbsolutePosition(0, 0);

				// add image to document
				doc.add(myImg);
			}

			// set footer
			Phrase footerText = new Phrase(category);
			HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
			doc.setFooter(pdfFooter);
			doc.setHeader(pdfFooter);
		} catch (DocumentException de) {
			Log.e("PDFCreator", "DocumentException:" + de);
			return b;
		} catch (IOException e) {
			Log.e("PDFCreator", "ioException:" + e);
			return b;
		} finally {
			doc.close();
		}
		deleteListFile(listImage);
		return true;

	}

	public void createPDF(String path, ArrayList<File> Files, String category) {
		Document doc = new Document(PageSize.A4);
		try {
			File dir = new File(path);
			// if (!dir.exists())
			// dir.mkdirs();
			File file = new File(dir, category + ".pdf");
			FileOutputStream fOut = new FileOutputStream(file);

			PdfWriter writer = PdfWriter.getInstance(doc, fOut);
			writer.setFullCompression();
			// open the document
			doc.open();
			for (int i = 0; i < Files.size(); i++) {
				Bitmap bt = decodeFile(Files.get(i).getAbsolutePath());
				Image myImg = Image.getInstance(convertBitmapToByteArray(bt));
				// myImg.set
				// myImg.scaleToFit(PageSize.A4.getWidth(),
				// PageSize.A4.getHeight());
				// myImg.scaleAbsolute(200f, 200f);
				myImg.setRotationDegrees(-90);
				doc.setPageSize(new Rectangle(myImg.getScaledWidth(), myImg
						.getScaledHeight()));
				doc.newPage();
				myImg.setAbsolutePosition(0, 0);

				// add image to document
				doc.add(myImg);
			}

			// set footer
			Phrase footerText = new Phrase(category);
			HeaderFooter pdfFooter = new HeaderFooter(footerText, false);
			doc.setFooter(pdfFooter);
			doc.setHeader(pdfFooter);
		} catch (DocumentException de) {
			Log.e("PDFCreator", "DocumentException:" + de);
		} catch (IOException e) {
			Log.e("PDFCreator", "ioException:" + e);
		} finally {
			doc.close();
		}
		// deleteListFile(listImage);

	}

	public void deleteListFile(List<String> listFile) {
		for (int i = 0; i < listFile.size(); i++) {
			deleteFile(listFile.get(i));
		}
	}

	public void deleteFile(String path) {
		File temp = new File(path);
		temp.delete();
	}

	public Bitmap decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);
		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;
		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
		return bitmap;
	}

	public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		} else {
			byte[] b = null;
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0, byteArrayOutputStream);
				b = byteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return b;
		}
	}

	public boolean checkCategory(String path, String category) {
		File dir = new File(path);
		String[] fileNames = dir.list();
		for (String fileName : fileNames) {
			if (fileName.endsWith(".pdf") && fileName.contains(category))
				return true;
		}
		return false;
	}
}
