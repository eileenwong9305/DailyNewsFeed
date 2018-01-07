package com.example.android.dailynewsfeed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * {@link SectionFragmentPagerAdapter} is a {@link FragmentPagerAdapter} that can provide the
 * layout for each list item based on a data source which is a list of {@link Article} objects.
 */
public class SectionFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * Context of the app
     */
    private Context context;

    /**
     * Create a new {@link SectionFragmentPagerAdapter} object.
     *
     * @param context of the app
     * @param fm      is the fragment manager that will keep each fragment's state in the adapter
     *                across swipes.
     */
    public SectionFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number
     *
     * @param position page number
     * @return {@link Fragment}
     */
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        switch (position) {
            case 0:
                b.putString("section", context.getString(R.string.section_world_key));
                break;
            case 1:
                b.putString("section", context.getString(R.string.section_politics_key));
                break;
            case 2:
                b.putString("section", context.getString(R.string.section_business_key));
                break;
            case 3:
                b.putString("section", context.getString(R.string.section_technology_key));
                break;
            case 4:
                b.putString("section", context.getString(R.string.section_science_key));
                break;
            case 5:
                b.putString("section", context.getString(R.string.section_sport_key));
                break;
            case 6:
                b.putString("section", context.getString(R.string.section_culture_key));
                break;
            case 7:
                b.putString("section", context.getString(R.string.section_lifeandstyle_key));
                break;
            case 8:
                b.putString("section", context.getString(R.string.section_fashion_key));
                break;
            default:
                b.putString("section", context.getString(R.string.section_travel_key));
                break;
        }
        SectionFragment fragment = new SectionFragment();
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * @return total number of pages
     */
    @Override
    public int getCount() {
        return 9;
    }

    /**
     * Return page title to be displayed on the tab.
     *
     * @param position page number
     * @return page title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.section_world_key);
            case 1:
                return context.getString(R.string.section_politics_key);
            case 2:
                return context.getString(R.string.section_business_key);
            case 3:
                return context.getString(R.string.section_technology_key);
            case 4:
                return context.getString(R.string.section_science_key);
            case 5:
                return context.getString(R.string.section_sport_key);
            case 6:
                return context.getString(R.string.section_culture_key);
            case 7:
                return context.getString(R.string.section_lifeandstyle_title);
            case 8:
                return context.getString(R.string.section_fashion_key);
            default:
                return context.getString(R.string.section_travel_key);
        }
    }
}
