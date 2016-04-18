package oms.mmc.fortunetelling.fate.lib.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarm;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarms;
import oms.mmc.fortunetelling.fate.lib.view.DigitalClock;

/**
 * https://github.com/cmc00022/easyrecycleradapters
 */
public class NotificationActivity extends StarterActivity implements View.OnClickListener{

    String am;
    String pm;

    private RecyclerView notifyItemRecycleView;
    private FloatingActionButton fab;
    private TextView mEmptyTipView;

    private Cursor mCursor;


    private HomeAdapter mAdapter;
    private List<Alarm> mDatas;
    //保存每一个item的Switcher选中状态（以免出现错位的情况）
    List<Boolean> mIsSelected = new ArrayList<>();

    //当前选中的item位置
    private int currentPosition = 0;
    //标记点击的操作是否为添加的操作
    public final static int REQUEST_CODE_ADD = 100;
    //标记点击的操作是否为修改的操作
    public final static int REQUEST_CODE_MODIFY = 201;
    private boolean selected[] = {true, true, true, true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    @Override
    protected void setupViews() {
        am = getString(R.string.notify_meridiem_ante);
        pm = getString(R.string.notify_meridiem_psot);
        mEmptyTipView = ViewUtils.getView(this, R.id.notify_empty_tip);
        notifyItemRecycleView = (RecyclerView) findViewById(R.id.notify_item_recycle_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mDatas = new ArrayList<>();
        notifyItemRecycleView.setLayoutManager(new LinearLayoutManager(this));
        notifyItemRecycleView.setAdapter(mAdapter = new HomeAdapter());
        //获取闹钟的cursor
        mCursor = Alarms.getAlarmsCursor(getContentResolver());
        if(mCursor != null && mCursor.getCount() != 0){
            for (int i = 0; i < mCursor.getCount(); i++) {
                mCursor.moveToPosition(i);
                Alarm notification = new Alarm(mCursor);
                mIsSelected.add(notification.enabled);
                mDatas.add(notification);
            }
            mAdapter.notifyDataSetChanged();
            mEmptyTipView.setVisibility(View.GONE);
        }else {
            Utils.showToast(this, R.string.not_set_alarm);
            mEmptyTipView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_notify);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab){
            if(mDatas.size() <= 9){
                if(mDatas.size() == 0){
                    Utils.showToast(this, getString(R.string.notify_add_first_tip));
                }
                Intent intent = new Intent(mContext, NotificationModifyActivity.class);
                intent.putExtra(Constant.EXTRA_NOTIFY_ADD, true);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }else if(mDatas.size() > 9) {
                Utils.showToast(this, getString(R.string.notify_more_ten_item_tip));
            }
            MobclickAgent.onEvent(this, "notify_add");
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.item_notify_layout, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Alarm alarm = mDatas.get(position);
            // set the alarm text
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, alarm.hour);
            c.set(Calendar.MINUTE, alarm.minutes);
            holder.digitalClock.setLive(false);
            holder.digitalClock.updateTime(c);
            holder.notifyItemDay.setText(alarm.daysOfWeek.toString(mContext, false));

            holder.notifyItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIsSelected.remove(position);
                    mIsSelected.add(position, isChecked);
                    setIsSelected(mIsSelected);
                    alarm.enabled = isChecked;
                    Alarms.setAlarm(mContext,alarm);
                }
            });
            boolean temp = getIsSelected().get(position);
            //从mIsSelected中获取Switcher的选中状态
            holder.notifyItemSwitch.setChecked(temp);
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NotificationModifyActivity.class);
                    intent.putExtra(Constant.EXTRA_NOTIFY_NOTIFICATION, alarm);
                    startActivityForResult(intent, REQUEST_CODE_MODIFY);
                    currentPosition = position;
                }
            });

        }

        @Override
        public int getItemCount() {
            if(mDatas.size() > 0 ){
                mEmptyTipView.setVisibility(View.GONE);
            }else {
                mEmptyTipView.setVisibility(View.VISIBLE);
            }
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            DigitalClock digitalClock;
            TextView notifyItemDay;
            SwitchButton notifyItemSwitch;
            View rootView;

            public MyViewHolder(View view) {
                super(view);
                digitalClock = ViewUtils.getView(view, R.id.notify_item_time);
                notifyItemDay = ViewUtils.getView(view, R.id.notify_item_day);
                notifyItemSwitch = ViewUtils.getView(view, R.id.notify_item_switch);
                rootView = ViewUtils.getView(view, R.id.notify_item_root_view);
            }
        }

        public List<Boolean> getIsSelected() {
            return mIsSelected;
        }

        public void setIsSelected(List<Boolean> isSelected) {
            mIsSelected = isSelected;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            //判断是否为修改操作
            if(requestCode == REQUEST_CODE_MODIFY){
                //判断是否为删除操作
                if (data.getBooleanExtra(Constant.EXTRA_NOTIFY_DELETE, false)) {
                    mDatas.remove(currentPosition);
                    mIsSelected.remove(currentPosition);
                }else if(data.getBooleanExtra(Constant.EXTRA_NOTIFY_CONFIRM, false)){ // 判断是否为修改操作
                    //将原来位置的数据替换
                    mDatas.remove(currentPosition);
                    mDatas.add(currentPosition, (Alarm) data.getParcelableExtra(Constant.EXTRA_NOTIFY_NOTIFICATION));
                }
            }else if(requestCode == REQUEST_CODE_ADD && data.getBooleanExtra(Constant.EXTRA_NOTIFY_CONFIRM, false)){ //添加操作
                mDatas.add((Alarm) data.getParcelableExtra(Constant.EXTRA_NOTIFY_NOTIFICATION));
                mIsSelected.add(true);
            }
            mAdapter.notifyDataSetChanged();
        }

    }

}
