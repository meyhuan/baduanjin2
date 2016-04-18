package oms.mmc.fortunetelling.fate.lib.ui;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import com.smartydroid.android.starter.kit.helper.StatusBarHelper;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.utils.Md5Tool;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;

public class ForgetPWActivity extends StarterNetworkActivity<User> implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextInputLayout containerLoginPhone;
    private TextInputEditText editLoginPhone;
    private TextInputLayout containerLoginValidCode;
    private TextInputEditText editLoginValidCode;
    private TextInputLayout containerLoginPassword;
    private TextInputLayout containerLoginPasswordAgain;
    private TextInputEditText editLoginPasswordAgain;
    private TextInputEditText editLoginPassword;
    private CheckBox registerShowPwCheckBox;
    private Button registerConfirmBtn;
    private ImageView registerBackBtn;
    private TextView textValidCode;

    private String pwString;
    private String pwString2;
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
        setContentView(R.layout.activity_forget_pw);
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
                if(s.length() >= 6){
                    containerLoginPassword.setErrorEnabled(false);
                }else {
                    containerLoginPassword.setErrorEnabled(true);
                    containerLoginPassword.setError(getString(R.string.register_input_pw_error));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        editLoginPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 6){
                    containerLoginPasswordAgain.setErrorEnabled(false);
                }else {
                    containerLoginPasswordAgain.setErrorEnabled(true);
                    containerLoginPasswordAgain.setError(getString(R.string.register_input_pw_error));
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
        pwString2 = editLoginPassword.getText().toString();

        //输入不能为空
        if(TextUtils.isEmpty(phoneString) || TextUtils.isEmpty(vavidString) || TextUtils.isEmpty(pwString)|| TextUtils.isEmpty(pwString2)){
            showToastShort(R.string.register_input_empty_error);
            return false;
        }else if(!isMobileNO(phoneString)){     //手机号为11位的数字
            showToastShort(R.string.register_input_phone_error);
            return false;
        }else if(vavidString.length() != 4 || !vavidString.matches("\\d*")){
            showToastShort(R.string.register_input_valid_error);
            return false;
        }else if(pwString.length() < 6){
            showToastShort(R.string.register_input_pw_error);
            return false;
        }else if(pwString2.length() < 6 || !pwString.equals(pwString2)){
            showToastShort(R.string.register_input_valid_error2);
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
                    //提交验证码成功
                   changePW();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Toast.makeText(mContext, getResources().getString(R.string.register_send_success), Toast.LENGTH_SHORT).show();
                    countDownHandler.post(runnable);
                    textValidCode.setEnabled(false);
                    hasSendCode = true;
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
            message.obj = data;
            message.arg1 = event;
            message.arg2 = result;
            validCodeHandler.sendMessage(message);
            dismissUnBackProgressLoading();
        }
    };


    /**
     * 向服务器发送修改密码请求
     */
    private void changePW(){

        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("appkey","");
        requestParams.put("userId",phoneString);
        requestParams.put("userPW",Md5Tool.encryptPW(pwString));
        httpClient.post(this, ApiService.API_ENDPOINT + ApiService.API_FORGET_PW,requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(response.getString("status").equals(ApiService.OK)){
                        Utils.showToast(mContext,getString(R.string.forget_pw_success));
                        startActivity(LoginActivity.class);
                        finish();
                    }else {
                        Utils.showToast(mContext,getString(R.string.forget_pw_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(mContext,getString(R.string.forget_pw_fail));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Utils.showToast(mContext,getString(R.string.forget_pw_fail));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissUnBackProgressLoading();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.register_confirm_btn){
            MobclickAgent.onEvent(mContext, "forget_confirm");
            if(checkInput()){
                validCode(vavidString);
            }
        }else if(view.getId() == R.id.text_valid_code){
            MobclickAgent.onEvent(mContext, "forget_code");
            if(!validPhone()){return;}
            getValidCode();
            hasSendCode = false;
        }else if(view.getId() == R.id.register_upload_avatar_layout){


        }else if(view.getId() == R.id.register_back_btn){
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //显示密码
            editLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editLoginPasswordAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else {
            //隐藏密码
            editLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editLoginPasswordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
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
        containerLoginPasswordAgain = (TextInputLayout) findViewById(R.id.container_login_password_again);
        editLoginPasswordAgain = (TextInputEditText) findViewById(R.id.edit_login_password_again);
        registerShowPwCheckBox = (CheckBox) findViewById(R.id.register_show_pw_check_box);
        registerConfirmBtn = (Button) findViewById(R.id.register_confirm_btn);
        registerBackBtn = (ImageView) findViewById(R.id.register_back_btn);
        textValidCode = (TextView) findViewById(R.id.text_valid_code);

        registerShowPwCheckBox.setOnCheckedChangeListener(this);
        registerConfirmBtn.setOnClickListener(this);
        registerBackBtn.setOnClickListener(this);
        textValidCode.setOnClickListener(this);
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
