package com.madi.sphero_21;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Madi Zhanbyrtayev on 04.05.2016.
 *
 * Custom view for drawing path on the image of the indoor plan
 */
public class ImageMap extends ImageView {
    private Path mPath = null;
    private Paint mPaint = null;
    private float x = 0;
    private float y = 0;

    public ImageMap(Context context) {
        super(context);
    }

    public ImageMap(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageMap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMap(Path src, float scale, float rx, float ry){
        mPath = src;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
        x = rx;
        y = ry;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPath!=null){
            canvas.translate(x,y);
            canvas.scale(1, -1);
            canvas.drawPath(mPath, mPaint);
        }
    }
}
