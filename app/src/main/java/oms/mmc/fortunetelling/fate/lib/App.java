package oms.mmc.fortunetelling.fate.lib;

import android.content.Context;
import android.os.Bundle;

import com.danikula.videocache.HttpProxyCacheServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartydroid.android.starter.kit.StarterKit;
import com.smartydroid.android.starter.kit.account.Account;
import com.smartydroid.android.starter.kit.app.StarterKitApp;
import com.smartydroid.android.starter.kit.retrofit.RetrofitBuilder;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import java.io.IOException;

import okhttp3.logging.HttpLoggingInterceptor;
import oms.mmc.fortunetelling.fate.lib.utils.Util;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;

/**
 * Author: meyu
 * Date:   16/2/28
 * Email:  627655140@qq.com
 */
public class App extends StarterKitApp{
    //用户登录微信回调Activity传递到LoginActivity
    public static SendAuth.Resp  resp;
    @Override
    public void onCreate() {

        // common config
        new StarterKit.Builder()
                .setDebug(BuildConfig.DEBUG)
                .build();

        super.onCreate();
        //enabledStrictMode();
        //LeakCanary.install(this);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // init api service
        new RetrofitBuilder.Builder()
                .debug(BuildConfig.DEBUG)
                .baseUrl(ApiService.API_ENDPOINT)
                .addInterceptors(logging)
                .build();


    }

    //视频播放有关的代理 https://github.com/danikula/AndroidVideoCache
    private HttpProxyCacheServer proxy;
    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Util.getVideoCacheFile(this))
                .build();
    }

    public void startMainActivity(Bundle bundle){
    }

    public Object getMainActivity(){
        return null;
    }



    @Override
    public Account accountFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, User.class);
        } catch (IOException e) {
            // Nothing to do
        }
        return null;
    }

    @Override
    public String accept() {
        return null;
    }

//    //各个平台的配置，建议放在全局Application或者程序入口
//    {
//        //微信    wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
//        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
//        //豆瓣RENREN平台目前只能在服务器端配置
//        //新浪微博
//        PlatformConfig.setSinaWeibo("1555897818", "f1fc4455fb18b6183c70548368043090");
//        //易信
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//        PlatformConfig.setAlipay("2015111700822536");
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//        PlatformConfig.setPinterest("1439206");
//
//    }
}
