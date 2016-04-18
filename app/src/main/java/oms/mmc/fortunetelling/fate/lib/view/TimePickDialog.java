package oms.mmc.fortunetelling.fate.lib.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.smartydroid.android.starter.kit.widget.PickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarm;
import oms.mmc.fortunetelling.fate.lib.alarm.Alarms;

/**
 * Author: meyu
 * Date:   16/3/2
 * Email:  627655140@qq.com
 */
public class TimePickDialog {

    private Context mContext;
    MaterialDialog materialDialog;

    private PickerView mPickerView0;
    private PickerView mPickerView1;
    private PickerView mPickerView2;

    private List<String> data0 = new ArrayList<>();
    private List<String> data1 = new ArrayList<>();
    private List<String> data2 = new ArrayList<>();

    private String timeString0;
    private String timeString1;
    private String timeString2;

    private Alarm mCurrentAlarm;

    TimePickerConfirmListener timePickerConfirmListener;

    public TimePickDialog(Context context, Alarm alarm){
        this.mContext = context;
        mCurrentAlarm = alarm;
        initView();
        initData();
    }


    private void initView(){
        materialDialog = new MaterialDialog(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_time_picker_view, null);
        mPickerView0 = ViewUtils.getView(view, R.id.dialog_time_picker0);
        mPickerView1 = ViewUtils.getView(view, R.id.dialog_time_picker1);
        mPickerView2 = ViewUtils.getView(view, R.id.dialog_time_picker2);
        mPickerView0.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                timeString0 = text;
            }
        });
        mPickerView1.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                timeString1 = text;
            }
        });
        mPickerView2.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                timeString2 = text;
            }
        });

        materialDialog.setContentView(view);
        materialDialog.setPositiveButton(R.string.notify_mod_right, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePickerConfirmListener != null){
                    Alarm n = new Alarm();
                    n.label = timeString0;
                    n.hour = Integer.parseInt(timeString1);
                    n.minutes = Integer.parseInt(timeString2);
                    n.enabled = true;
                    timePickerConfirmListener.confirm(n);
                }

                materialDialog.dismiss();
            }
        });

        materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
    }

    private void initData() {
        String am = mContext.getResources().getString(R.string.notify_meridiem_ante);
        String pm = mContext.getResources().getString(R.string.notify_meridiem_psot);
        data0.add(am);
        data0.add(pm);
        data0.add(am);
        data0.add(pm);

        boolean is24 = Alarms.get24HourMode(mContext);
        int hours =is24 ? 24 : 12;

        for(int i = 0; i < 60; i++){
            if(i < hours ){
                data1.add(is24 ? i+"" : i+1+"");
            }
            data2.add(String.format(Locale.CHINA,"%02d",i));
        }

        mPickerView0.setData(data0);
        mPickerView1.setData(data1);
        mPickerView2.setData(data2);

        //根据传入的时间设置对应的数值
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(mCurrentAlarm.time));
        int index1 = c.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1;
        int index2 = c.get(is24 ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
        int index3 = c.get(Calendar.MINUTE);
        mPickerView0.setSelected(index1);
        mPickerView1.setSelected(index2);
        mPickerView2.setSelected(index3);

        timeString0 = data0.get(index1);
        timeString1 = data1.get(index2);
        timeString2 = data2.get(index3);
        //如果是24小时制，不显示上、下午
        mPickerView0.setVisibility(is24 ? View.VISIBLE : View.GONE);

    }

    public void setTimePickerConfirmListener(TimePickerConfirmListener t){
        timePickerConfirmListener = t;
    }


    public void showDialog(){
        materialDialog.show();
    }

    public interface TimePickerConfirmListener{
        void confirm(Alarm notification);
    }

}
