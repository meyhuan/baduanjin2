package oms.mmc.fortunetelling.fate.lib.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.smartydroid.android.starter.kit.utilities.Utils;
import com.umeng.analytics.MobclickAgent;

import butterknife.OnClick;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.base.BaseFragment;
import oms.mmc.fortunetelling.fate.lib.callback.OnFragmentInteractionListener;

public class MainFragment extends BaseFragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private Button mainMovementStartBtn;
    private Button mainMovementBreakBtn;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainMovementStartBtn = (Button) view.findViewById(R.id.main_movement_start_btn);
        mainMovementBreakBtn = (Button) view.findViewById(R.id.main_movement_break_btn);
        mainMovementStartBtn.setOnClickListener(this);
        mainMovementBreakBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.main_movement_break_btn) {
            startActivity(new Intent(getActivity(), ActionBreakIndexActivity.class));
            MobclickAgent.onEvent(getActivity(),"main_exercise_break");
        } else if (id == R.id.main_movement_start_btn) {
            MobclickAgent.onEvent(getActivity(),"main_start_exercise");
            if(Utils.isInternetAvailable(getActivity()) || VideoFragment.isCacheExist(getActivity())){
                if(Utils.isExecuted(getActivity(), "no_wifi_tip_is_show")){
                    Utils.showToast(getActivity(), R.string.not_wifi);
                }
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA_TAG_FRAGMENT, Constant.EXTRA_TAG_FRAGMENT_MAIN);
                onButtonPressed(intent);
            }else {
                Utils.showToast(getActivity(), R.string.network_unavailable);
            }

        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_main;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Intent intent) {
        if (mListener != null) {
            mListener.onFragmentInteraction(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
