package oms.mmc.fortunetelling.fate.lib.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smartydroid.android.starter.kit.utilities.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import me.drakeet.materialdialog.MaterialDialog;
import oms.mmc.fortunetelling.fate.lib.App;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.MainActivity;
import oms.mmc.fortunetelling.fate.lib.R;

public class AlarmActivity extends AppCompatActivity {

    private long mTime;
    private String mTimeString;
    MediaPlayer mMediaPlayer;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_alarm);

        mTime = getIntent().getLongExtra(Constant.EXTRA_NOTIFY_ALARM_TIME, Calendar.getInstance().getTimeInMillis());
        Date date = new Date(mTime);
        mTimeString = date.getHours() + ":"+date.getMinutes();

        startAlarm();
        showDialog(this);
        showNotification();
    }


    private void showDialog(final Context context){
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alarm_layout, null);
        final TextView textView = ViewUtils.getView(view, R.id.alarm_dialog_time_tv);
        textView.setText(mTimeString);
        materialDialog.setView(view);
        materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                finish();
                mMediaPlayer.pause();
                MobclickAgent.onEvent(context, "notify_dialog_cancel");
            }
        });
        materialDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((App)getApplication()).startMainActivity(new Bundle());
                finish();
                mMediaPlayer.pause();
                MobclickAgent.onEvent(context, "notify_dialog_confirm");
            }
        });
        materialDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                countDownTimer.cancel();
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                finish();

            }
        });
        materialDialog.show();
        MobclickAgent.onEvent(context, "notify_dialog_show");
    }

    private void startAlarm() {
        mMediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        countDownTimer.start();

    }
    CountDownTimer countDownTimer = new CountDownTimer(30000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
        }
    };

    private void showNotification(){
        Intent notify = new Intent(context,(Class<?>) ((App)getApplication()).getMainActivity());
        notify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingNotify = PendingIntent.getActivity(context,
                100, notify, PendingIntent.FLAG_CANCEL_CURRENT);

        // Use the alarm's label or the default label as the ticker text and
        // main text of the notification.
        String label = context.getString(R.string.alarm_default_label);

        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(label)
                .setContentText(context.getString(R.string.activity_name_app))
                .setContentIntent(pendingNotify)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(Calendar.getInstance().getTimeInMillis());
        Notification n = builder.getNotification();
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.defaults |= Notification.DEFAULT_LIGHTS;

        NotificationManager nm = getNotificationManager(context);
        nm.notify(100, n);
    }



    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
