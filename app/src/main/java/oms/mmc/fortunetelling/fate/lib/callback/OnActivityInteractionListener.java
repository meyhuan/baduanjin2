package oms.mmc.fortunetelling.fate.lib.callback;


import android.content.Intent;

/**
 * Author: meyu
 * Date:   16/3/5
 * Email:  627655140@qq.com
 * activity 与 fragment 通信回调接口(fragment监听activity里面的动作)
 */
public interface OnActivityInteractionListener {
    // TODO: Update argument type and name
    void onActivityInteraction(Intent intent);
}