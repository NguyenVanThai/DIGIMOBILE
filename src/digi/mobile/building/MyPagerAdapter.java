package digi.mobile.building;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

	UpdateableFragment updateableFragment;
	// fragments to instantiate in the viewpager
	private List<Fragment> fragments;

	// constructor
	public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	// return access to fragment from position, required override
	@Override
	public Fragment getItem(int position) {

		return this.fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		if(object instanceof UpdateableFragment){
			((UpdateableFragment) object).update("OK");
		}
		return super.getItemPosition(object);
	}

	// number of fragments in list, required override
	@Override
	public int getCount() {
		return this.fragments.size();
	}

}
