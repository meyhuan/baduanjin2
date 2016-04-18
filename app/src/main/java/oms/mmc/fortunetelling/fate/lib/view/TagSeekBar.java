package oms.mmc.fortunetelling.fate.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * User  : guanhuan
 * Date  : 2016/4/11
 */
public class TagSeekBar extends SeekBar{

    private Paint paint ;
    private int [] mPositions = new int[]{};

    public TagSeekBar(Context context) {
        this(context, null);
    }

    public TagSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 设置节点位置 （如：{20,30,50,90}）
     * @param positions positions （0 -- 100）
     */
    public void setTagPositions(int [] positions){
        this.mPositions = positions;
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.parseColor("#FDFDFE"));
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int position : mPositions) {
            canvas.drawCircle(getMeasuredWidth() *  position / 100, getMeasuredHeight() / 2, getProgressDrawable().getIntrinsicHeight() / 2, paint);
        }
        canvas.save();
    }
}
