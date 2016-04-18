package oms.mmc.fortunetelling.fate.lib.api.service;

import oms.mmc.fortunetelling.fate.lib.model.entity.BaseResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.LoginResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.UserInfo;
import oms.mmc.fortunetelling.fate.lib.model.entity.UserSimpleInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/3/30.
 * 用户登录，修改信息接口
 */
public interface AuthService {

    /**
     * 登录接口
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @param userPW 用户密码
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberLogin_login")
    Call<BaseResponse> login(
            @Field("appkey") String appkey,
            @Field("userId") String userId,
            @Field("userPW") String userPW);

    /**
     * 注册接口
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @param userPW 用户密码
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberLogin_join")
    Call<LoginResponse> join(
            @Field("appkey") String appkey,
            @Field("userId") String userId,
            @Field("userPW") String userPW,
            @Field("channel") String channel,
            @Field("email") String email);

    /**
     * 获取用户信息接口
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberInfo_getUserinfo")
    Call<UserInfo> getUserInfo(
            @Field("appkey") String appkey,
            @Field("userId") String userId);

    /**
     * 修改用户信息（只要在这里添加需要修改的参数即可）
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @param userPW 用户密码
     * @param sex 用户性别
     * @param name 用户名称
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberInfo_userNew")
    Call<BaseResponse> updateUserInfo(
            @Field("appkey") String appkey,
            @Field("userId") String userId,
            @Field("userPW") String userPW,
            @Field("sex") String sex,
            @Field("name") String name);

    /**
     * 修改用户密码
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @param userPW 用户密码
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberInfo_newsChangePW")
    Call<BaseResponse> updateUserPW(
            @Field("appkey") String appkey,
            @Field("userId") String userId,
            @Field("userPW") String userPW);


    /**
     * 微博登陆
     *
     * @param appkey 授权码
     * @param secret 用户密码
     * @param token 用户性别
     * @param weiboid 用户名称
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberLogin_weiboLogin")
    Call<UserInfo> joinWithWb(
            @Field("appkey") String appkey,
            @Field("secret") String secret,
            @Field("token") String token,
            @Field("weiboid") String weiboid);


    /**
     * 微信登陆
     *
     * @param appkey 授权码
     * @param openid 用户密码
     * @param name 用户性别
     * @return Call
     */
    @FormUrlEncoded
    @POST("MemberLogin_webChatLogin")
    Call<UserSimpleInfo> joinWithWX(
            @Field("appkey") String appkey,
            @Field("openid") String openid,
            @Field("name") String name,
            @Field("imgUrl") String imgUrl);

}
