/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oms.mmc.fortunetelling.fate.lib.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.smartydroid.android.starter.kit.utilities.Utils;
import com.smartydroid.android.starter.kit.utilities.ViewUtils;

import me.drakeet.materialdialog.MaterialDialog;
import oms.mmc.fortunetelling.fate.lib.Constant;
import oms.mmc.fortunetelling.fate.lib.MainActivity;
import oms.mmc.fortunetelling.fate.lib.R;
import oms.mmc.fortunetelling.fate.lib.ui.AboutAppActivity;
import oms.mmc.fortunetelling.fate.lib.ui.AlarmActivity;

/**
 * Glue class: connects AlarmAlert IntentReceiver to AlarmAlert
 * activity.  Passes through Alarm ID.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /** If the alarm is older than STALE_WINDOW, ignore.  It
        is probably the result of a time or timezone change */
    private final static int STALE_WINDOW = 30 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = null;
        // Grab the alarm from the intent. Since the remote AlarmManagerService
        // fills in the Intent to add some extra data, it must unparcel the
        // Alarm object. It throws a ClassNotFoundException when unparcelling.
        // To avoid this, do the marshalling ourselves.
        final byte[] data = intent.getByteArrayExtra(Alarms.ALARM_RAW_DATA);
        if (data != null) {
            Parcel in = Parcel.obtain();
            in.unmarshall(data, 0, data.length);
            in.setDataPosition(0);
            alarm = Alarm.CREATOR.createFromParcel(in);
        }

        if (alarm == null) {
            Log.v("wangxianming", "Failed to parse the alarm from the intent");
            // Make sure we set the next alert if needed.
            Alarms.setNextAlert(context);
            return;
        }

        // Disable this alarm if it does not repeat.
        if (!alarm.daysOfWeek.isRepeatSet()) {
            Alarms.enableAlarm(context, alarm.id, false);
        } else {
            // Enable the next alert if there is one. The above call to
            // enableAlarm will call setNextAlert so avoid calling it twice.
            Alarms.setNextAlert(context);
        }

        // Intentionally verbose: always log the alarm time to provide useful
        // information in bug reports.
        long now = System.currentTimeMillis();

        // Always verbose to track down time change problems.
        if (now > alarm.time + STALE_WINDOW) {
            Log.v("wangxianming", "Ignoring stale alarm");
            return;
        }


        /* Close dialogs and window shade */
//        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        context.sendBroadcast(closeDialogs);

        // Trigger a notification that, when clicked, will show the alarm alert
        // dialog. No need to check for fullscreen since this will always be
        // launched from a user action.

//        showDialog(context);
        Intent intent1 = new Intent(context, AlarmActivity.class);
        intent1.putExtra(Constant.EXTRA_NOTIFY_ALARM_TIME,alarm.time);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }



    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void updateNotification(Context context, Alarm alarm, int timeout) {
        NotificationManager nm = getNotificationManager(context);

        // If the alarm is null, just cancel the notification.
        if (alarm == null) {
            if (true) {
                Log.v("wangxianming", "Cannot update notification for killer callback");
            }
            return;
        }

        // Launch SetAlarm when clicked.
        Intent viewAlarm = new Intent(context, MainActivity.class);
        viewAlarm.putExtra(Alarms.ALARM_ID, alarm.id);
        PendingIntent intent =
                PendingIntent.getActivity(context, alarm.id, viewAlarm, 0);

        // Update the notification to indicate that the alert has been
        // silenced.
        String label = alarm.getLabelOrDefault(context);
//
//        Notification.Builder builder = new Notification.Builder(context)
//                .setAutoCancel(true)
//                .setContentTitle(label)
//                .setContentText(context.getString(R.string.alarm_alert_alert_silenced, timeout))
//                .setContentIntent(intent)
//                .setSmallIcon(R.drawable.stat_notify_alarm)
//                .setWhen(alarm.time);
//        Notification n = builder.getNotification();
//
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
//        // We have to cancel the original notification since it is in the
//        // ongoing section and we want the "killed" notification to be a plain
//        // notification.
//        nm.cancel(alarm.id);
//        nm.notify(alarm.id, n);
    }
}
