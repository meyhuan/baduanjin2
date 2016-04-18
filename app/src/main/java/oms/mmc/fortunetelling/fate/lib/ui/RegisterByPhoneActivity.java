package oms.mmc.fortunetelling.fate.lib.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import com.smartydroid.android.starter.kit.helper.StatusBarHelper;
import com.smartydroid.android.starter.kit.utilities.ACache;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;
import me.drakeet.materialdialog.MaterialDialog;
import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.api.ErrorHelper;
import oms.mmc.fortunetelling.fate.lib.utils.Md5Tool;
import oms.mmc.fortunetelling.fate.lib.model.entity.LoginResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterByPhoneActivity extends StarterNetworkActivity<User> implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    public static final String ACACHE_AVARAT_KEY= "acache_avarat_key";
    private Uri mPhotoUri;
    private User user;

    private TextInputLayout containerLoginPhone;
    private TextInputEditText editLoginPhone;
    private TextInputLayout containerLoginValidCode;
    private TextInputEditText editLoginValidCode;
    private TextInputLayout containerLoginPassword;
    private TextInputEditText editLoginPassword;
    private CheckBox registerShowPwCheckBox;
    private Button registerConfirmBtn;
    private ImageView registerBackBtn;
    private ImageView registerAvatarImg;
    private LinearLayout registerUploadAvatarImg;
    private TextView textValidCode;

    private String pwString;
    private String phoneString;
    private String vavidString;

    Handler countDownHandler = new Handler();
    private static final int TIME = 1000;
    private static final int MAX_VALUES = 60;

    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddToolBar = false;
        setContentView(R.layout.activity_register);
        SMSSDK.initSDK(this, Constant.MOB_APPKEY, Constant.MOB_APP_SERCRET);
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void setupViews() {
        assignViews();

        editLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isMobileNO(s.toString())){
                    containerLoginPhone.setErrorEnabled(false);
                }else {
                    containerLoginPhone.setErrorEnabled(true);
                    containerLoginPhone.setError(getString(R.string.register_input_phone_error));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        editLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() >= 6){
                    containerLoginPassword.setErrorEnabled(false);
                }else {
                    containerLoginPassword.setErrorEnabled(true);
                    containerLoginPassword.setError(getString(R.string.register_input_pw_error));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        runnable = new Runnable() {
            int t = MAX_VALUES;
            String again = getString(R.string.register_again);
            String able = getString(R.string.register_send_valid_code);
            @Override
            public void run() {
                if(t-- > 0){
                    countDownHandler.postDelayed(this, TIME);
                    textValidCode.setText(again+t);
                }else {
                    textValidCode.setText(able);
                    textValidCode.setEnabled(true);
                    t = MAX_VALUES;
                }
            }
        };

    }

    /**
     * 检查输入的信息是否合法
     * @return true 合法； false 不合法
     */
    private boolean checkInput(){
        phoneString = editLoginPhone.getText().toString();
        vavidString = editLoginValidCode.getText().toString();
        pwString = editLoginPassword.getText().toString();

        //输入不能为空
        if(TextUtils.isEmpty(phoneString) || TextUtils.isEmpty(vavidString) || TextUtils.isEmpty(pwString)){
            showToastShort(R.string.register_input_empty_error);
            return false;
        }else if(!isMobileNO(phoneString)){     //手机号为11位的数字
            return false;
        }else if(vavidString.length() != 4 || !vavidString.matches("\\d*")){
            showToastShort(R.string.register_input_valid_error);
            return false;
        }else if(pwString.length() < 6){
            showToastShort(R.string.register_input_pw_error);
            return false;
        }else {
            return true;
        }
    }

    private boolean validPhone(){
        phoneString = editLoginPhone.getText().toString();
        if(TextUtils.isEmpty(phoneString) || !isMobileNO(phoneString)){
            showToastShort(R.string.register_input_phone_error);
            return false;
        }else {
            return true;
        }
    }

    /**
     * 向服务器验证验证码是否正确
     * @param code
     */
    private void validCode(String code){
        SMSSDK.submitVerificationCode("86",phoneString, code);
        showUnBackProgressLoading(R.string.loading);
    }

    /**
     * 向服务器获取验证码
     */
    private void getValidCode(){

        SMSSDK.getVerificationCode("86", phoneString );
        showUnBackProgressLoading(R.string.loading);
    }
    private boolean hasSendCode = false;
    Handler validCodeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                    register();
                    asyncRegister();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Toast.makeText(mContext, getResources().getString(R.string.register_send_success), Toast.LENGTH_SHORT).show();
                    countDownHandler.post(runnable);
                    textValidCode.setEnabled(false);
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                if(hasSendCode){
                    Toast.makeText(mContext, getResources().getString(R.string.register_code_error), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, getResources().getString(R.string.register_send_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    EventHandler eventHandler = new EventHandler(){

        @Override
        public void afterEvent(int event, int result, Object data) {
            Message message = new Message();
            message.arg1 = event;
            message.arg2 = result;
            validCodeHandler.sendMessage(message);
            dismissUnBackProgressLoading();
            if(result == SMSSDK.RESULT_COMPLETE){
            }else {
                ((Throwable) data).printStackTrace();
            }
        }
    };


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.register_confirm_btn){
            MobclickAgent.onEvent(mContext, "register_confirm");
            if(checkInput()){
                validCode(vavidString);
//                register();
//                asyncRegister();
            }
        }else if(view.getId() == R.id.text_valid_code){
            MobclickAgent.onEvent(mContext, "register_code");
            if(!validPhone()){return;}
            getValidCode();
        }else if(view.getId() == R.id.register_upload_avatar_layout){
            showAvatarSelectDialog();
        }else if(view.getId() == R.id.register_back_btn){
            finish();
        }
    }

    private void asyncRegister(){
        RequestParams requestParams = new RequestParams();
        requestParams.put("appkey",ApiService.APP_KEY);
        requestParams.put("userId",phoneString);
        requestParams.put("userPW",pwString);
        requestParams.put("pwString","app_android");
        requestParams.put("email","6625522@mmclik.com");
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(this,"http://test.api.linghit.com/api/MemberLogin_join", requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    int status = response.getInt("status");
                    if(status == 1){
                        saveInfo();
                    }else {
                        ErrorHelper.getInstance().handleErrCode(status + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(mContext,getString(R.string.register_failed));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Utils.showToast(mContext,getString(R.string.net_error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissUnBackProgressLoading();
            }
        });
    }

    private void saveInfo(){
        Utils.showToast(mContext,getString(R.string.register_success));
        //保存加密后的密码
        ACache.get(mContext).put(ApiService.PASSWORD,  Md5Tool.encryptPW(pwString));
        ACache.get(mContext).put(ApiService.USERNAME,  phoneString);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.EXTRA_FROM_LOGIN_ACTIVITY, true);
        ((App)getApplication()).startMainActivity(bundle);
        finish();
    }

    /**
     * 向服务器发送注册请求
     */
    private void register(){
        Call<LoginResponse> call = ApiService.createAuthService().join(
                ApiService.APP_KEY, phoneString, pwString, "app","6625522@mmclik.com");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body().status.equals(ApiService.OK)){
                   saveInfo();
                }else {
                    Utils.showToast(mContext,getString(R.string.register_failed));
                }
                dismissUnBackProgressLoading();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissUnBackProgressLoading();
                Utils.showToast(mContext,getString(R.string.register_failed));
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //显示密码
            editLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else {
            //隐藏密码
            editLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * 显示头像选择对话框
     */
    private void showAvatarSelectDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_avatar_select_view, null);
        ViewUtils.getView(view, R.id.setting_photo_form_take_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoUri = Uri.fromFile(getOutputMediaFile());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(intent, PHOTOHRAPH);
                materialDialog.dismiss();
            }
        });
        ViewUtils.getView(view, R.id.setting_photo_form_album_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_UNSPECIFIED);
                startActivityForResult(intent, PHOTOZOOM);
                materialDialog.dismiss();
            }
        });
        materialDialog.setView(view);
        materialDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            // 设置文件保存路径这里放在跟目录下
            startPhotoZoom(mPhotoUri);
            return;
        }

        if (data == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == PHOTOZOOM) { // 读取相册缩放图片
            startPhotoZoom(data.getData());
        } else if (requestCode == PHOTORESOULT) { // 处理结果
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            Bitmap userHeadPortrait = extras.getParcelable("data");
            registerAvatarImg.setImageBitmap(userHeadPortrait);
            ACache.get(this).put(ACACHE_AVARAT_KEY,userHeadPortrait);
//            upLoadHead();
        }
    }


    /**
     * 裁剪头像
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 96);
        intent.putExtra("outputY", 96);
        intent.putExtra("return-data", true);

        try {
            startActivityForResult(intent, PHOTORESOULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片缓存的路径
     *
     * @return File
     */
    private File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                .format(new Date());
        File mediaFile = new File(ACache.getExternalCacheDir(this).getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownHandler.removeCallbacks(runnable);
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private void assignViews() {
        containerLoginPhone = (TextInputLayout) findViewById(R.id.container_login_phone);
        editLoginPhone = (TextInputEditText) findViewById(R.id.edit_login_phone);
        containerLoginValidCode = (TextInputLayout) findViewById(R.id.container_login_valid_code);
        editLoginValidCode = (TextInputEditText) findViewById(R.id.edit_login_valid_code);
        containerLoginPassword = (TextInputLayout) findViewById(R.id.container_login_password);
        editLoginPassword = (TextInputEditText) findViewById(R.id.edit_login_password);
        registerShowPwCheckBox = (CheckBox) findViewById(R.id.register_show_pw_check_box);
        registerConfirmBtn = (Button) findViewById(R.id.register_confirm_btn);
        registerBackBtn = (ImageView) findViewById(R.id.register_back_btn);
        registerAvatarImg = (ImageView) findViewById(R.id.nav_head_view_avatar_img);
        registerUploadAvatarImg = (LinearLayout) findViewById(R.id.register_upload_avatar_layout);
        textValidCode = (TextView) findViewById(R.id.text_valid_code);

        registerShowPwCheckBox.setOnCheckedChangeListener(this);
        registerConfirmBtn.setOnClickListener(this);
        registerBackBtn.setOnClickListener(this);
        textValidCode.setOnClickListener(this);
        registerUploadAvatarImg.setOnClickListener(this);
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        StatusBarHelper.setColor(this, getResources().getColor(R.color.nav_status_bar_color));
    }

    /**
     * 验证手机格式
     */
    public boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }


}
