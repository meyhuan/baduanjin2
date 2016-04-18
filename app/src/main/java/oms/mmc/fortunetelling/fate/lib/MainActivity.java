package oms.mmc.fortunetelling.fate.lib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import com.smartydroid.android.starter.kit.app.WebActivity;
import com.smartydroid.android.starter.kit.helper.GlideRoundTransform;
import com.smartydroid.android.starter.kit.helper.StatusBarHelper;
import com.smartydroid.android.starter.kit.utilities.ACache;
import com.smartydroid.android.starter.kit.utilities.ImageUtils;
import com.smartydroid.android.starter.kit.utilities.ScreenUtils;
import com.smartydroid.android.starter.kit.utilities.StarterCommon;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.fragment.FeedbackFragment;

import oms.mmc.fortunetelling.fate.lib.callback.OnActivityInteractionListener;
import oms.mmc.fortunetelling.fate.lib.callback.OnFragmentInteractionListener;
import oms.mmc.fortunetelling.fate.lib.model.entity.BaseResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;
import oms.mmc.fortunetelling.fate.lib.model.entity.UserInfo;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import oms.mmc.fortunetelling.fate.lib.ui.AboutAppActivity;
import oms.mmc.fortunetelling.fate.lib.ui.FeedbackActivity;
import oms.mmc.fortunetelling.fate.lib.ui.MainFragment;
import oms.mmc.fortunetelling.fate.lib.ui.NotificationActivity;
import oms.mmc.fortunetelling.fate.lib.ui.RankActivity;
import oms.mmc.fortunetelling.fate.lib.ui.SettingActivity;
import oms.mmc.fortunetelling.fate.lib.ui.VideoFragment;
import oms.mmc.fortunetelling.fate.lib.user.Helper;
import oms.mmc.fortunetelling.fate.lib.view.ShareCompleteDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends StarterNetworkActivity<UserInfo>
        implements NavigationView.OnNavigationItemSelectedListener,
        AccountManager.CurrentAccountObserver, OnFragmentInteractionListener {

    String loginOrRegister;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    Toolbar toolbar;

    private ImageView mAvatarImageView;
    private TextView mNicknameTextView;

    //当用户的信息改变的时候，会通知当前的activity去更改信息，但是有可能当前的activity 处于destroy，可能会出现：
    //（Glider）You cannot start a load for a destroyed activity 异常，所以暂时去修改这个变量，但resume的时候更改信息
    private static boolean FALG_REINIT_USER_INFO = false;

    //发送指令到fragment的接口
    private OnActivityInteractionListener onActivityInteractionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决播放视频前黑屏问题（似乎没有什么卵用）
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        isAddToolBar = false;
        setContentView(R.layout.activity_main);
        //注册一个用户信息改变回调的监听器
        AccountManager.registerObserver(this);
    }

    @Override
    protected void setupViews() {

        loginOrRegister = getString(R.string.login_or_register);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //设置标题放在setSupportActionBar 的前面
        toolbar.setTitle(R.string.activity_name_app);
        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MobclickAgent.onEvent(mContext, "main_avatar");
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.main_avatar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_tool_bar_bg));

        navView.setNavigationItemSelectedListener(this);
        //需要加上这一句才能显示图片的正确颜色
        navView.setItemIconTintList(null);

        View headerView = navView.getHeaderView(0);
        mAvatarImageView = ViewUtils.getView(headerView, R.id.nav_head_view_avatar_img);
        mNicknameTextView = ViewUtils.getView(headerView, R.id.nav_head_view_nickname_tv);
        initUserInfo();
        bindUserInfo();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isLogin(mContext, getString(R.string.not_login_tip1))) {
                    startActivity(new Intent(mContext, SettingActivity.class));
                }
                MobclickAgent.onEvent(mContext, "nav_avatar");
            }
        });
        addVideoFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getExtras()!=null && intent.getExtras().getBoolean(Constant.EXTRA_FROM_LOGIN_ACTIVITY)){
            initUserInfo();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //发现小于4.4版本的会有问题
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
                layoutParams.setMargins(0, ScreenUtils.getStatusHeight(this), 0, 0);
                toolbar.setLayoutParams(layoutParams);
            }

//            FrameLayout videoContainer = ViewUtils.getView(this, R.id.main_video_cantainer);
//            RelativeLayout.LayoutParams videoLayoutParams = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
//            videoLayoutParams.setMargins(0, ScreenUtils.getStatusHeight(this), 0, 0);
//            videoContainer.setLayoutParams(videoLayoutParams);
        }
    }

    /**
     * 添加视频播放Fragment到activity
     */
    private void addVideoFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_video_cantainer, MainFragment.newInstance())
                .commit();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(FALG_REINIT_USER_INFO){
            bindUserInfo();
            FALG_REINIT_USER_INFO = false;
        }
        Helper.modifyExitAction(this);
    }

    /**
     * 当在其他地方的修改用户信息的时候 此方法会被回调
     */
    @Override
    public void userInfoNotifyChange() {
        if(!this.isFinishing()){
            bindUserInfo();
        }else {
            //onResume的时候再更改
            FALG_REINIT_USER_INFO = true;
        }
    }

    /**
     * 只保存了用户名和密码的情况下去请求服务器用户信息
     */
    private void initUserInfo(){
        String userName = ACache.get(this).getAsString(ApiService.USERNAME);
        String userPW = ACache.get(this).getAsString(ApiService.PASSWORD);
        if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPW) && !AccountManager.isLogin()){
            Call<UserInfo> userInfoCall = ApiService.createAuthService().getUserInfo(ApiService.APP_KEY, userName);
            networkQueue().enqueue(userInfoCall);
        }
    }

    @Override
    public void respondSuccess(UserInfo data) {
        super.respondSuccess(data);
        if(data.status.equals(ApiService.OK)){
            User user = data.content;
            //去掉默认服务器给的图片
            if(user.imgUrl.equals("http://7wy478.com1.z0.glb.clouddn.com/app/20151231231229.png")){
                user.imgUrl = "";
            }
            user.userPW = ACache.get(this).getAsString(ApiService.PASSWORD);
            AccountManager.store(user);
            bindUserInfo();
        }else {
            Utils.showToast(this,getString(R.string.login_get_user_info_fail));
        }
    }

    private void bindUserInfo() {
        if (AccountManager.isLogin()) {
            User user = AccountManager.getCurrentAccount();
            mNicknameTextView.setText(user.name);
            Glide.with(mContext).load(user.imgUrl).placeholder(R.drawable.main_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(mContext)).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    Bitmap bitmap = ImageUtils.drawableToBitmap(resource);
                    mAvatarImageView.setImageBitmap(bitmap);
                    Bitmap sBitmap = ImageUtils.scaleDownBitmap(mContext, bitmap, 38);
                    toolbar.setNavigationIcon(new BitmapDrawable(mContext.getResources(), sBitmap));
                }
            });
            mNicknameTextView.setText(TextUtils.isEmpty(user.name) ? User.DEFAULT_NAME + user.id : user.name);
        } else {
            mNicknameTextView.setText(loginOrRegister);
            mAvatarImageView.setImageResource(R.drawable.ic_default_avatar);
            toolbar.setNavigationIcon(R.drawable.main_avatar);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up material_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            startActivity(NotificationActivity.class);
            MobclickAgent.onEvent(mContext, "main_exercise_notify");
            return true;
        } else if (id == R.id.action_rank) {
            MobclickAgent.onEvent(mContext, "main_rank");
            if(Helper.isLogin(this, getString(R.string.not_login_tip2))){
                startActivity(RankActivity.class);
            }
            return true;
        } else if (id == R.id.action_jifen) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_about_bdj) {
            startActivity(AboutAppActivity.class);
            MobclickAgent.onEvent(mContext, "nav_about_app");
        } else if (id == R.id.nav_about_us) {
            MobclickAgent.onEvent(mContext, "nav_about_us");
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra(WebActivity.PARAM_NAME,getResources().getString(R.string.about_us));
            intent.putExtra(WebActivity.PARAM_URL,"http://m.linghit.com/Index/aboutus");
            startActivity(intent);
        } else if (id == R.id.nav_fb) {
            MobclickAgent.onEvent(mContext, "nav_feedback");
//            Intent intent = new Intent(mContext, WebActivity.class);
//            intent.putExtra(WebActivity.PARAM_NAME,getResources().getString(R.string.face_back));
//            intent.putExtra(WebActivity.PARAM_URL,"http://m.linghit.com/Index/message");
//            startActivity(intent);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, FeedbackActivity.class);
            String idd = new FeedbackAgent(MainActivity.this).getDefaultConversation().getId();
            intent.putExtra(FeedbackFragment.BUNDLE_KEY_CONVERSATION_ID, idd);
            startActivity(intent);
        } else if (id == R.id.nav_comment) {
            StarterCommon.goMarket(this);
            MobclickAgent.onEvent(mContext, "nav_comment");
        } else if (id == R.id.nav_setting) {
            MobclickAgent.onEvent(mContext, "nav_setting");
            if(Helper.isLogin(this, getString(R.string.not_login_tip1))){
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        }else if (id == R.id.nav_invite) {
            MobclickAgent.onEvent(mContext, "nav_invite");
            if(Helper.isLogin(this, getString(R.string.not_login_tip1))){
                new ShareCompleteDialog(this).showShare(true);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void setStatusBar() {
//        super.setStatusBar();
        StatusBarHelper.setColor(this, getResources().getColor(R.color.nav_status_bar_color));
        StatusBarHelper.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout), getResources()
                .getColor(R.color.nav_status_bar_color));

    }

    /**
     * fragment 通信回调接口
     *
     * @param intent
     */
    @Override
    public void onFragmentInteraction(Intent intent) {
        if (intent == null) return;
        int tag = intent.getIntExtra(Constant.EXTRA_TAG_FRAGMENT, 0);
        if (tag == Constant.EXTRA_TAG_FRAGMENT_MAIN) {
            Fragment fragment = VideoFragment.newInstance(toolbar.getHeight());
            onActivityInteractionListener = (OnActivityInteractionListener) fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_video_cantainer, fragment)
                    .commit();
        }

        //暂停显示toolbar
        if (intent.getIntExtra(Constant.EXTRA_TAG_FRAGMENT_VIDEO_START_OR_STOP, -1) == 0) {//播放视频3秒显示toolbar
            hideTime = 0;   //从新计算时间
            showToolBar();
        } else if (intent.getIntExtra(Constant.EXTRA_TAG_FRAGMENT_VIDEO_START_OR_STOP, -1) == 1) {
            hideTime = 0;   //从新计算时间
            hideToolBarHanlder.post(hideToolBarRunnable);
        }
    }

    private static final int HIDEED = 1;
    private static final int NOTHIDEED = 0;
    private int isHideed = NOTHIDEED;
    Handler hideToolBarHanlder = new Handler();
    int hideTime = 0;
    //处理3秒显示全屏和暂停恢复逻辑
    Runnable hideToolBarRunnable = new Runnable() {
        @Override
        public void run() {
            hideToolBarHanlder.postDelayed(this, 1000);
            if (hideTime == 3) {
                hideToolBar();
                hideTime = 0;
                hideToolBarHanlder.removeCallbacks(this);
            }
            hideTime++;
            LogUtils.d(hideTime);
        }
    };

    private void hideToolBar() {
        if (isHideed == NOTHIDEED) {    //如果当前是出于显示的状态才会去隐藏
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbar, "y", toolbar.getY(), toolbar.getY() - toolbar.getHeight());
            objectAnimator.setDuration(200);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    isHideed = HIDEED;
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_TAG_FRAGMENT_ANMI_START_OR_STOP, Constant.ANIM_HIDE);
                    if(onActivityInteractionListener != null){
                        onActivityInteractionListener.onActivityInteraction(intent);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            objectAnimator.start();
        }

    }

    private void showToolBar() {
        if (isHideed == HIDEED) {   //如果当前是出于隐藏的状态才会去显示
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(toolbar, "y", toolbar.getY(), toolbar.getY() + toolbar.getHeight());
            objectAnimator.setDuration(200);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    isHideed = NOTHIDEED;
                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_TAG_FRAGMENT_ANMI_START_OR_STOP, Constant.ANIM_SHOW);
                    if(onActivityInteractionListener != null){
                        onActivityInteractionListener.onActivityInteraction(intent);
                    }

                }
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            objectAnimator.start();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!navView.isShown()){
                Helper.showExitDialog(this, false);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

