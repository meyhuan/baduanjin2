//package sample.baselib.meyhuan.com.sharesdk;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import java.util.HashMap;
//
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.tpl.OnLoginListener;
//import cn.sharesdk.tpl.SignupPage;
//import cn.sharesdk.tpl.UserInfo;
//import cn.sharesdk.wechat.friends.Wechat;
//import cn.smssdk.SMSSDK;
//
//public class ThridLoginFragment extends Fragment implements View.OnClickListener, Handler.Callback, PlatformActionListener {
//
//
//    private LinearLayout loginWxLayout;
//    private LinearLayout loginQqLayout;
//    private LinearLayout loginSinaLayout;
//
//    // 填写从短信SDK应用后台注册得到的APPKEY
//    private static String APPKEY = "27fe7909f8e8";
//    // 填写从短信SDK应用后台注册得到的APPSECRET
//    private static String APPSECRET = "3c5264e7e05b8860a9b98b34506cfa6e";
//
//    private static final int MSG_SMSSDK_CALLBACK = 1;
//    private static final int MSG_AUTH_CANCEL = 2;
//    private static final int MSG_AUTH_ERROR= 3;
//    private static final int MSG_AUTH_COMPLETE = 4;
//
//    private OnLoginListener signupListener;
//    private Handler handler;
//    //短信验证的对话框
//    private Dialog msgLoginDlg;
//
//    private Activity activity;
//
//    private void assignViews(View view) {
//        loginWxLayout = (LinearLayout) view.findViewById(R.id.login_wx_layout);
//        loginQqLayout = (LinearLayout) view.findViewById(R.id.login_qq_layout);
//        loginSinaLayout = (LinearLayout) view.findViewById(R.id.login_sina_layout);
//
//        loginWxLayout.setOnClickListener(this);
//        loginQqLayout.setOnClickListener(this);
//        loginSinaLayout.setOnClickListener(this);
//    }
//
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.activity = activity;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // 初始化ui
//        handler = new Handler(this);
//        initSDK(activity);
//
//        signupListener = new OnLoginListener() {
//            @Override
//            public boolean onSignin(String platform, HashMap<String, Object> res) {
//                return true;
//            }
//
//            @Override
//            public boolean onSignUp(UserInfo info) {
//                return true;
//            }
//        };
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_thrid_login, container, false);
//        assignViews(view);
//        return view;
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if(id == R.id.login_wx_layout){
//            //新浪微博
//            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//            authorize(wechat);
//        }else if(id == R.id.login_qq_layout){
//            Platform qq = ShareSDK.getPlatform(QQ.NAME);
//            authorize(qq);
//        }else if(id == R.id.login_sina_layout){
//            Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//            authorize(sina);
//        }
//    }
//
//    //执行授权,获取用户信息
//    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
//    private void authorize(Platform plat) {
//        if (plat == null) {
//            return;
//        }
//
//        //关闭SSO授权
////        plat.SSOSetting(false);
//        plat.setPlatformActionListener(this);
//        plat.authorize();
//    }
//
//    /** 设置授权回调，用于判断是否进入注册 */
//    public void setOnLoginListener(OnLoginListener l) {
//        this.signupListener = l;
//    }
//
//
//    private void initSDK(Context context) {
//        //初始化sharesdk,具体集成步骤请看文档：
//        //http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
//        ShareSDK.initSDK(context);
//
//        //短信验证初始化，具体集成步骤看集成文档：
//        //http://wiki.mob.com/Android_%E7%9F%AD%E4%BF%A1SDK%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3
//        SMSSDK.initSDK(context, APPKEY, APPSECRET);
//    }
//
//    @Override
//    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            Message msg = new Message();
//            msg.what = MSG_AUTH_COMPLETE;
//            msg.obj = new Object[] {platform.getName(), res};
//            handler.sendMessage(msg);
//
//        }
//        Log.i("Tag", res.toString());
//        Toast.makeText(getContext(), res.toString(), Toast.LENGTH_LONG).show();
//    }
//    @Override
//    public void onError(Platform platform, int action, Throwable t) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            handler.sendEmptyMessage(MSG_AUTH_ERROR);
//        }
//        t.printStackTrace();
//    }
//    @Override
//    public void onCancel(Platform platform, int action) {
//        if (action == Platform.ACTION_USER_INFOR) {
//            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public boolean handleMessage(Message msg) {
//        switch(msg.what) {
//            case MSG_AUTH_CANCEL: {
//                //取消授权
//                Toast.makeText(activity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
//            } break;
//            case MSG_AUTH_ERROR: {
//                //授权失败
//                Toast.makeText(activity, R.string.auth_error, Toast.LENGTH_SHORT).show();
//            } break;
//            case MSG_AUTH_COMPLETE: {
//                //授权成功
//                Toast.makeText(activity, R.string.auth_complete, Toast.LENGTH_SHORT).show();
//                Object[] objs = (Object[]) msg.obj;
//                String platform = (String) objs[0];
//                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
//
//                if (signupListener != null && signupListener.onSignin(platform, res)) {
//                    SignupPage signupPage = new SignupPage();
//                    signupPage.setOnLoginListener(signupListener);
//                    signupPage.setPlatform(platform);
//                    signupPage.show(activity, null);
//                }
//            } break;
//            case MSG_SMSSDK_CALLBACK: {
//                if (msg.arg2 == SMSSDK.RESULT_ERROR) {
//                    Toast.makeText(activity, "操作失败", Toast.LENGTH_SHORT).show();
//                } else {
//                    switch (msg.arg1) {
//                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
//                            if(msgLoginDlg != null && msgLoginDlg.isShowing()){
//                                msgLoginDlg.dismiss();
//                            }
//                            Toast.makeText(activity, "提交验证码成功", Toast.LENGTH_SHORT).show();
//                            Message m = new Message();
//                            m.what = MSG_AUTH_COMPLETE;
//                            m.obj = new Object[] {"SMSSDK", (HashMap<String, Object>) msg.obj};
//                            handler.sendMessage(m);
//                        } break;
//                        case SMSSDK.EVENT_GET_VERIFICATION_CODE:{
//                            Toast.makeText(activity, "验证码已经发送", Toast.LENGTH_SHORT).show();
//                        } break;
//                    }
//                }
//            } break;
//        }
//        return false;
//    }
//
////    04-04 00:04:36.732 6259-6568/? I/Tag: {verified_source_url=, location=广东 广州, remark=, block_app=0, verified_type=-1, verified_reason=, statuses_count=9, verified_source=, lang=zh-cn, city=1, credit_score=80, id=2370978633, verified_trade=, following=false, favourites_count=1, idstr=2370978633, description=, verified=false, name=mey_环, province=44, domain=, gender=m, created_at=Tue May 28 12:26:12 +0800 2013, user_ability=0, weihao=, followers_count=41, online_status=0, profile_url=u/2370978633, bi_followers_count=4, geo_enabled=true, status={pic_urls=[], textLength=4, visible={type=0, list_id=0}, attitudes_count=0, isLongText=false, darwin_tags=[], in_reply_to_screen_name=, mlevel=0, source_type=1, truncated=false, userType=0, id=3957224210747281, idstr=3957224210747281, in_reply_to_status_id=, created_at=Sat Mar 26 10:27:16 +0800 2016, reposts_count=0, text=null, comments_count=0, source_allowclick=0, text_tag_tips=[], hot_weibo_tags=[], source=<a href="http://app.weibo.com/t/feed/2voKi6" rel="nofollow">未通过审核应用</a>, favorited=false, biz_feature=0, in_reply_to_user_id=, mid=3957224210747281}, star=0, class=1, urank=6, allow_all_comment=true, avatar_hd=http://tva4.sinaimg.cn/crop.160.0.1600.1600.1024/8d524349jw8e8kled4oqxj21hc18gka1.jpg, mbrank=0, allow_all_act_msg=false, url=, avatar_large=http://tp2.sinaimg.cn/2370978633/180/5674001470/1, pagefriends_count=0, friends_count=48, verified_reason_url=, screen_name=mey_环, mbtype=0, profile_image_url=http://tp2.sinaimg.cn/2370978633/50/5674001470/1, follow_me=false, block_word=0, ptype=0}
////        04-04 00:04:47.772 6259-6653/? I/Tag: {ret=0, is_yellow_year_vip=0, figureurl_qq_1=http://q.qlogo.cn/qqapp/100371282/0B2312CFEA102372B540D26915C18BF1/40, nickname=Meyu, figureurl_qq_2=http://q.qlogo.cn/qqapp/100371282/0B2312CFEA102372B540D26915C18BF1/100, yellow_vip_level=0, is_lost=0, msg=, city=咸宁, figureurl_1=http://qzapp.qlogo.cn/qzapp/100371282/0B2312CFEA102372B540D26915C18BF1/50, vip=0, figureurl_2=http://qzapp.qlogo.cn/qzapp/100371282/0B2312CFEA102372B540D26915C18BF1/100, level=0, province=湖北, gender=男, is_yellow_vip=0, figureurl=http://qzapp.qlogo.cn/qzapp/100371282/0B2312CFEA102372B540D26915C18BF1/30}
//
//    }
