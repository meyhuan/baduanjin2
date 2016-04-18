package oms.mmc.fortunetelling.fate.lib.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.Utils;

import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.R;

public class LaunchActivity extends StarterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddToolBar = false;
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void setupViews() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Utils.isFirstStart(mContext)){
                    startActivity(GuideActivity.class);
                }else {
                    ((App)getApplication()).startMainActivity(new Bundle());
                }
                finish();
            }
        }, 2000);

    }
}
