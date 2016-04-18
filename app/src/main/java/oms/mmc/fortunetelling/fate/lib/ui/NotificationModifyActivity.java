package oms.mmc.fortunetelling.fate.lib.ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.apkfuns.logutils.LogUtils;
import com.smartydroid.android.starter.kit.app.StarterActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarm;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarms;
import oms.mmc.fortunetelling.fate.lib.view.DigitalClock;
import oms.mmc.fortunetelling.fate.lib.view.TimePickDialog;

/**
 * compile 'me.drakeet.materialdialog:library:1.2.8'
 * https://github.com/drakeet/MaterialDialog
 */
public class NotificationModifyActivity extends StarterActivity
        implements TimePickDialog.TimePickerConfirmListener ,TimePickerDialog.OnTimeSetListener
        ,CompoundButton.OnCheckedChangeListener, View.OnClickListener{
    private RelativeLayout notifyModTimeLayout;
    private DigitalClock notifyItemTime;
    private TextView amPm;
    private TextView timeDisplay;
    private CheckBox notifyModDayCheck;
    private LinearLayout notifyDaysLayout;
    private FrameLayout notifyDay1;
    private TextView notifyDayTv1;
    private FrameLayout notifyDay2;
    private TextView notifyDayTv2;
    private FrameLayout notifyDay3;
    private TextView notifyDayTv3;
    private FrameLayout notifyDay4;
    private TextView notifyDayTv4;
    private FrameLayout notifyDay5;
    private TextView notifyDayTv5;
    private FrameLayout notifyDay6;
    private TextView notifyDayTv6;
    private FrameLayout notifyDay7;
    private TextView notifyDayTv7;




    private Alarm currentNotification;
    private boolean isAddAction = false;

    /**
     * 每一个星期控件的id
     */
    private int[] ids = {R.id.notify_day_tv_1, R.id.notify_day_tv_2,
            R.id.notify_day_tv_3, R.id.notify_day_tv_4, R.id.notify_day_tv_5, R.id.notify_day_tv_6, R.id.notify_day_tv_7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_modify);
        isAddAction = getIntent().getBooleanExtra(Constant.EXTRA_NOTIFY_ADD, false);
    }

    @Override
    protected void setupViews() {

        assignViews();

        currentNotification = getIntent().getParcelableExtra(Constant.EXTRA_NOTIFY_NOTIFICATION);
        if (currentNotification == null) {
            currentNotification = new Alarm();
        }

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, currentNotification.hour);
        c.set(Calendar.MINUTE, currentNotification.minutes);
        //设置不可自动更新
        notifyItemTime.setLive(false);
        notifyItemTime.updateTime(c);
        initDays(currentNotification);
        notifyDaysLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        notifyDaysLayout.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        currentNotification.enabled = isChecked;
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.activity_name_notify_modify);
    }

    private void initDays(Alarm alarm){
        boolean [] arr = alarm.daysOfWeek.getBooleanArray();
        int len = arr.length;
        for(int i  = 0; i< len; i ++){
            initSelect(i, arr[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notify_modify, menu);
        if (isAddAction) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            toolbar.setTitle(R.string.activity_name_notify_add);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up material_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent();
        if (id == R.id.action_confirm) {
            MobclickAgent.onEvent(this, "notify_confirm");
            intent.putExtra(Constant.EXTRA_NOTIFY_CONFIRM, true);
            intent.putExtra(Constant.EXTRA_NOTIFY_NOTIFICATION, currentNotification);
            currentNotification.enabled = true;
            if(isAddAction){
                Alarms.addAlarm(mContext, currentNotification);
            }else {
                Alarms.setAlarm(mContext, currentNotification);
            }
            LogUtils.d("tag:"+currentNotification.daysOfWeek.getCoded()+":"+currentNotification.minutes);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (id == R.id.action_delete) {
            MobclickAgent.onEvent(this, "notify_delete");
            Alarms.deleteAlarm(mContext, currentNotification.id);
            intent.putExtra(Constant.EXTRA_NOTIFY_DELETE, true);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.notify_mod_time_layout){
            showTimePicker();
        }else if(view.getId() == R.id.notify_day_1){
            toggle(0);
        }else if(view.getId() == R.id.notify_day_2){
            toggle(1);
        }else if(view.getId() == R.id.notify_day_3){
            toggle(2);
        }else if(view.getId() == R.id.notify_day_4){
            toggle(3);
        }else if(view.getId() == R.id.notify_day_5){
            toggle(4);
        }else if(view.getId() == R.id.notify_day_6){
            toggle(5);
        }else if(view.getId() == R.id.notify_day_7){
            toggle(6);
        }

    }

    private void showTimePicker() {
        new TimePickerDialog(this, this, currentNotification.hour, currentNotification.minutes,
                DateFormat.is24HourFormat(this)).show();
    }


    private void toggle(int index){
        boolean s = currentNotification.daysOfWeek.isSet(index);
        s = !s;
        currentNotification.daysOfWeek.set(index, s);
        initSelect(index, s);
    }

    /**
     * 设置点击选择状态的改变
     * @param index 第几个（星期几）被选中
     * @param isSelect 设置是否选中
     */
    private void initSelect(int index, boolean isSelect) {
        int colorWhite = getResources().getColor(R.color.white);
        int colorPrimary = getResources().getColor(R.color.colorPrimary);
        Drawable drawable = getResources().getDrawable(R.drawable.primary_round_bg);
        if (isSelect) {
            ((TextView) findViewById(ids[index])).setTextColor(colorWhite);
            findViewById(ids[index]).setBackgroundDrawable(drawable);
        } else {
            ((TextView) findViewById(ids[index])).setTextColor(colorPrimary);
            findViewById(ids[index]).setBackgroundColor(colorWhite);
        }
    }

    @Override
    public void confirm(Alarm notification) {
        if (notification == null) {
            return;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        currentNotification.hour = hourOfDay;
        currentNotification.minutes = minute;
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        notifyItemTime.updateTime(c);
    }

    private void assignViews() {
        notifyModTimeLayout = (RelativeLayout) findViewById(R.id.notify_mod_time_layout);
        notifyItemTime = (DigitalClock) findViewById(R.id.notify_item_time);
        amPm = (TextView) findViewById(R.id.am_pm);
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        notifyModDayCheck = (CheckBox) findViewById(R.id.notify_mod_day_check);
        notifyDaysLayout = (LinearLayout) findViewById(R.id.notify_days_layout);
        notifyDay1 = (FrameLayout) findViewById(R.id.notify_day_1);
        notifyDayTv1 = (TextView) findViewById(R.id.notify_day_tv_1);
        notifyDay2 = (FrameLayout) findViewById(R.id.notify_day_2);
        notifyDayTv2 = (TextView) findViewById(R.id.notify_day_tv_2);
        notifyDay3 = (FrameLayout) findViewById(R.id.notify_day_3);
        notifyDayTv3 = (TextView) findViewById(R.id.notify_day_tv_3);
        notifyDay4 = (FrameLayout) findViewById(R.id.notify_day_4);
        notifyDayTv4 = (TextView) findViewById(R.id.notify_day_tv_4);
        notifyDay5 = (FrameLayout) findViewById(R.id.notify_day_5);
        notifyDayTv5 = (TextView) findViewById(R.id.notify_day_tv_5);
        notifyDay6 = (FrameLayout) findViewById(R.id.notify_day_6);
        notifyDayTv6 = (TextView) findViewById(R.id.notify_day_tv_6);
        notifyDay7 = (FrameLayout) findViewById(R.id.notify_day_7);
        notifyDayTv7 = (TextView) findViewById(R.id.notify_day_tv_7);

        notifyDay1.setOnClickListener(this);
        notifyDay2.setOnClickListener(this);
        notifyDay3.setOnClickListener(this);
        notifyDay4.setOnClickListener(this);
        notifyDay5.setOnClickListener(this);
        notifyDay6.setOnClickListener(this);
        notifyDay7.setOnClickListener(this);

        notifyModTimeLayout.setOnClickListener(this);
        notifyModDayCheck.setOnCheckedChangeListener(this);
    }
}
