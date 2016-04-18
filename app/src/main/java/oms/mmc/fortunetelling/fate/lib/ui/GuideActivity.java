package oms.mmc.fortunetelling.fate.lib.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import oms.mmc.fortunetelling.fate.lib.R;

public class GuideActivity extends StarterActivity {

    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddToolBar = false;
        setContentView(R.layout.activity_guide);
    }

    @Override
    protected void setupViews() {

        mFragmentList.add(GuideFragment.getInstance(false));
        mFragmentList.add(GuideFragment.getInstance(true));

        final CheckBox checkBox1 = ViewUtils.getView(this, R.id.guide_check_box1);
        final CheckBox checkBox2 = ViewUtils.getView(this, R.id.guide_check_box2);

        ViewPager mViewPager = ViewUtils.getView(this, R.id.guide_view_page);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                }else {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


}
