package digi.mobile.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import digi.mobile.activity.R;

public class PdfListAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] itemname;

	public PdfListAdapter(Activity context, String[] itemname) {
		super(context, R.layout.mylist, itemname);
		// TODO Auto-generated constructor stub

		this.context = context;
		this.itemname = itemname;
	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.mylist, null, true);

		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		// ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		// TextView extratxt = (TextView)
		// rowView.findViewById(R.id.description);

		txtTitle.setText(itemname[position]);
		// imageView.setImageResource(imgid[position]);
		// extratxt.setText("Description "+itemname[position]);
		CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox1);
		return rowView;

	};
}
