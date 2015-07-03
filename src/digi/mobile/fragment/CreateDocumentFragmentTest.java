package digi.mobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import digi.mobile.activity.R;

public class CreateDocumentFragmentTest extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View myFragmentView = inflater.inflate(
				R.layout.fragment_create_document, container, false);

		return myFragmentView;
	}

}
