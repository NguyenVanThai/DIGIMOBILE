package digi.mobile.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.DocumentTypeActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.PdfListAdapter;
import digi.mobile.building.IEventListener;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class PdfFragment extends Fragment {

	RelativeLayout relNext, relUpload;
	LinearLayout lReason, lDucoment;
	private IEventListener listener;
	Operation operation = new Operation();
	ListView listView;
	EditText edReason;
	String pathCustomer;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IEventListener) {
			listener = (IEventListener) activity;
		} else {
			// Throw an error!
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			String path = Constant.APP_FOLDER + File.separator
					+ Constant.NAME_USER + File.separator
					+ Constant.NAME_CUSTOMER;
			pathCustomer = Constant.getPathRoot(path);
			showPdf();
		} else {
			// Log.d("frag2", "HIde");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.pdf_fragment,
				container, false);
		edReason = (EditText) myFragmentView.findViewById(R.id.edReason);
		listView = (ListView) myFragmentView.findViewById(R.id.lvPdf);
		lReason = (LinearLayout) myFragmentView.findViewById(R.id.lReason);
		lDucoment = (LinearLayout) myFragmentView.findViewById(R.id.lDocument);
		relNext = (RelativeLayout) myFragmentView
				.findViewById(R.id.RelativeLayout_Next);
		relUpload = (RelativeLayout) myFragmentView
				.findViewById(R.id.RelativeLayout_Upload);

		//
		checkType();

		relNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), DocumentTypeActivity.class);

				startActivityForResult(intent,
						Constant.REQUEST_CODE_DUCOMENT_TYPE_ACTIVITY);

			}
		});

		relUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkUpload();
			}
		});
		return myFragmentView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Constant.REQUEST_CODE_DUCOMENT_TYPE_ACTIVITY:
			int position = data.getIntExtra(Constant.POSITION, 100);
			Constant.NAME_SHORT_DUCOMENT = Constant.ARRAY_APP_SHORT_ITMES[position];
			// listener.sendDataToActivity(Constant.Step_3);
			// Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT)
			// .show();
			sendDataToActivity(Constant.Step_3);
			break;
		}

	}

	public void sendDataToActivity(int step) {
		// do whatever you want
		listener.sendDataToActivity(step);
	}

	private void showPdf() {
		// TODO Auto-generated method stub
		List<String> listPDF = operation.listImagebyCategory(pathCustomer,
				".pdf", "name");
		if (listPDF.size() > 0) {

			PdfListAdapter pdfListAdapter = new PdfListAdapter(getActivity(),
					convertListToArray(listPDF));
			pdfListAdapter.notifyDataSetChanged();
			listView.setAdapter(pdfListAdapter);

		}
	}

	private String[] convertListToArray(List<String> listFolder) {
		ArrayList<String> list = (ArrayList<String>) listFolder;
		return list.toArray(new String[list.size()]);

	}

	private void checkType() {
		switch (Constant.TYPE) {
		case 1:// new
			lReason.setVisibility(View.GONE);
			break;
		case 2:// hard
			lReason.setVisibility(View.VISIBLE);
			lDucoment.setVisibility(View.VISIBLE);
			break;
		case 3:// soft
			lReason.setVisibility(View.VISIBLE);
			lDucoment.setVisibility(View.GONE);
			relNext.setVisibility(View.GONE);

			break;
		}
	}

	private void checkUpload() {
		switch (Constant.TYPE) {
		case 1:// new
				// checkListView();
			if (checkLvPDF()) {

			}
			break;
		case 2:// hard
			if (checkEtReason() && checkLvPDF()) {

			}

			break;
		case 3:// soft
			if (checkEtReason()) {

			}
			break;
		}
	}

	private boolean checkLvPDF() {
		int count = listView.getCount();
		int temp = 0;
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				TextView txt = (TextView) listView.getChildAt(i).findViewById(
						R.id.item);
				switch (txt.getText().toString()) {
				case "DN.pdf":
					temp++;
					break;
				case "ID.pdf":
					temp++;
					break;
				case "HK.pdf":
					temp++;
					break;
				}
			}
			if (temp < 3) {
				Toast.makeText(
						getActivity(),
						"Please create full 3 documents: DN.pdf, HK.pdf, ID.pdf",
						Toast.LENGTH_LONG).show();
				return false;
			} else {
				return true;
			}
		} else {
			Toast.makeText(getActivity(), "Please create Document",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private boolean checkEtReason() {
		if (edReason.getText().toString().isEmpty()) {
			Toast.makeText(getActivity(), "Please fill in a reason",
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}
	}
	// private String getNameCustomer() {
	// switch (Constant.TYPE) {
	// case 1:
	// return txtReview.getText().toString();
	//
	// case 2:
	// case 3:
	// return txtReview.getText().toString() + "_"
	// + edCode.getText().toString();
	//
	// }
	// return null;
	// }
}
