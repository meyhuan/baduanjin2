package oms.mmc.fortunetelling.fate.lib.base;

import android.support.v7.widget.Toolbar;

import com.smartydroid.android.starter.kit.app.StarterActivity;

import oms.mmc.fortunetelling.fate.lib.R;

/**
 * Author: meyu
 * Date:   16/3/3
 * Email:  627655140@qq.com
 */
public abstract class BaseActivity extends StarterActivity{


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
    }


}
