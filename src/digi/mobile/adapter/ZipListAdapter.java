package digi.mobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import digi.mobile.activity.R;

public class ZipListAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final List<String> itemname;

	public ZipListAdapter(Activity context, List<String> itemname) {
		super(context, R.layout.mylist, itemname);
		// TODO Auto-generated constructor stub

		this.context = context;
		this.itemname = itemname;
	}

	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.ziplist, null, true);

		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		// ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		// TextView extratxt = (TextView)
		// rowView.findViewById(R.id.description);

		txtTitle.setText(itemname.get(position));
		// imageView.setImageResource(imgid[position]);
		// extratxt.setText("Description "+itemname[position]);
		
		return rowView;

	};
}
