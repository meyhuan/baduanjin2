package oms.mmc.fortunetelling.fate.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author: meyu
 * Date:   16/3/27
 * Email:  627655140@qq.com
 */
public class FullScreenVideoView extends VideoView{


    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
