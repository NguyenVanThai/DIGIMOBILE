package digi.mobile.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class DocumentTypeActivity extends Activity {

	ImageButton imageExit;
	ListView lvListCustomer;
	ArrayAdapter<String> arrAdapter;
	Operation operation;
	String pathCustomer;
	String nameCustomer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document_type);

		lvListCustomer = (ListView) findViewById(R.id.listViewDocument);

		imageExit = (ImageButton) findViewById(R.id.imageButtonExit);

		operation = new Operation();
		nameCustomer = Constant.NAME_CUSTOMER;

		// Intent intent = getIntent();
		// pathCustomer = intent.getStringExtra(Constant.PATH_MY_FOLDER);

		String path = Constant.APP_FOLDER + File.separator + Constant.NAME_USER
				+ File.separator + Constant.NAME_CUSTOMER;
		pathCustomer = Constant.getPathRoot(path);

		// pathCustomer = Constant.getPathRoot(Constant.APP_FOLDER
		// + File.separator + Constant.NAME_MY_FOLDER + File.separator
		// + nameCustomer);

		// pathCustomer = pathCustomer =
		// Constant.getPathRoot(Constant.APP_FOLDER
		// + File.separator + nameCustomer);
		//

		arrAdapter = new ArrayAdapter<String>(DocumentTypeActivity.this,
				android.R.layout.simple_list_item_1, Constant.ARRAY_APP_ITEMS);
		lvListCustomer.setAdapter(arrAdapter);

		lvListCustomer
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						// TODO Auto-generated method stub
						final String nameShortDocument;
						nameShortDocument = Constant.ARRAY_APP_SHORT_ITMES[position];
						if (operation.checkCategory(pathCustomer,
								nameShortDocument)) {
							AlertDialog.Builder dialogReplace = new AlertDialog.Builder(
									DocumentTypeActivity.this);
							dialogReplace.setTitle("Do you want replace?");
							dialogReplace.setIcon(R.drawable.ic_action_about);

							dialogReplace.setPositiveButton("YES",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											operation.deleteFile(pathCustomer
													+ File.separator
													+ nameShortDocument
													+ ".pdf");

											showNewAppDetailActivity(position);

										}

									});

							dialogReplace.setNegativeButton("NO",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									});
							dialogReplace.show();
						} else {
							showNewAppDetailActivity(position);

						}
					}
				});

		imageExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void showNewAppDetailActivity(int position) {
		// TODO Auto-generated method stub

		// Constant.NAME_SHORT_DUCOMENT = nameShortDocument;
		// intent.putExtra(Constant.NAME_DUCOMENT, nameShortDocument);

		Intent intent = new Intent();
		intent.putExtra(Constant.POSITION, position);
		
		setResult(RESULT_OK, intent);
		finish();
	}
}
