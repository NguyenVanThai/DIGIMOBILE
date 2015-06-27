package digi.mobile.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import digi.mobile.activity.R;
import digi.mobile.building.ImageItem;

public class AdapterGridView extends BaseAdapter {

	private Context context;
	private int layoutResourceId;
	// private ArrayList<ImageItem> dataSource = new ArrayList<ImageItem>();
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
	static LayoutInflater inflater = null;
	private DisplayImageOptions options;
	ImageLoader imageLoader;

	public AdapterGridView(Context context, ArrayList<ImageItem> data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// AsyncTaskLoadFiles asyncTaskLoadFiles = new AsyncTaskLoadFiles(this,
		// dataSource);
		// asyncTaskLoadFiles.execute();
		imageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()
		// .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
				.showImageForEmptyUri(R.drawable.ic_empty) // resource or
															// drawable
				// .showImageOnFail(R.drawable.ic_error) // resource or drawable
				.resetViewBeforeLoading(false) // default
				// .delayBeforeLoading(1000)
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default

				.postProcessor(null)
				// .preProcessor(...)
				// .postProcessor(...)
				// .extraForDownloader(...)
				// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.EXACTLY) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				// .decodingOptions(...)
				// .displayer(new SimpleBitmapDisplayer()) // default
				// .handler(new Handler()) // default
				// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.build();
		// .showImageForEmptyUri(R.drawable.ic_empty)
		// .showImageOnFail(R.drawable.ic_error)
		// .showImageOnLoading(R.drawable.ic_empty)
		// .resetViewBeforeLoading(true).cacheOnDisk(true)
		// .imageScaleType(ImageScaleType.EXACTLY)
		// .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
		// .displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(options)

		.diskCacheExtraOptions(480, 320, null).build();

		ImageLoader.getInstance().init(config);

	}

	void add(ImageItem item) {
		data.add(item);
	}

	void clear() {
		data.clear();
	}

	void remove(int index) {
		data.remove(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {
		TextView imageTitle;
		ImageView image;
		CheckBox checBox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder = new ViewHolder();
		View rowView;
		rowView = inflater.inflate(R.layout.gridview_item, null);
		holder.imageTitle = (TextView) rowView.findViewById(R.id.text);
		holder.image = (ImageView) rowView.findViewById(R.id.picture);
		holder.checBox = (CheckBox) rowView.findViewById(R.id.checkBox1);

		holder.imageTitle.setText(data.get(position).getTitle());
		// holder.image.setImageBitmap(Constant.decodeSampledBitmapFromUri(data
		// .get(position).getImage(), 300, 300));
		holder.image.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				imageLoader.displayImage("file://"
						+ data.get(position).getImage(), holder.image);
			}
		});

		holder.checBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// Here we get the position that we have set for the
						// checkbox using setTag.
						int getPosition = (Integer) buttonView.getTag();
						// Set the value of checkbox to maintain its state.
						data.get(getPosition).setSelected(
								buttonView.isChecked());
					}
				});
		holder.checBox.setId(position);
//		convertView.setTag(holder);
//		convertView.setTag(R.id.text, holder.imageTitle);
//		convertView.setTag(R.id.checkBox1, holder.checBox);
		// imageLoader.displayImage("file://" + data.get(position).getImage(),
		// holder.image, options);

		// ImageAware imageAware = new ImageViewAware(holder.image, false);
		// imageLoader.displayImage("file://" + data.get(position).getImage(),
		// imageAware);
		// imageLoader.displayImage("file://" + data.get(position).getImage(),
		// imageAware, options);
		holder.checBox.setTag(position); // This line is important.

		// holder.text.setText(list.get(position).getName());
		holder.checBox.setChecked(data.get(position).isSelected());
		return rowView;
	}

	public class AsyncTaskLoadFiles extends AsyncTask<Void, ImageItem, Void> {

		AdapterGridView myTaskAdapter;
		private ArrayList<ImageItem> dataSource = new ArrayList<ImageItem>();

		public AsyncTaskLoadFiles(AdapterGridView adapter,
				ArrayList<ImageItem> dataSource) {
			myTaskAdapter = adapter;
			this.dataSource = dataSource;
		}

		@Override
		protected void onPreExecute() {

			myTaskAdapter.clear();

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// File[] files = targetDirector.listFiles();
			// for (File file : files) {
			// publishProgress(file.getAbsolutePath());
			// if (isCancelled())
			// break;
			// }
			for (ImageItem item : dataSource) {
				publishProgress(item);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(ImageItem... values) {
			// TODO Auto-generated method stub
			myTaskAdapter.add(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			myTaskAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

	}

}
