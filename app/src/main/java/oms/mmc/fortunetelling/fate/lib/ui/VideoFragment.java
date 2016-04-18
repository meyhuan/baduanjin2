package oms.mmc.fortunetelling.fate.lib.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.apkfuns.logutils.LogUtils;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.smartydroid.android.starter.kit.utilities.ACache;
import com.smartydroid.android.starter.kit.utilities.DateUtils;
import com.smartydroid.android.starter.kit.utilities.SPUtils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import java.io.File;
import java.util.Locale;

import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.callback.InfoListener;
import oms.mmc.fortunetelling.fate.lib.utils.Util;
import oms.mmc.fortunetelling.fate.lib.base.BaseFragment;
import oms.mmc.fortunetelling.fate.lib.callback.OnActivityInteractionListener;
import oms.mmc.fortunetelling.fate.lib.callback.OnFragmentInteractionListener;
import oms.mmc.fortunetelling.fate.lib.view.ShareCompleteDialog;
import oms.mmc.fortunetelling.fate.lib.view.TagSeekBar;

public class VideoFragment extends BaseFragment implements CacheListener, MediaPlayer.OnCompletionListener,
        View.OnClickListener , OnActivityInteractionListener{


    private FrameLayout containerVideoView;
    private VideoView videoView;
    private ProgressBar videoViewProgressBar;
    private TextView mainMovementNameTv;
    private View containerPauseView;
    private Button mainMovementBreakBtn;
    private TextView videoStartTimeTv;
    private TagSeekBar seekBar;
    private TextView videoEndTimeTv;
    private String [] actionNames;
    private LinearLayout videoDesLayout;
    private View coverView;


    private static final String VIDEO_URL = "http://7xsa5a.com1.z0.glb.clouddn.com/v.mp4";
//    private static final String VIDEO_URL = "http://7xoi5l.com1.z0.glb.clouddn.com/Tmw3TmwmTmxBskgDaWI1D8WvTKE60Oy6fJvIkaKTW3dqKOLzHOuOr.mp4";

    //根据这个值，动作分解会显示不同的内容
    private int extra = Constant.EXTRA_TAG_ACTION_0;

    //记录播放的进度
//    private int videoTimeProgress = 0;
//    private final static int PROGRESS_MAX_VALUE = 10000;
    //是否按Home键
    private boolean isStop = false;
    //用户统计播放的时间
    public static int PLAY_TIME = 0;
    public int actionBarHeight = 0;

    private OnFragmentInteractionListener mListener;
    private final VideoProgressUpdater updater = new VideoProgressUpdater();


    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(int actionBarHeight) {

        VideoFragment fragment = new VideoFragment();
        fragment.actionBarHeight = actionBarHeight;
        return fragment;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_video;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从本地中获取，上次保留下来的缓存锻炼时间
        // 只有上传成功后会被清0）
        String s = ACache.get(getActivity()).getAsString(Constant.USER_PLAY_TIME);
        if(!TextUtils.isEmpty(s)){
            PLAY_TIME = Integer.valueOf(s);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
        startVideo();
        ViewUtils.getView(view, R.id.container_video_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    updater.stop();
                } else {
                    updater.start();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoStartTimeTv.setText(DateUtils.formatSecToMinute(progress / 1000));
                setActionTitle(videoView.getCurrentPosition() );
                SPUtils.put(getActivity(), Constant.VIDEO_PALY_PROCESS_TAG, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                setActionTitle(videoView.getCurrentPosition());
            }
        });

        seekBar.setTagPositions(Constant.TAG_POSITIONS);
    }

    /**
     * 判断视频的缓存时候存在
     * @param context context
     * @return true 存在
     */
    public static boolean isCacheExist(Context context){
        File cache = Util.getVideoCacheFile(context);
        if(new File(cache,new Md5FileNameGenerator().generate(VIDEO_URL)).exists()){
            return true;
        }
        return false;
    }

    public void startVideo() {

        String isCache = ACache.get(getActivity()).getAsString("percentsAvailable");
        File cache = Util.getVideoCacheFile(getActivity());
        if(isCacheExist(getContext())){
            videoView.setVideoPath(new File(cache,new Md5FileNameGenerator().generate(VIDEO_URL)).getAbsolutePath());
        }else {
            HttpProxyCacheServer proxy = App.getProxy(getActivity());
            proxy.registerCacheListener(this, VIDEO_URL);
            videoView.setVideoPath(proxy.getProxyUrl(VIDEO_URL));
        }
        videoView.setKeepScreenOn(true);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dismissUnBackProgressLoading();
//                coverView.setVisibility(View.GONE);
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setMax(videoView.getDuration());
//                videoViewProgressBar.setVisibility(View.GONE);
                videoEndTimeTv.setText(DateUtils.formatSecToMinute(videoView.getDuration() / 1000));
                if(isStop){
                    int videoTimeProgress = (int) SPUtils.get(getActivity(), Constant.VIDEO_PALY_PROCESS_TAG, 0);
                    videoView.seekTo(videoTimeProgress);
                    LogUtils.d("videoView prepare ： "+ videoTimeProgress);
                }else {
                    mp.start();
                    updater.start();
                }
            }
        });
        videoView.setOnCompletionListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        coverView.setVisibility(View.GONE);
                        if(isStop){
                            updater.stop();
                            isStop = false;
                        }
                        containerVideoView.setClickable(true);
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.main_movement_break_btn) {
            Intent intent = new Intent(getActivity(), ActionBreakIndexActivity.class);
            intent.putExtra(Constant.EXTRA_TAG_ACTION_INTENT,extra);
            startActivity(intent);
        }
    }


    /**
     * 视频播放完成是回调
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        updater.stop();
        updateVideoProgress();
        //只有累计的时间超过600秒之后才会显示分享对话框，以及 加时间
        if(PLAY_TIME > 0){
            ShareCompleteDialog shareCompleteDialog = new ShareCompleteDialog(getContext());
            shareCompleteDialog.show();
        }
    }

    /**
     * 视频缓存接口
     *
     * @param cacheFile         cacheFile
     * @param url               url
     * @param percentsAvailable percentsAvailable
     */
    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        seekBar.setSecondaryProgress(percentsAvailable * seekBar.getMax() / 100);
        if(percentsAvailable == 100){
            ACache.get(getActivity()).put("percentsAvailable", "percentsAvailable");
        }
//        LogUtils.d(String.format(Locale.CHINA, "onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url));
    }



    /**
     * 更新进度条
     */
    private void updateVideoProgress() {
        int videoProgress = videoView.getCurrentPosition() ;
        seekBar.setProgress(videoProgress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /**
     * activity 通知 fragment 动作接口
     * @param intent 参数
     */
    @Override
    public void onActivityInteraction(Intent intent) {
        if(intent == null) return;
        //toolbar 已经隐藏了
        if(intent.getIntExtra(Constant.EXTRA_TAG_FRAGMENT_ANMI_START_OR_STOP, -1) == Constant.ANIM_HIDE){
            upLayout();
        }else if(intent.getIntExtra(Constant.EXTRA_TAG_FRAGMENT_ANMI_START_OR_STOP, -1) == Constant.ANIM_SHOW){
            downLayout();
        }
    }

    /**
     * 执行layout上升动画
     */
    private void upLayout(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                videoDesLayout, "y", videoDesLayout.getY(), videoDesLayout.getY() - actionBarHeight);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    /**
     * 执行layout下降动画
     */
    private void downLayout(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                videoDesLayout, "y", videoDesLayout.getY(), videoDesLayout.getY() + actionBarHeight);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }


    /**
     * 根据时间来动态改变动作的标题以及动作的处理
     * @param time
     */
    private void setActionTitle(int time){
        String actionName = actionNames[0];
        if(time < Constant.time_video_action_1){
            actionName =  actionNames[0];
            extra = Constant.EXTRA_TAG_ACTION_0;
        }else if(time < Constant.time_video_action_2){
            actionName =  actionNames[1];
            extra = Constant.EXTRA_TAG_ACTION_1;
        }else if(time < Constant.time_video_action_3){
            actionName =  actionNames[2];
            extra = Constant.EXTRA_TAG_ACTION_2;
        }else if(time < Constant.time_video_action_4){
            actionName =  actionNames[3];
            extra = Constant.EXTRA_TAG_ACTION_3;
        }else if(time < Constant.time_video_action_5){
            actionName =  actionNames[4];
            extra = Constant.EXTRA_TAG_ACTION_4;
        }else if(time < Constant.time_video_action_6){
            actionName =  actionNames[5];
            extra = Constant.EXTRA_TAG_ACTION_5;
        }else if(time < Constant.time_video_action_7){
            actionName =  actionNames[6];
            extra = Constant.EXTRA_TAG_ACTION_6;
        }else if(time < Constant.time_video_action_8){
            actionName =  actionNames[7];
            extra = Constant.EXTRA_TAG_ACTION_7;
        }else if(time >= Constant.time_video_action_8){
            actionName =  actionNames[8];
            extra = Constant.EXTRA_TAG_ACTION_8;
        }
        mainMovementNameTv.setText(actionName);
    }



    /**
     * 视频进度条控制
     */
    private final class VideoProgressUpdater extends Handler {

        public void start() {
            sendEmptyMessage(0);
            videoView.start();
            containerPauseView.setVisibility(View.GONE);
            isStop = false;
            if(mListener != null){
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA_TAG_FRAGMENT_VIDEO_START_OR_STOP, 1);
                mListener.onFragmentInteraction(intent);
            }
        }

        public void stop() {
            removeMessages(0);
            videoView.pause();
            containerPauseView.setVisibility(View.VISIBLE);
            if(mListener != null){
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA_TAG_FRAGMENT_VIDEO_START_OR_STOP, 0);
                mListener.onFragmentInteraction(intent);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            //100% 停止更新进度条
            updateVideoProgress();
            sendEmptyMessageDelayed(0, 1000);
            PLAY_TIME++;
//            LogUtils.i("PLAY:TIME:" + PLAY_TIME);
            if((PLAY_TIME + 1) % 10 == 0){
                ACache.get(getActivity()).put(Constant.USER_PLAY_TIME, PLAY_TIME+"");
            }
        }
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        isStop = true;
        updater.stop();
        coverView.setVisibility(View.VISIBLE);
        containerPauseView.setVisibility(View.GONE);
        containerVideoView.setClickable(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        App.getProxy(getActivity()).unregisterCacheListener(this);
    }

    private void assignViews(View view) {
        actionNames = getResources().getStringArray(R.array.action_name);
        containerVideoView = (FrameLayout) view.findViewById(R.id.container_video_view);
        videoView = (VideoView) view.findViewById(R.id.videoView);
        videoViewProgressBar = (ProgressBar) view.findViewById(R.id.videoView_progress_bar);
        mainMovementNameTv = (TextView) view.findViewById(R.id.main_movement_name_tv);
        containerPauseView = view.findViewById(R.id.container_pause_view);
        mainMovementBreakBtn = (Button) view.findViewById(R.id.main_movement_break_btn);
        videoStartTimeTv = (TextView) view.findViewById(R.id.video_start_time_tv);
        seekBar = (TagSeekBar) view.findViewById(R.id.progressBar);
        videoEndTimeTv = (TextView) view.findViewById(R.id.video_end_time_tv);
        videoDesLayout = (LinearLayout) view.findViewById(R.id.video_des_layout);
        coverView = view.findViewById(R.id.video_view_cover);

        mainMovementBreakBtn.setOnClickListener(this);

    }

}
