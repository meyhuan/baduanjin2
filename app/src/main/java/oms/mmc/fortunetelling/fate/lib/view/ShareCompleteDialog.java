package oms.mmc.fortunetelling.fate.lib.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.helper.GlideRoundTransform;
import com.smartydroid.android.starter.kit.utilities.ACache;
import com.smartydroid.android.starter.kit.utilities.ImageUtils;
import com.smartydroid.android.starter.kit.utilities.SPUtils;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import me.drakeet.materialdialog.MaterialDialog;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import oms.mmc.fortunetelling.fate.lib.api.ErrorHelper;
import oms.mmc.fortunetelling.fate.lib.model.entity.BaseResponse;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;
import oms.mmc.fortunetelling.fate.lib.ui.RankActivity;
import oms.mmc.fortunetelling.fate.lib.ui.VideoFragment;
import oms.mmc.fortunetelling.fate.lib.user.Helper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Author: meyu
 * Date:   16/3/8
 * Email:  627655140@qq.com
 * <p/>
 * 视频完成显示分享对话框
 */
public class ShareCompleteDialog implements AccountManager.CurrentAccountObserver{

    String comTip;
    String goRank;
    String addTime;

    private TextView completeDialogCountTipTv;
    private TextView completeDialogTimeAddTv;
    private TextView completeDialogGoRankTv;
    private ImageView avatarImageView;
    private Context mContext;
    MaterialDialog materialDialog;
    private View rootView;

    String sharePath = "";
    //用户累计的锻炼时间
    private String mTotalTime;
    //用户锻炼的次数
    private int exerciseTime;

    public ShareCompleteDialog(Context context) {
        mContext = context;
        materialDialog = new MaterialDialog(context);
        rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_video_complete_share, null);
        comTip = context.getString(R.string.complete_dialog_count_tip);
        goRank = context.getString(R.string.complete_dialog_go_rank);
        addTime = context.getString(R.string.complete_dialog_add_time);
        initView(rootView);

        //获取保存的路径
        sharePath = ACache.get(context).getAsString("share_bitmap_path");
        if(TextUtils.isEmpty(sharePath)){
            //保存图片
            sharePath = ACache.get(context).put(
                    "share_bitmap", BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher)).getAbsolutePath();
            //保存图片路径
            ACache.get(context).put("share_bitmap_path", sharePath);
        }

        //注册一个用户信息改变回调的监听器
        AccountManager.registerObserver(this);
    }

    private void initView(View rootView) {
        completeDialogCountTipTv = (TextView) rootView.findViewById(R.id.complete_dialog_count_tip_tv);
        completeDialogTimeAddTv = (TextView) rootView.findViewById(R.id.complete_dialog_add_tv);
        completeDialogGoRankTv = (TextView) rootView.findViewById(R.id.complete_dialog_go_rank_tv);
        avatarImageView = (ImageView) rootView.findViewById(R.id.share_avatar_img);
        User mCurrentUser = AccountManager.getCurrentAccount();
        if(mCurrentUser != null){
            Glide.with(mContext).load(mCurrentUser.imgUrl).placeholder(R.drawable.rank_default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(mContext)).into(avatarImageView);
        }

        materialDialog.setPositiveButton(R.string.complete_dialog_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                if(Helper.isLogin(mContext, mContext.getString(R.string.not_login_tip1))){
                    showShare(false);
                }
                MobclickAgent.onEvent(mContext, "share_dialog_share");

            }
        });
        materialDialog.setNegativeButton(R.string.complete_dialog_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                MobclickAgent.onEvent(mContext, "share_dialog_cancel");
            }
        });
        completeDialogGoRankTv.setText(Html.fromHtml(goRank));
        materialDialog.setView(rootView);

        completeDialogGoRankTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isLogin(mContext, mContext.getString(R.string.not_login_tip2))) {
                    v.getContext().startActivity(new Intent(mContext, RankActivity.class));
                    materialDialog.dismiss();
                }
                MobclickAgent.onEvent(mContext, "share_dialog_rank");
            }
        });

        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AccountManager.unregisterObserver(ShareCompleteDialog.this);
            }
        });
        mTotalTime = ACache.get(mContext).getAsString(Constant.USER_PLAY_TIME);
        exerciseTime = (int) SPUtils.get(mContext,Constant.KEY_EXERCISE_TIME,0);
        exerciseTime = exerciseTime + 1;
        setAddTimeText(Integer.parseInt(TextUtils.isEmpty(mTotalTime) ? "0" : mTotalTime) / 60);
        setACompleteTip(exerciseTime);
    }

    /**
     * 上传用户上次锻炼的时间
     */
    private void upLoadMomentTime(){
        User user = AccountManager.getCurrentAccount();
        if(user != null && !TextUtils.isEmpty(mTotalTime) && !mTotalTime.equals("0")){
            Call<BaseResponse> call = ApiService.createUpLoadService().addTime(ApiService.APP_KEY, user.id, mTotalTime);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if(response.body().status.equals(ApiService.OK)){
                        //上传成功之后请 0
                        ACache.get(mContext).put(Constant.USER_PLAY_TIME, "0");
                        VideoFragment.PLAY_TIME = 0;
                    }else {
                        ErrorHelper.getInstance().handleErrCode(response.body().status);
                    }
                }
                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Utils.showToast(mContext, R.string.net_error);
                }
            });
        }
    }


    /**
     * 设置显示增加的时间数
     *
     * @param addTimeCount addTimeCount
     */
    public void setAddTimeText(int addTimeCount) {
        String addString = String.format(Locale.CHINA, addTime, addTimeCount);
        completeDialogTimeAddTv.setText(Html.fromHtml(addString));
    }


    /**
     * 设置显示完成的次数
     *
     * @param completeCount completeCount
     */
    public void setACompleteTip(int completeCount) {
        String addString = String.format(Locale.CHINA, comTip, completeCount);
        completeDialogCountTipTv.setText(Html.fromHtml(addString));
    }

    public void show(){
        MobclickAgent.onEvent(mContext, "share_dialog_show");
        materialDialog.show();
        SPUtils.put(mContext, Constant.KEY_EXERCISE_TIME, exerciseTime);
        upLoadMomentTime();
    }
    public void showShare(boolean isMain) {
        final String content;
        final String title;
        if(AccountManager.isLogin()){
            if(isMain){
                content = mContext.getString(R.string.share_content_tip);
                title = mContext.getString(R.string.app_name);
            }else {
                title = String.format(Locale.CHINA, mContext.getResources().getString(R.string.share_title_tip),exerciseTime);
                content = mContext.getString(R.string.share_content_tip);
            }
        }else {
            return;
        }

        ShareSDK.initSDK(mContext);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(getCnShareUrl(mContext.getPackageName()));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(sharePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(getCnShareUrl(mContext.getPackageName()));
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("很不错哦");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(getCnShareUrl(mContext.getPackageName()));
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_more);
        oks.setCustomerLogo(bitmap, mContext.getResources().getString(R.string.share_more),
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "share_channel_more");
                shareMsg(mContext.getString(R.string.share),mContext.getString(R.string.app_name),
                        content + getCnShareUrl(mContext.getPackageName()), "");
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Utils.showToast(mContext, "onComplete");
                MobclickAgent.onEvent(mContext, "share_channel_"+platform.getName());
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
//                Utils.showToast(mContext, "onError");
            }

            @Override
            public void onCancel(Platform platform, int i) {
//                Utils.showToast(mContext, "onCancel");
                MobclickAgent.onEvent(mContext, "share_channel_"+platform.getName());
            }
        });
        // 启动分享GUI
        oks.show(mContext);
    }

    @Override
    public void userInfoNotifyChange() {
        //等获取到用户信息时候再显示图片
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                User mCurrentUser = AccountManager.getCurrentAccount();
                if(mCurrentUser != null){
                    Glide.with(mContext).load(mCurrentUser.imgUrl).placeholder(R.drawable.rank_default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new GlideRoundTransform(mContext)).into(avatarImageView);
                }
            }
        }, 1500);

    }

    public static String getCnShareUrl(String packName) {
        String url = "http://a.app.qq.com/o/simple.jsp?pkgname=%1$s&g_f=991653";
        return String.format(url, new Object[]{packName});
    }

    /**
     * 分享功能
     *
     * @param activityTitle
     *            Activity的名字
     * @param msgTitle
     *            消息标题
     * @param msgText
     *            消息内容
     * @param imgPath
     *            图片路径，不分享图片则传null
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/*");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, activityTitle));
    }
}
