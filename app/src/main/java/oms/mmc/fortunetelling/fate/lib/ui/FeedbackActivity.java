package oms.mmc.fortunetelling.fate.lib.ui;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.umeng.fb.fragment.FeedbackFragment;

import oms.mmc.fortunetelling.fate.lib.R;

public class FeedbackActivity extends StarterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.feedback);

    }

    @Override
    protected void setupViews() {
        String conversation_id = getIntent().getStringExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID);
        FeedbackFragment mFeedbackFragment = FeedbackFragment.newInstance(conversation_id);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,mFeedbackFragment)
                .commit();
    }
}
