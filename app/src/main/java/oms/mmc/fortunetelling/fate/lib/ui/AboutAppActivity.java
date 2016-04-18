package oms.mmc.fortunetelling.fate.lib.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.smartydroid.android.starter.kit.app.StarterActivity;

import oms.mmc.fortunetelling.fate.lib.R;

public class AboutAppActivity extends StarterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
    }

    @Override
    protected void setupViews() {

    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_about_app);
    }
}
