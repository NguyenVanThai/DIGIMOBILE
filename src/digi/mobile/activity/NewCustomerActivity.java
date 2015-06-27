package digi.mobile.activity;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RelativeLayout;
import digi.mobile.building.IEventListener;
import digi.mobile.building.MyPagerAdapter;
import digi.mobile.building.MyViewPager;
import digi.mobile.fragment.CreateCustomerFragment1;
import digi.mobile.fragment.PdfFragment;
import digi.mobile.fragment.PhotoFragment;

public class NewCustomerActivity extends FragmentActivity implements
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_customer);
		relStep1 = (RelativeLayout) findViewById(R.id.relStep1);
		relStep2 = (RelativeLayout) findViewById(R.id.relStep2);
		relStep3 = (RelativeLayout) findViewById(R.id.relStep3);

		myViewPager = (MyViewPager) findViewById(R.id.pager);

		// creating fragments and adding to list
		fragments.add(Fragment.instantiate(this,
				CreateCustomerFragment1.class.getName()));
		fragments.add(Fragment.instantiate(this, PdfFragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, PhotoFragment.class.getName()));

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

		myViewPager.setCurrentItem(step);

	}

}
