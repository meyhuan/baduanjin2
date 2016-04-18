package oms.mmc.fortunetelling.fate.lib.api.service;

import okhttp3.RequestBody;
import oms.mmc.fortunetelling.fate.lib.model.entity.BaseResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.RankInfo;
import oms.mmc.fortunetelling.fate.lib.model.entity.TimeInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2016/3/30.
 */
public interface UpLoadService {
    @Multipart
    @POST("MemberLogin_getUserPic")
    Call<BaseResponse> uploadAvatar(
            @Part("file\"; filename=\"pp.png\" ") RequestBody file ,
            @Part("appkey") RequestBody appkey,
            @Part("userId") RequestBody userId,
            @Part("userPW") RequestBody userPW,
            @Part("channel") RequestBody channel);


    /**
     * 获取锻炼时间
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @return Call
     */
    @FormUrlEncoded
    @POST("BaDuanJin_getTime")
    Call<TimeInfo> getTime(
            @Field("appkey") String appkey,
            @Field("userid") String userId);


    /**
     * 添加锻炼时间
     *
     * @param appkey 授权码
     * @param userId 用户登录名或邮箱或手机号
     * @return Call
     */
    @FormUrlEncoded
    @POST("BaDuanJin_addTime")
    Call<BaseResponse> addTime(
            @Field("appkey") String appkey,
            @Field("userid") String userId,
            @Field("time") String time);

    /**
     * 获取排行榜信息
     *
     * @param appkey 授权码
     * @param page 用户登录名或邮箱或手机号
     * @return Call
     */
    @FormUrlEncoded
    @POST("BaDuanJin_rankList")
    Call<RankInfo> getRankList(
            @Field("appkey") String appkey,
            @Field("page") String page,
            @Field("timestamp") String timestamp);

}
