package digi.mobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import digi.mobile.activity.R;
import digi.mobile.building.History;

public class CustomerListAdapter extends ArrayAdapter<History> {

	private final Activity context;
	private final List<History> itemname;

	public CustomerListAdapter(Activity context, List<History> objects) {
		super(context, R.layout.customerlist, objects);
		this.context = context;
		this.itemname = objects;
	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.customerlist, null, true);

		EditText edName = (EditText) rowView.findViewById(R.id.edName);
		EditText edID = (EditText) rowView.findViewById(R.id.edID);
		EditText edType = (EditText) rowView.findViewById(R.id.edType);
		
		edName.setText(itemname.get(position).getName());
		edID.setText(itemname.get(position).getId());
		edType.setText(itemname.get(position).getType());
		
		return rowView;

	};
}
