package digi.mobile.building;

import java.io.File;
import java.util.ArrayList;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class DigiCompressFile {
	public String compressFilesToZip(ArrayList<File> arr, String password,
			String pathSave, String zipName) throws Exception {

		//
		zipName = pathSave + File.separator + zipName;
		ZipFile zipFile = new ZipFile(zipName);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
																		// compression
																		// method
																		// to
																		// store
																		// compression

		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Set the encryption flag to true
		// If this is set to false, then the rest of encryption properties
		// are ignored
		if (password != null && !password.isEmpty()) {
			parameters.setEncryptFiles(true);

			// Set the encryption method to Standard Zip Encryption
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

			// Set password
			parameters.setPassword(password);
		}

		// zipFile.addFolder(directory, parameters); // only support one
		// folder
		zipFile.addFiles(arr, parameters);

		return zipName;
	}

	public String compressFolderToZip(String pathFolder, String pathSave,
			String zipName) throws ZipException {

		zipName = pathSave + File.separator + zipName;

		// check File zip
		File file = new File(zipName);
		if (file.exists()) {
			file.delete();
		}

		ZipFile zipFile = new ZipFile(zipName);

		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
																		// compression
																		// method
																		// to
																		// store
																		// compression

		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		zipFile.addFolder(pathFolder, parameters);
		// zipFile.createZipFileFromFolder(pathFolder, parameters, splitArchive,
		// splitLength);
		return zipName;
	}

	public String compressFilesToZip(ArrayList<File> arr, String pathSave,
			String zipName) throws Exception {

		zipName = pathSave + File.separator + zipName;

		// check File exist

		File file = new File(zipName);
		if (file.exists()) {
			file.delete();
		}

		ZipFile zipFile = new ZipFile(zipName);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
																		// compression
																		// method
																		// to
																		// store
																		// compression

		// Set the compression level
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Set the encryption flag to true
		// If this is set to false, then the rest of encryption properties
		// are ignored

		// parameters.setEncryptFiles(true);

		// Set the encryption method to Standard Zip Encryption
		// parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);

		// Set password

		// zipFile.addFolder(directory, parameters); // only support one
		// folder
		zipFile.addFiles(arr, parameters);

		return zipName;
	}

}
