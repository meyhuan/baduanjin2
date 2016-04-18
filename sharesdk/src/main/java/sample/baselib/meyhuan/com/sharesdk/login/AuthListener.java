package sample.baselib.meyhuan.com.sharesdk.login;

import android.os.Bundle;

/**
 * Author: meyu
 * Date:   16/4/4
 * Email:  627655140@qq.com
 */
public interface AuthListener {
    void onComplete(Bundle var1);

    void onWeiboException(Exception var1);

    void onCancel();


}
