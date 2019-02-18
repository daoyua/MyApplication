package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyTestView extends View {
    public MyTestView(Context context) {
        super(context);
    }
    public MyTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mHolder = this.getHolder();
//        mHolder.addCallback(this);
    }
    public float left, top, width, height;
    float bei=3.50f;
    public void setView(float lefts, float tops, float widths, float heights) {
        left = lefts*bei;
        top = tops*bei;
        width = widths*bei;
        height = heights*bei;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Canvas canvas=new Canvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
        canvas.drawLine(left,top,left+width,top,paint);//横
        canvas.drawLine(left,top+height,left+width,top+height,paint);//第二横
        canvas.drawLine(left,top,left,top+height,paint);//第一su
        canvas.drawLine(left+width,top,left+width,top+height,paint);//第二竖
//                    canvas.drawRect(
//                            left, top, left+width, top+height, paint
//                    );
    }
}
