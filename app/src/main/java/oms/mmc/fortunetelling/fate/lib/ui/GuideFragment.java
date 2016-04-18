package oms.mmc.fortunetelling.fate.lib.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.apkfuns.logutils.LogUtils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment extends Fragment {

    public boolean mIsLast = false;
    public GuideFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(boolean isLast){
        GuideFragment guideFragment = new GuideFragment();
        guideFragment.mIsLast = isLast;
        return guideFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_guide, container, false);
        ViewGroup v1 = ViewUtils.getView(root, R.id.guide_layout1);
        ViewGroup v2 = ViewUtils.getView(root, R.id.guide_layout2);
        if(mIsLast){
            v1.setVisibility(View.GONE);
            v2.setVisibility(View.VISIBLE);
            ImageButton imageButton = ViewUtils.getView(root,R.id.guide_fragment_image_button);
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((App)getActivity().getApplication()).startMainActivity(new Bundle());
                    getActivity().finish();
                }
            });
        }else {
            v1.setVisibility(View.VISIBLE);
            v2.setVisibility(View.GONE);
        }
        return root;
    }

}
