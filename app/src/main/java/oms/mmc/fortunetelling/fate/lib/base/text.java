//package oms.mmc.fortunetelling.corelibrary.activity;
//
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.mmc.base.http.HttpListener;
//import com.mmc.base.http.HttpResponse;
//import com.mmc.base.http.error.HttpError;
//import com.mmc.core.log.LG;
//import com.umeng.analytics.MobclickAgent;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//import java.util.UUID;
//
//import oms.mmc.fortunetelling.baselibrary.constant.Constants;
//import oms.mmc.fortunetelling.baselibrary.core.Constant;
//import oms.mmc.fortunetelling.baselibrary.core.NetWorkController;
//import oms.mmc.fortunetelling.baselibrary.core.SDCardCacheController;
//import oms.mmc.fortunetelling.baselibrary.core.Settings;
//import oms.mmc.fortunetelling.baselibrary.dao.BaseDataEntity;
//import oms.mmc.fortunetelling.baselibrary.http.BaseDataConvert;
//import oms.mmc.fortunetelling.baselibrary.http.LingjiHttpListener;
//import oms.mmc.fortunetelling.baselibrary.http.RequestManager;
//import oms.mmc.fortunetelling.baselibrary.http.datas.BaseData;
//import oms.mmc.fortunetelling.baselibrary.http.datas.OnDataCallBack;
//import oms.mmc.fortunetelling.baselibrary.model.UserInfo;
//import oms.mmc.fortunetelling.baselibrary.ui.activity.BaseLingJiActivity;
//import oms.mmc.fortunetelling.baselibrary.util.DbUtil;
//import oms.mmc.fortunetelling.baselibrary.widget.ProgressWaitDialog;
//import oms.mmc.fortunetelling.corelibrary.R;
//import oms.mmc.fortunetelling.corelibrary.adapter.main.YunchengAdapter;
//import oms.mmc.fortunetelling.baselibrary.core.BroadcastController;
//import oms.mmc.fortunetelling.corelibrary.core.UserController;
//import oms.mmc.fortunetelling.corelibrary.util.ImageLoader;
//import oms.mmc.util.L;
//import oms.mmc.util.Util;
//import oms.mmc.widget.LunarDataTimePopupWindow;
//import oms.mmc.widget.LunarDateTimeView;
//import oms.mmc.widget.MMCAlertDialog;
//
///**
// * Name: UserInfoActivity
// * User: Max
// * Date: 2015-11-12
// * Email: mojunhao@mmclick.com
// * description: 用户资料
// */
//
//public class UserInfoActivity extends BaseLingJiActivity implements View.OnClickListener {
//
//    private LinearLayout iconLayout;
//    private LinearLayout nameLayout;
//    private LinearLayout sexLayout;
//    private LinearLayout workLayout;
//    private LinearLayout marryLayout;
//    private LinearLayout emailLayout;
//    private ImageView userIcon;
//    private TextView userName;
//    private TextView userId;
//    private TextView userBrith;
//    private TextView userSex;
//    private TextView userWork;
//    private TextView userMarry;
//    private TextView userEmail;
//    private TextView modifyPW;
//    private Button logoutBt;
//    private UserController mUserController;
//    private UserInfo mUserInfo;
//    private UserBroadcast mBroadcast;
//    public RequestManager requestManager;
//    public static final int PHOTOHRAPH = 1;// 拍照
//    public static final int PHOTOZOOM = 2; // 缩放
//    public static final int PHOTORESOULT = 3;// 结果
//    public static final String IMAGE_UNSPECIFIED = "image/*";
//    private Uri mPhotoUri;
//    public File mFileHead;
//    private static String TYPE = "type";
//    private static String NAME = "name";
//    private static String EMAIL = "email";
//    private String email = "";
//    private ProgressWaitDialog mLoaderDialog;
//    private LunarDataTimePopupWindow mDataTimePopup;
//    private Calendar mCalendar = Calendar.getInstance();
//    private int verifyemail = 0;
//    private MMCAlertDialog workDialog;
//    private MMCAlertDialog marryDialog;
//    private String action = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.lingji_userinfo_layout);
//        UserController.init(this);
//        action = getIntent().getAction();
//        mUserController = UserController.getInstance();
//        mUserInfo = mUserController.getLocalUserInfo();
//        mBroadcast = new UserBroadcast();
//        requestManager = RequestManager.getInstance();
//        UserController.registerUserChanger(this, mBroadcast);
//        mLoaderDialog = new ProgressWaitDialog(this);
//        initView();
//        if (mUserInfo != null) {
//            initData();
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // 每次进来都load用户信息
//        mUserController.loadUserInfo();
//    }
//
//    private void initView() {
//        iconLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_icon_layout);
//        nameLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_name_layout);
//        sexLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_sex_layout);
//        workLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_work_layout);
//        marryLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_marriage_layout);
//        emailLayout = (LinearLayout) findViewById(R.id.lingji_userinfo_email_layout);
//
//        userIcon = (ImageView) findViewById(R.id.lingji_userinfo_icon);
//        userName = (TextView) findViewById(R.id.lingji_userinfo_name);
//        userId = (TextView) findViewById(R.id.lingji_userinfo_id);
//        userBrith = (TextView) findViewById(R.id.lingji_userinfo_date);
//        userSex = (TextView) findViewById(R.id.lingji_userinfo_sex);
//        userWork = (TextView) findViewById(R.id.lingji_userinfo_work);
//        userMarry = (TextView) findViewById(R.id.lingji_userinfo_marriage);
//        userEmail = (TextView) findViewById(R.id.lingji_userinfo_email);
//        modifyPW = (TextView) findViewById(R.id.lingji_userinfo_modifyPW);
//        logoutBt = (Button) findViewById(R.id.lingji_userinfo_logout);
//    }
//
//    private void initData() {
//
//        mUserController.loadUserAvatar(new OnDataCallBack<Bitmap>() {
//
//            @Override
//            public void onCallBack(Bitmap result) {
//                if (isFinishing())
//                    return;
//                if (result != null)
//                    userIcon.setImageBitmap(result);
//                else
//                    userIcon.setImageResource(R.drawable.lingji_default_head);
//            }
//        });
//        mDataTimePopup = new LunarDataTimePopupWindow(getActivity(),
//                onDataSetListener);
//
//        userName.setText(mUserInfo.getName());
//        userId.setText(mUserInfo.getUserId());
//
//        int year = mUserInfo.getYear();
//        int month = mUserInfo.getMonth();
//        int day = mUserInfo.getDay();
//        int hour = mUserInfo.getHour();
//        verifyemail = mUserInfo.getVerifyemail();
//        String birthTime = getString(R.string.lingji_modifyinfo_time_format,
//                year, month, day, hour);
//        userBrith.setText(birthTime);
//
//        int sex = mUserInfo.getSex();
//        if (sex == 0) {
//            userSex.setText(R.string.lingji_community_rank_male);
//        } else if (sex == 1) {
//            userSex.setText(R.string.lingji_community_rank_famale);
//        }
//        int work = mUserInfo.getWork();
//        if (work == 0) {
//            userWork.setText(R.string.lingji_modifyinfo_worked);
//        } else if (work == 1) {
//            userWork.setText(R.string.lingji_modifyinfo_working);
//        } else if (work == 2) {
//            userWork.setText(R.string.lingji_modifyinfo_worked2);
//        } else if (work == 3) {
//            userWork.setText(R.string.lingji_modifyinfo_worked3);
//        } else if (work == 4) {
//            userWork.setText(R.string.lingji_modifyinfo_worked4);
//        } else if (work == 5) {
//            userWork.setText(R.string.lingji_modifyinfo_worked5);
//        } else if (work == 6) {
//            userWork.setText(R.string.lingji_modifyinfo_worked6);
//        } else if (work == 7) {
//            userWork.setText(R.string.lingji_modifyinfo_worked7);
//        }
//
//        int marry = mUserInfo.getLove();
//        if (marry == 0) {
//            userMarry.setText(R.string.lingji_community_merray);
//        } else if (marry == 1) {
//            userMarry.setText(R.string.lingji_community_single);
//        } else if (marry == 2) {
//            userMarry.setText(R.string.lingji_community_loving);
//        } else if (marry == 3) {
//            userMarry.setText(R.string.lingji_community_norecord);
//        }
//        email = mUserInfo.getEmail();
//        if (Util.isEmpty(email) || email.equals("null")) {
//            userEmail.setText(getString(R.string.lingji_userinfo_bindemail));
//            userEmail.setFocusable(true);
//        } else {
//            if (verifyemail == 0) {
//                userEmail.setText(getString(R.string.lingji_userinfo_bindemail) + email);
//                userEmail.setTextColor(getResources().getColor(R.color.lingji_userinfo_text1));
//            } else {
//                userEmail.setText(email);
//            }
//            userEmail.setFocusable(false);
//        }
//        iconLayout.setOnClickListener(this);
//        nameLayout.setOnClickListener(this);
//        workLayout.setOnClickListener(this);
//        sexLayout.setOnClickListener(this);
//        marryLayout.setOnClickListener(this);
//        modifyPW.setOnClickListener(this);
//        logoutBt.setOnClickListener(this);
//        userBrith.setOnClickListener(this);
//        if (UserController.getInstance().getThirdParty()) {
//            modifyPW.setVisibility(View.GONE);
//            findViewById(R.id.lingji_userinfo_userid).setVisibility(View.GONE);
//        }
//        if (verifyemail == 0) {
//            emailLayout.setOnClickListener(this);
//        } else {
//            emailLayout.setClickable(false);
//        }
//    }
//
//    public LunarDateTimeView.OnDateSetListener onDataSetListener = new LunarDateTimeView.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(LunarDateTimeView picker, int type, int year,
//                              int monthOfYear, int dayOfMonth, int hour, String date) {
//            mCalendar.set(year, monthOfYear - 1, dayOfMonth, hour, 0);
//            String birthday = String.valueOf(mCalendar.getTimeInMillis() / 1000);
//            L.d("birthday", "birthday:--------------" + birthday);
//            requestManager.RequestModifyUserInfo(getActivity(), mUserController.getUserId(), mUserController.getUserPassword(),
//                    mUserInfo.getName(), "" + mUserInfo.getSex(), "" + mUserInfo.getWork(), "" + mUserInfo.getLove(), birthday, new MyHttpListener());
//            if (mUserInfo.getSex() == 0) {
//                MobclickAgent.onEvent(getActivity(), "每日运势分析", year + "年-男");
//            } else if (mUserInfo.getSex() == 1) {
//                MobclickAgent.onEvent(getActivity(), "每日运势分析", year + "年-女");
//            }
//        }
//
//    };
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.lingji_userinfo_modifyPW) {
//            Intent i = new Intent(UserInfoActivity.this, ChangeUserPWActivity.class);
//            startActivity(i);
//        } else if (v.getId() == R.id.lingji_userinfo_icon_layout) {
//            editHead();
//        } else if (v.getId() == R.id.lingji_userinfo_name_layout) {
//            Intent i = new Intent(UserInfoActivity.this, ModifyUserActivity.class);
//            i.putExtra(TYPE, NAME);
//            startActivity(i);
//        } else if (v.getId() == R.id.lingji_userinfo_sex_layout) {
//            ModifySex();
//        } else if (v.getId() == R.id.lingji_userinfo_work_layout) {
//            ModifyWork();
//        } else if (v.getId() == R.id.lingji_userinfo_marriage_layout) {
//            ModifyMarry();
//        } else if (v.getId() == R.id.lingji_userinfo_email_layout) {
//            Intent i = new Intent(UserInfoActivity.this, EmailCheckActivity.class);
//            startActivity(i);
//        } else if (v.getId() == R.id.lingji_userinfo_logout) {
//            mUserController.logout();
//            clearData();
//            finish();
//        } else if (v.getId() == R.id.lingji_userinfo_date) {
//            mDataTimePopup.showAtLocation(getActivity().getWindow()
//                            .getDecorView(),
//                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//            return;
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb1) {
//            updateWork(0);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb2) {
//            updateWork(1);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb3) {
//            updateWork(2);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb4) {
//            updateWork(3);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb5) {
//            updateWork(4);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb6) {
//            updateWork(5);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb7) {
//            updateWork(6);
//        } else if (v.getId() == R.id.lingji_userinfo_dialog_rb8) {
//            updateWork(7);
//        } else if (v.getId() == R.id.lingji_userinfo_lovedialog_rb1) {
//            updateMarriage(0);
//        } else if (v.getId() == R.id.lingji_userinfo_lovedialog_rb2) {
//            updateMarriage(1);
//        } else if (v.getId() == R.id.lingji_userinfo_lovedialog_rb3) {
//            updateMarriage(2);
//        } else if (v.getId() == R.id.lingji_userinfo_lovedialog_rb4) {
//            updateMarriage(3);
//        }
//    }
//
//    /**
//     * 用户退出的时候清除一些数据
//     */
//    private void clearData() {
//        // 未支付的订单数据
//        BaseDataEntity entity = new BaseDataEntity();
//        entity.setKey(Constants.LINGJI_KEY_NOPAY_ORDER);
//        entity.setValue("");
//        entity.setIsfirst(false);
//        DbUtil.saveBaseData(entity);
//        // 支付的订单数据
//        entity = new BaseDataEntity();
//        entity.setKey(Constants.LINGJI_KEY_PAY_ORDER);
//        entity.setValue("");
//        entity.setIsfirst(false);
//        // 奖品与优惠券
//        entity = new BaseDataEntity();
//        entity.setKey(Constants.LINGJI_KEY_PRIZE_DATA);
//        entity.setValue("");
//        entity.setIsfirst(false);
//        entity = new BaseDataEntity();
//        // 星座数据
//        entity.setKey(Constants.LINGJI_KEY_XINGZUO_DAY);
//        entity.setValue("");
//        entity.setIsfirst(false);
//        //优惠券
//        BroadcastController.cancelNewYearPrizeChangeBroadcast(this);
//    }
//
//    @Override
//    protected void setupTopTitle(TextView textview) {
//        super.setupTopTitle(textview);
//        textview.setText(R.string.lingji_title_userinfo);
//    }
//
//    class UserBroadcast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (isFinishing())
//                return;
//            mUserInfo = mUserController.getLocalUserInfo();
//            if (mUserInfo == null)
//                return;
//            initData();
//            if (mUserInfo.checkUserInfo()) {
//                if (action != null && action.equals(ScoreTipsActivity.SCORETASK)) {
//                    MobclickAgent.onEvent(getActivity(), "我的灵机积分任务中点击完成完善个人资料次数", "未完善资料用户完善了资料的次数");
//                } else if (action != null && action.equals(YunchengAdapter.ACTION_YUNCHENG)) {
//                    MobclickAgent.onEvent(getActivity(), "任务卡点击次数", "用户成功完善个人资料的次数");
//                }
//                String channel = Settings.getSettings().getChannel();
//                final String password = UserController.getInstance().getUserPassword();
//                final String userId = UserController.getInstance().getUserId();
//                requestManager.RequestScoreHandle(getMMCApplication(), channel,
//                        userId, password, Constant.SCORE_ADDCODE_USERINFO, new HttpListener<String>() {
//                            @Override
//                            public void onResponse(HttpResponse httpResponse) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(String result) {
//                                BaseData data = BaseDataConvert.convert(result);
//                                if (data.getStatus() == BaseData.STATUS_SUCCESS) {
//                                    if (isFinishing()) {
//                                        return;
//                                    }
//                                    mUserController.loadUserInfo();
//                                    BroadcastController.sendUserDataChangeBroadcast(getActivity());
//                                    toast(R.string.lingji_addscroce_userinfo);
//                                }
//                            }
//
//                            @Override
//                            public void onError(HttpError httpError) {
//                                Toast.makeText(getActivity(), R.string.lingji_netword_unusual, Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onFinish() {
//                            }
//                        });
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mBroadcast != null) {
//            UserController.unregisterUserChanger(this, mBroadcast);
//        }
//    }
//
//    /**
//     * 编辑头像
//     */
//    protected void editHead() {
//        final AlertDialog searchDialog = new AlertDialog.Builder(this).create();
//        searchDialog.setTitle(R.string.lingji_modify_upload_file_title);
//        ListView listView = new ListView(getBaseContext());
//        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter
//                .createFromResource(getBaseContext(),
//                        R.array.LingJi_uploadFile_array,
//                        android.R.layout.simple_expandable_list_item_1);
//        listView.setAdapter(arrayAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int position, long arg3) {
//                if (position == 1) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, null);
//                    intent.setDataAndType(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            IMAGE_UNSPECIFIED);
//                    startActivityForResult(intent, PHOTOZOOM);
//                } else {
//                    mPhotoUri = Uri.fromFile(getOutputMediaFile());
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
//                    startActivityForResult(intent, PHOTOHRAPH);
//                }
//                searchDialog.cancel();
//            }
//        });
//        searchDialog.setView(listView);
//        searchDialog.show();
//    }
//
//    /**
//     * 修改用户性别
//     */
//    private void ModifySex() {
//        final MMCAlertDialog sexDialog = new MMCAlertDialog(getActivity());
//        sexDialog.setContentView(R.layout.lingji_modify_userinfo_sex_dialog);
//        final RadioButton maleRb = (RadioButton) sexDialog.findViewById(R.id.lingji_userinfo_dialog_rb1);
//        maleRb.setText(R.string.lingji_community_rank_male);
//        maleRb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                maleRb.setChecked(true);
//                requestManager.RequestModifyUserInfo(getActivity(), mUserController.getUserId(), mUserController.getUserPassword(),
//                        mUserInfo.getName(), "0", "" + mUserInfo.getWork(), "" + mUserInfo.getLove(), "", new MyHttpListener());
//                sexDialog.dismiss();
//            }
//        });
//        final RadioButton femaleRb = (RadioButton) sexDialog.findViewById(R.id.lingji_userinfo_dialog_rb2);
//        femaleRb.setText(R.string.lingji_community_rank_famale);
//        femaleRb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                femaleRb.setChecked(true);
//                requestManager.RequestModifyUserInfo(getActivity(), mUserController.getUserId(), mUserController.getUserPassword(),
//                        mUserInfo.getName(), "1", "" + mUserInfo.getWork(), "" + mUserInfo.getLove(), "", new MyHttpListener());
//                sexDialog.dismiss();
//            }
//        });
//        int sex = mUserInfo.getSex();
//        if (sex == 0) {
//            maleRb.setChecked(true);
//        } else {
//            femaleRb.setChecked(true);
//        }
//        sexDialog.show();
//    }
//
//    /**
//     * 修改用户工作
//     */
//    private void ModifyWork() {
//        workDialog = new MMCAlertDialog(getActivity());
//        workDialog.setContentView(R.layout.lingji_modify_userinfo_work_dalog);
//        RadioButton workedRb1 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb1);
//        RadioButton workingRb = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb2);
//        RadioButton workedRb2 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb3);
//        RadioButton workedRb3 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb4);
//        RadioButton workedRb4 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb5);
//        RadioButton workedRb5 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb6);
//        RadioButton workedRb6 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb7);
//        RadioButton workedRb7 = (RadioButton) workDialog.findViewById(R.id.lingji_userinfo_dialog_rb8);
//
//        workedRb1.setOnClickListener(this);
//        workedRb2.setOnClickListener(this);
//        workedRb3.setOnClickListener(this);
//        workedRb4.setOnClickListener(this);
//        workedRb5.setOnClickListener(this);
//        workedRb6.setOnClickListener(this);
//        workedRb7.setOnClickListener(this);
//        workingRb.setOnClickListener(this);
//        workDialog.show();
//
//        int work = mUserInfo.getWork();
//        if (work == 0) {
//            workedRb1.setChecked(true);
//        } else if (work == 1) {
//            workingRb.setChecked(true);
//        } else if (work == 2) {
//            workedRb2.setChecked(true);
//        } else if (work == 3) {
//            workedRb3.setChecked(true);
//        } else if (work == 4) {
//            workedRb4.setChecked(true);
//        } else if (work == 5) {
//            workedRb5.setChecked(true);
//        } else if (work == 6) {
//            workedRb6.setChecked(true);
//        } else if (work == 7) {
//            workedRb7.setChecked(true);
//        }
//    }
//
//    private void updateWork(int work) {
//        requestManager.RequestModifyUserInfo(getActivity(), mUserController.getUserId(), mUserController.getUserPassword(),
//                mUserInfo.getName(), "" + mUserInfo.getSex(), work + "", "" + mUserInfo.getLove(), "", new MyHttpListener());
//        if (workDialog != null) {
//            workDialog.dismiss();
//            workDialog = null;
//        }
//    }
//
//    /**
//     * 修改用户婚姻
//     */
//    private void ModifyMarry() {
//        marryDialog = new MMCAlertDialog(getActivity());
//        marryDialog.setContentView(R.layout.lingji_modify_userinfo_marriage_dialog);
//        RadioButton marryRb = (RadioButton) marryDialog.findViewById(R.id.lingji_userinfo_lovedialog_rb1);
//        RadioButton noMarryRb = (RadioButton) marryDialog.findViewById(R.id.lingji_userinfo_lovedialog_rb2);
//        RadioButton lovingRb = (RadioButton) marryDialog.findViewById(R.id.lingji_userinfo_lovedialog_rb3);
//        RadioButton norecordRb = (RadioButton) marryDialog.findViewById(R.id.lingji_userinfo_lovedialog_rb4);
//
//        marryRb.setOnClickListener(this);
//        noMarryRb.setOnClickListener(this);
//        lovingRb.setOnClickListener(this);
//        norecordRb.setOnClickListener(this);
//
//        int marry = mUserInfo.getLove();
//        if (marry == 0) {
//            marryRb.setChecked(true);
//        } else if (marry == 1) {
//            noMarryRb.setChecked(true);
//        } else if (marry == 2) {
//            lovingRb.setChecked(true);
//        } else if (marry == 3) {
//            norecordRb.setChecked(true);
//        }
//        marryDialog.show();
//    }
//
//    private void updateMarriage(int marriage) {
//        requestManager.RequestModifyUserInfo(getActivity(), mUserController.getUserId(), mUserController.getUserPassword(),
//                mUserInfo.getName(), "" + mUserInfo.getSex(), "" + mUserInfo.getWork(), marriage + "", "", new MyHttpListener());
//        if (LG.sDebug) {
//            LG.i("上传数据", mUserController.getUserId() + "" +
//                    mUserController.getUserPassword() + "" +
//                    mUserInfo.getName() + "" +
//                    mUserInfo.getSex() + "" +
//                    mUserInfo.getWork() + "" +
//                    marriage + "");
//        }
//        if (marryDialog != null) {
//            marryDialog.dismiss();
//            marryDialog = null;
//        }
//    }
//
//
//    /**
//     * 监听
//     */
//    protected class MyHttpListener extends LingjiHttpListener {
//
//        public MyHttpListener() {
//        }
//
//        @Override
//        public void onSuccess(String result) {
//            LG.i("修改成功:" + result);
//            BaseData data = BaseDataConvert.convert(result);
//            if (data.getStatus() == BaseData.STATUS_SUCCESS) {
//                mUserController.loadUserInfo();
//            } else {
//                Toast.makeText(getActivity(), R.string.lingji_netword_unusual, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public void onError(HttpError httpError) {
//            Toast.makeText(getActivity(), R.string.lingji_netword_unusual, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 上传头像
//     */
//    public void upLoadHead() {
//        mLoaderDialog.show();
//        NetWorkController.upLoadUsePic(mUserController.getUserId(),
//                mUserController.getUserPassword(), mFileHead,
//                onDataCallBackHead);
//    }
//
//    /**
//     * 上传头像的回调
//     */
//    public OnDataCallBack<BaseData> onDataCallBackHead = new OnDataCallBack<BaseData>() {
//
//        @Override
//        public void onCallBack(BaseData result) {
//            if (isFinishing()) {
//                return;
//            }
//            mLoaderDialog.dismiss();
//            if (result.getStatus() == BaseData.STATUS_SUCCESS) {
//                mUserController.loadUserInfo();
//            } else {
//                toast(R.string.lingji_user_modify_head_fail);
//            }
//        }
//    };
//
//    /**
//     * 获取输出图片文件
//     *
//     * @return
//     */
//    public static File getOutputMediaFile() {
//        File mediaStorageDir = null;
//        if (Util.hasFroyo()) {
//            mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        } else {
//            mediaStorageDir = Environment.getExternalStorageDirectory();
//        }
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
//                .format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");
//
//        return mediaFile;
//    }
//
//    /**
//     * 裁剪头像
//     */
//    public void startPhotoZoom(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 96);
//        intent.putExtra("outputY", 96);
//        intent.putExtra("return-data", true);
//
//        try {
//            startActivityForResult(intent, PHOTORESOULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // 拍照
//        if (requestCode == PHOTOHRAPH) {
//            // 设置文件保存路径这里放在跟目录下
//            startPhotoZoom(mPhotoUri);
//            return;
//        }
//
//        if (data == null) {
//            super.onActivityResult(requestCode, resultCode, data);
//            return;
//        }
//
//        if (requestCode == PHOTOZOOM) { // 读取相册缩放图片
//            startPhotoZoom(data.getData());
//        } else if (requestCode == PHOTORESOULT) { // 处理结果
//            Bundle extras = data.getExtras();
//            if (extras == null) {
//                return;
//            }
//            Bitmap userHeadPortrait = extras.getParcelable("data");
//            userIcon.setImageBitmap(userHeadPortrait);
//            File dir = SDCardCacheController.getTempDir();
//            File temp = new File(dir, UUID.randomUUID().toString() + ".jpg");
//            mFileHead = ImageLoader.saveBitmap(userHeadPortrait,
//                    temp.getAbsolutePath());
//            upLoadHead();
//        }
//    }
//}