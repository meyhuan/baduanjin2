package oms.mmc.fortunetelling.fate.lib.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.ACache;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import oms.mmc.fortunetelling.fate.lib.R;

public class EditShareActivity extends StarterActivity implements View.OnClickListener{

    String compketeTimes;

    private ImageView editSharePhotoSelectImg;
    private TextView editShareContentTv;
    private TextView editShareRankingTv;
    private ImageView editShareImg;
    private TextView editShareCompleteTimes;
    private Button editShareBtn;
    String sharePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_share);
        ShareSDK.initSDK(this);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_edit_share);
    }

    @Override
    protected void setupViews() {
        compketeTimes = getString(R.string.edit_share_complete_times);
        editSharePhotoSelectImg = (ImageView) findViewById(R.id.edit_share_photo_select_img);
        editShareContentTv = (TextView) findViewById(R.id.edit_share_content_tv);
        editShareRankingTv = (TextView) findViewById(R.id.edit_share_ranking_tv);
        editShareImg = (ImageView) findViewById(R.id.edit_share_img);
        editShareCompleteTimes = (TextView) findViewById(R.id.edit_share_complete_times);
        editShareBtn = (Button) findViewById(R.id.edit_share_btn);

        editShareCompleteTimes.setText(Html.fromHtml(String.format(compketeTimes,100)));

        editSharePhotoSelectImg.setOnClickListener(this);
        editShareBtn.setOnClickListener(this);
        //获取保存的路径
        sharePath = ACache.get(this).getAsString("share_bitmap_path");

        if(TextUtils.isEmpty(sharePath)){
            //保存图片
            sharePath = ACache.get(this).put(
                    "share_bitmap",BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).getAbsolutePath();
            //保存图片路径
            ACache.get(this).put("share_bitmap_path", sharePath);
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.edit_share_photo_select_img){

        }else if(view.getId() == R.id.edit_share_btn){
            showShare();
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getString(R.string.alarm_default_label));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(sharePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
// 启动分享GUI
        oks.show(this);
    }




}
