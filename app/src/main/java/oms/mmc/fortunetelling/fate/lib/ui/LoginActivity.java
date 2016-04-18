package oms.mmc.fortunetelling.fate.lib.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import com.smartydroid.android.starter.kit.helper.StatusBarHelper;
import com.smartydroid.android.starter.kit.utilities.ACache;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.api.ErrorHelper;
import oms.mmc.fortunetelling.fate.lib.model.entity.UserInfo;
import oms.mmc.fortunetelling.fate.lib.model.entity.UserSimpleInfo;
import oms.mmc.fortunetelling.fate.lib.utils.Md5Tool;
import oms.mmc.fortunetelling.fate.lib.model.entity.BaseResponse;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends StarterNetworkActivity<BaseResponse> implements View.OnClickListener{



    private LinearLayout loginWxLayout;
    private LinearLayout loginQqLayout;
    private LinearLayout loginSinaLayout;

    private TextInputLayout containerRegisterUserName;
    private TextInputEditText editRegiserUserName;
    private TextInputLayout containerRegisterPassword;
    private TextInputEditText editRegiserPassword;
    private Button loginConfirmRegisterBtn;
    private TextView loginPhoneRegisterBtn;
    private TextView loginForgetPasswordBtn;

    private String mUserName;
    private String mUserPW;


    String loadingString;
    String loginSuccessString;
    String loginFailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAddToolBar = false;
        setContentView(R.layout.activity_login);
        /** init auth api**/
        weiBoInit();

    }

    @Override
    protected void setupViews() {
        loadingString = getString(R.string.loading);
        loginSuccessString = getString(R.string.login_success);
        loginFailString = getString(R.string.login_fail);

        containerRegisterUserName = (TextInputLayout) findViewById(R.id.container_register_user_name);
        editRegiserUserName = (TextInputEditText) findViewById(R.id.edit_regiser_user_name);
        containerRegisterPassword = (TextInputLayout) findViewById(R.id.container_register_password);
        editRegiserPassword = (TextInputEditText) findViewById(R.id.edit_regiser_password);
        loginPhoneRegisterBtn = (TextView) findViewById(R.id.login_phone_register_btn);
        loginConfirmRegisterBtn = (Button) findViewById(R.id.login_confirm_register_btn);
        loginForgetPasswordBtn = (TextView) findViewById(R.id.login_forget_password_btn);
        loginWxLayout = (LinearLayout) findViewById(R.id.login_wx_layout);
        loginQqLayout = (LinearLayout) findViewById(R.id.login_qq_layout);
        loginSinaLayout = (LinearLayout) findViewById(R.id.login_sina_layout);

        loginWxLayout.setOnClickListener(this);
        loginQqLayout.setOnClickListener(this);
        loginSinaLayout.setOnClickListener(this);

        loginForgetPasswordBtn.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );   //下划线
        loginForgetPasswordBtn.getPaint().setAntiAlias(true);//抗锯齿

        loginPhoneRegisterBtn.setOnClickListener(this);
        loginConfirmRegisterBtn.setOnClickListener(this);
        loginForgetPasswordBtn.setOnClickListener(this);

        mUserName = ACache.get(this).getAsString(ApiService.USERNAME);
        editRegiserUserName.setText(mUserName);
        addThirdLoginFragment();
    }

    private void addThirdLoginFragment(){
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.login_third_layout, new ThridLoginFragment())
//                .commit();
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_phone_register_btn){
            startActivity(RegisterByPhoneActivity.class);
            MobclickAgent.onEvent(mContext, "login_register");
        }else if(view.getId() == R.id.login_confirm_register_btn){
            doLogin();
            MobclickAgent.onEvent(mContext, "login_login");
        }else if(view.getId() == R.id.login_forget_password_btn){
            startActivity(ForgetPWActivity.class);
            MobclickAgent.onEvent(mContext, "login_forget");
        }else if(view.getId() == R.id.login_wx_layout){
            loginWithWeixin();
            MobclickAgent.onEvent(mContext, "login_wx");
        }else if(view.getId() == R.id.login_qq_layout){

        }else if(view.getId() == R.id.login_sina_layout){
            authorze();
            MobclickAgent.onEvent(mContext, "login_wb");
        }
    }

    private boolean validInfo(){
        mUserName = editRegiserUserName.getText().toString();
        mUserPW = editRegiserPassword.getText().toString();

        if(TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mUserPW)){
            return false;
        }
        return true;
    }


    private void doLogin(){
        if(validInfo()){
            Call<BaseResponse> userCall = ApiService.createAuthService().login(ApiService.APP_KEY, mUserName, Md5Tool.encryptPW(mUserPW));
            networkQueue().enqueue(userCall);
        }else {
            Utils.showToast(mContext,getResources().getString(R.string.register_input_empty_error));
        }
    }

    /**
     * 网络请求开始回调成功
     */
    @Override
    public void respondSuccess(BaseResponse data) {
        if(data.status.equals(ApiService.OK)){
            Utils.showToast(this,getString(R.string.login_success));
            //保存加密后的密码
            ACache.get(this).put(ApiService.PASSWORD,  Md5Tool.encryptPW(mUserPW));
            ACache.get(this).put(ApiService.USERNAME,  mUserName);
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constant.EXTRA_FROM_LOGIN_ACTIVITY, true);
            ((App)getApplication()).startMainActivity(bundle);
            finish();
        }else {
            ErrorHelper.getInstance().handleErrCode(data.status);
        }
    }




    @Override
    protected void setStatusBar() {
        StatusBarHelper.setColor(this, getResources().getColor(R.color.nav_status_bar_color));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(App.resp != null){
            authToken(App.resp);
            App.resp = null;
        }
    }

    /****************************************微博登陆****************************************************/

    /**
     * 微博 Web 授权类，提供登陆等功能
     */
    private WeiboAuth mWeiboAuth;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     * App Key：335875226
     App Secret：049af61b8756824006d7b1f9e3dc0e32
     */
    private SsoHandler mSsoHandler;
    String APP_KEY      = "335875226";
    String REDIRECT_URL = "http://www.sina.com";
    String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    private void weiBoInit(){
        // 创建微博实例
        mWeiboAuth = new WeiboAuth(this, APP_KEY, REDIRECT_URL, SCOPE);
    }

    private void authorze(){
        mSsoHandler = new SsoHandler(this, mWeiboAuth);
        mSsoHandler.authorize(new AuthListener());
    }


    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                LogUtils.d(mAccessToken.getToken() + ":" + mAccessToken.getUid());
                Toast.makeText(mContext,
                        "success", Toast.LENGTH_SHORT).show();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = getString(R.string.register_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(mContext,
                    R.string.cancel, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mContext,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /****************************************微博登陆****************************************************/








    /****************************************微信登陆****************************************************/
    public final String TAG = "WXEntryActivity";
    public final String WEIXIN_APP_ID = "wx47a0cd9e335fbedd";
    public final String WEIXIN_APP_SECRET = "688c35c29babc02e671003a14fc22b4d";

    private IWXAPI mWeixinAPI;

    /**
     * 发起登录
     */
    private void loginWithWeixin() {
        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, false);
        }
        if (!mWeixinAPI.isWXAppInstalled()) {
            Utils.showToast(this,R.string.un_stall_wx);
            return;
        }
        mWeixinAPI.registerApp(WEIXIN_APP_ID);

        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWeixinAPI.sendReq(req);
    }

    //获取token
    private void authToken(final BaseResp resp) {
        showUnBackProgressLoading(R.string.loading);
        Log.e("tag", "---ErrCode:" + resp.errCode);
        final String code = ((SendAuth.Resp) resp).code;
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + WEIXIN_APP_ID + "&secret="
                + WEIXIN_APP_SECRET + "&code=" + code
                + "&grant_type=authorization_code";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(this, tokenUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("Tag",response.toString());
                String accessToken = "";
                String openId = "";
                try {
                    accessToken = response.getString("access_token");
                    openId = response.getString("openid");
                    getUserInfo(accessToken,openId);
                    Log.i("Tag",accessToken +":"+openId);
                } catch (Exception e) {
                    dismissUnBackProgressLoading();
                    Utils.showToast(mContext, R.string.json_format_error);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dismissUnBackProgressLoading();
                Utils.showToast(mContext, R.string.net_error);
            }
        });
    }

    /**
     * 向微信获取用户的信息
     * @param accessToken accessToken
     * @param openId openId
     */
    private void getUserInfo(String accessToken, String openId){
        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + accessToken + "&openid=" + openId;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(this, infoUrl, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "onSuccess: "+response.toString());
                try {
                    String openId = response.getString("openid");
                    String name = response.getString("nickname");
                    String imgUrl = response.getString("headimgurl");
                    getUserInfoByWX(openId, name, imgUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utils.showToast(mContext, R.string.json_format_error);
                    showUnBackProgressLoading(R.string.loading);
                    dismissUnBackProgressLoading();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Utils.showToast(mContext, R.string.net_error);
                dismissUnBackProgressLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


    /**
     * 获取服务器用户信息（微信）
     * @param openId openId
     * @param name name
     */
    private void getUserInfoByWX(String openId, String name, String imgUrl){
        Call<UserSimpleInfo> call = ApiService.createAuthService().joinWithWX(
                ApiService.APP_KEY,openId, name, imgUrl);
        call.enqueue(new Callback<UserSimpleInfo>() {
            @Override
            public void onResponse(Call<UserSimpleInfo> call, Response<UserSimpleInfo> response) {
                if(response.body().status.equals(ApiService.OK)){
                    LogUtils.i(response.body().content.toString());
                    saveUserInfo(response.body().content.userId, response.body().content.password);
                }else {
                    ErrorHelper.getInstance().handleErrCode(response.body().status);
                }
                dismissUnBackProgressLoading();
            }

            @Override
            public void onFailure(Call<UserSimpleInfo> call, Throwable t) {
                Utils.showToast(mContext, R.string.net_error);
                dismissUnBackProgressLoading();
            }
        });
    }

    private void saveUserInfo(String userName, String userPw){
        Utils.showToast(this,getString(R.string.login_success));
        ACache.get(this).put(ApiService.PASSWORD,  userPw);
        ACache.get(this).put(ApiService.USERNAME,  userName);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.EXTRA_FROM_LOGIN_ACTIVITY, true);
        ((App)getApplication()).startMainActivity(bundle);
        finish();
    }

//    {
//            "sex": 1,
//            "nickname": "meyu",
//            "unionid": "o9ze7sygKo5S9wPY5x122eBCCsU4",
//            "privilege": ["chinaunicom"],
//            "province": "Guangdong",
//            "openid": "ot-Nnv_ajBLsC4KXvw5zh6_FQx98",
//            "language": "zh_CN",
//            "headimgurl": "http://wx.qlogo.cn/mmopen/Q3auHgzwzM4ds0nnQWTBYotL1DN9upv9GxgSVA7uPUiah6SPYI3ibU9TLrjzNHOVCcrxwuXRnrk1rdJicGMPvbNXHkmiaCeUu9icbghpxDUIrGjw/0",
//            "country": "CN",
//            "city": "Guangzhou"
//    }


    /****************************************微信登陆****************************************************/


}
