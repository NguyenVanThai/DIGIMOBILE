package digi.mobile.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import paul.arian.fileselector.FileSelectionActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digi.mobile.activity.OptionActivity;
import digi.mobile.activity.R;
import digi.mobile.adapter.AdapterGridView;
import digi.mobile.building.IEventListener;
import digi.mobile.building.ImageItem;
import digi.mobile.util.Constant;
import digi.mobile.util.Operation;

public class UploadDocumentFragment extends Fragment implements OnClickListener {

	RelativeLayout relPDF, relCheck, relUnCheck, relGallery, relCamera;
	GridView gridView;
	String pathCustomer;
	String nameShortDucoment;
	Operation operation = new Operation();
	ArrayList<ImageItem> arrayImageItem;
	public static Dialog dialog;
	private IEventListener listener;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		View myFragmentView = inflater.inflate(
				R.layout.fragment_upload_fragment, container, false);

		// Log.d("Fragment 1", "onCreateView");
		// Toast.makeText(getActivity(), "onCreateView",
		// Toast.LENGTH_LONG).show();

//		relativeLayout = (RelativeLayout) myFragmentView
//				.findViewById(R.id.relativeLayout);
		return myFragmentView;
	}



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
