package oms.mmc.fortunetelling.fate.lib.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smartydroid.android.starter.kit.account.AccountManager;
import com.smartydroid.android.starter.kit.app.StarterNetworkActivity;
import com.smartydroid.android.starter.kit.helper.GlideRoundTransform;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.api.ErrorHelper;
import oms.mmc.fortunetelling.fate.lib.model.entity.Rank;
import oms.mmc.fortunetelling.fate.lib.model.entity.RankInfo;
import oms.mmc.fortunetelling.fate.lib.model.entity.TimeInfo;
import oms.mmc.fortunetelling.fate.lib.model.entity.User;
import oms.mmc.fortunetelling.fate.lib.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankActivity extends StarterNetworkActivity<RankInfo> {

    private ImageView rankMyAvatarImg;
    private TextView rankMyNicknameTv;
    private ImageView rankMyHeartImg;
    private TextView rankMyTotalTimeTv;
    private TextView rankMyRankingNumTv;
    private RecyclerView rankRecyclerView;
    private RankRecyclerViewAdapt mAdapt;

    private List<Rank> mData = new ArrayList<>();
    private User mCurrentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_rank);
    }

    @Override
    protected void setupViews() {
        assignViews();
        rankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapt = new RankRecyclerViewAdapt();
        rankRecyclerView.setAdapter(mAdapt);
        mCurrentUser = AccountManager.getCurrentAccount();
        if(mCurrentUser != null){
            Glide.with(mContext).load(mCurrentUser.imgUrl).placeholder(R.drawable.rank_default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(mContext)).into(rankMyAvatarImg);
            rankMyNicknameTv.setText(mCurrentUser.name);

            getRankInfo ();
            getMyTime();

        }
    }

    /**
     * 获取我的锻炼时间 以及 排名信息
     */
    private void getMyTime(){
        Call<TimeInfo> call = ApiService.createUpLoadService().getTime(ApiService.APP_KEY, mCurrentUser.id);
        call.enqueue(new Callback<TimeInfo>() {
            @Override
            public void onResponse(Call<TimeInfo> call, Response<TimeInfo> response) {
                if(response.body().status.equals(ApiService.OK)){
                    rankMyTotalTimeTv.setText(repalseTime(response.body().content.timestring));
                    rankMyRankingNumTv.setText(Html.fromHtml(String.format(Locale.CHINA,
                            getString(R.string.my_rank), response.body().content.top)));
                }else {
                    ErrorHelper.getInstance().handleErrCode(response.body().status);
                }
            }

            @Override
            public void onFailure(Call<TimeInfo> call, Throwable t) {
                Utils.showToast(mContext, R.string.net_error);
            }
        });
    }

    /**
     * 获取排名列表信息
     */
    private void getRankInfo (){
        long time = Calendar.getInstance().getTimeInMillis() / 1000;
        Call<RankInfo> call = ApiService.createUpLoadService().getRankList(ApiService.APP_KEY, String.valueOf(1), String.valueOf(time));
        networkQueue().enqueue(call);
    }


    @Override
    public void startRequest() {
        super.startRequest();
        showUnBackProgressLoading(R.string.loading);
    }

    @Override
    public void respondSuccess(RankInfo data) {
        super.respondSuccess(data);
        if(data.status.equals(ApiService.OK)){
            mData = data.content;
            mAdapt.notifyDataSetChanged();
        }else {
            Utils.showToast(this, getResources().getString(R.string.login_get_user_info_fail));
            dismissUnBackProgressLoading();
        }
    }

    @Override
    public void endRequest() {
        super.endRequest();
    }

    @Override
    public void respondWithError(Throwable t) {
        super.respondWithError(t);
    }

    /**
     * 10小时25分  换成 600分钟
     * @param timeString timeString
     * @return 600分钟
     */
    private String repalseTime(String timeString){
        String minute = getString(R.string.minute);
        LogUtils.d(timeString);
        String newT = "10"+minute;
        if(!TextUtils.isEmpty(timeString)) {
            if (timeString.contains("小时")) {
                newT = timeString.replace("小时", ":").replace("分钟", ":") + "0";
            } else if (timeString.contains("分钟")) {
                newT = "0:" + timeString.replace("分钟", ":") + "0";
            }
            try {
                String tims []  = newT.split(":");
                LogUtils.d(timeString + "; " + Arrays.toString(tims));
                int t = Integer.parseInt(tims[0].trim()) * 60 +  Integer.parseInt(tims[1].trim());
               return t + minute;
            }catch (Exception e){
                e.printStackTrace();
                return newT;
            }
        }
        return newT;
    }

    class RankRecyclerViewAdapt extends RecyclerView.Adapter<RankViewHolder>{

        @Override
        public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_rank_layout, parent, false);
            return new RankViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(RankViewHolder holder, int position) {
            Rank rank = mData.get(position);
            setRankText(holder.rankingNumTv, position+1);
            holder.timeTotalTv.setText(repalseTime(rank.totaltime));
            Glide.with(mContext)
                    .load(rank.avatar)
                    .placeholder(R.drawable.rank_default_avatar)
                    .error(R.drawable.rank_default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new GlideRoundTransform(mContext))
                    .into(holder.avatarImg);
            if(TextUtils.isEmpty(rank.username)){
                holder.nicknameTv.setText(User.DEFAULT_NAME+rank.user_id);
            }else {
                holder.nicknameTv.setText(rank.username);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        private void setRankText (TextView text , int ranking){
            if(ranking == 1){
                //每个都需要添加  text.setText(""); 不然会错位
                text.setText("");
                text.setBackgroundResource(R.drawable.rank_first);
            }else if(ranking == 2){
                text.setBackgroundResource(R.drawable.rank_second);
                text.setText("");
            }else if(ranking == 3){
                text.setBackgroundResource(R.drawable.rank_thrid);
                text.setText("");
            }else {
                text.setText(ranking+"");
                text.setBackgroundResource(R.drawable.rank_second_transparent);
            }
        }
    }

    class RankViewHolder extends RecyclerView.ViewHolder{

        private TextView rankingNumTv;
        private ImageView avatarImg;
        private TextView nicknameTv;
        private TextView timeTotalTv;

        public RankViewHolder(View itemView) {
            super(itemView);
            rankingNumTv = ViewUtils.getView(itemView, R.id.rank_ranking_num_tv);
            avatarImg = ViewUtils.getView(itemView, R.id.rank_avatar_img);
            nicknameTv = ViewUtils.getView(itemView, R.id.rank_nickname_tv);
            timeTotalTv = ViewUtils.getView(itemView, R.id.rank_total_time_tv);
        }
    }


    private void assignViews() {
        rankMyAvatarImg = (ImageView) findViewById(R.id.rank_my_avatar_img);
        rankMyNicknameTv = (TextView) findViewById(R.id.rank_my_nickname_tv);
        rankMyHeartImg = (ImageView) findViewById(R.id.rank_my_heart_img);
        rankMyTotalTimeTv = (TextView) findViewById(R.id.rank_my_total_time_tv);
        rankMyRankingNumTv = (TextView) findViewById(R.id.rank_my_ranking_num_tv);
        rankRecyclerView = (RecyclerView) findViewById(R.id.rank_recycler_view);
    }

}
