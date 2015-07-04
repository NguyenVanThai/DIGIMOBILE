package digi.mobile.activity;

import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import digi.mobile.building.IEventListener;
import digi.mobile.building.MyPagerAdapter;
import digi.mobile.building.MyViewPager;
import digi.mobile.fragment.CreateCustomerFragment;
import digi.mobile.fragment.CreateDocumentFragment;
import digi.mobile.fragment.UploadDocumentFragment;
import digi.mobile.util.Constant;

public class CustomerActivity extends FragmentActivity implements
		IEventListener {
	MyViewPager myViewPager;
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragments = new Vector<Fragment>();

	RelativeLayout relStep1, relStep2, relStep3;
	// page adapter between fragment list and view pager
	private MyPagerAdapter mPagerAdapter;
	// view pager

	public String p2text, p3text;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("CustomerActivity", "onResume");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("CustomerActivity", "Oncreate");

		setContentView(R.layout.activity_customer);
		relStep1 = (RelativeLayout) findViewById(R.id.relStep1);
		relStep2 = (RelativeLayout) findViewById(R.id.relStep2);
		relStep3 = (RelativeLayout) findViewById(R.id.relStep3);

		myViewPager = (MyViewPager) findViewById(R.id.pager);
		myViewPager.setOffscreenPageLimit(2);
		// creating fragments and adding to list
		fragments.add(Fragment.instantiate(this,
				CreateCustomerFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				CreateDocumentFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				UploadDocumentFragment.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapter = new MyPagerAdapter(
				super.getSupportFragmentManager(), fragments);

		myViewPager.setAdapter(this.mPagerAdapter);

		myViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "" + arg0,
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void sendDataToActivity(int step) {
		// TODO Auto-generated method stub

		setColor(step);
		myViewPager.setCurrentItem(step);

	}

	public void setColor(int step) {
		relStep1.setBackgroundColor(getResources().getColor(R.color.black_1));
		relStep2.setBackgroundColor(getResources().getColor(R.color.black_1));
		relStep3.setBackgroundColor(getResources().getColor(R.color.black_1));

		switch (step) {
		case 0:
			relStep1.setBackgroundColor(getResources().getColor(R.color.blue_3));
			break;
		case 1:
			relStep2.setBackgroundColor(getResources().getColor(R.color.blue_3));
			break;
		case 2:
			relStep3.setBackgroundColor(getResources().getColor(R.color.blue_3));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {

		// init Dialog Notification
		final Dialog dialog = new Dialog(CustomerActivity.this,
				R.style.MyTheme_Dialog_Action);
		// dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.dialog_signout);
		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
		TextView message = (TextView) dialog.findViewById(R.id.textViewMessage);
		// init button OK and Cancel
		Button btnOk = (Button) dialog.findViewById(R.id.button1);
		Button btnCancel = (Button) dialog.findViewById(R.id.button2);

		title.setText(getString(R.string.dialog_exit));
		message.setText(getString(R.string.warning_exit));

		dialog.show();

		// handling clicks
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				finish();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

}
