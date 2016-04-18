package oms.mmc.fortunetelling.fate.lib.utils;

import android.content.Context;
import android.os.Environment;

import com.smartydroid.android.starter.kit.utilities.ACache;

import java.io.File;
import java.io.IOException;

/**
 * Author: meyu
 * Date:   16/3/26
 * Email:  627655140@qq.com
 */
public class Util {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }


    public static File getVideoCacheFile(Context context){
        File file = null;
        if(Util.isMIUI()){
            file = new File(Environment.getExternalStorageDirectory() + "/Video0");
        }else {
            file = ACache.getExternCacheFile(context);
        }
        file = new File(Environment.getExternalStorageDirectory() + "/BaDuanJin");
        return file;
    }

}
