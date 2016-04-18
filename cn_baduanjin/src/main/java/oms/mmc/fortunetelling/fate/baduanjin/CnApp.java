package oms.mmc.fortunetelling.fate.baduanjin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import oms.mmc.fortunetelling.fate.lib.App;

/**
 * Author: meyu
 * Date:   16/3/19
 * Email:  627655140@qq.com
 */
public class CnApp extends App{
    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Override
    public void startMainActivity(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
       startActivity(intent);
    }

    @Override
    public Object getMainActivity() {
        return MainActivity.class;
    }
}
