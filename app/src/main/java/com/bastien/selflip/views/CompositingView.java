package com.bastien.selflip.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by guillaumelachaud on 6/15/14.
 */
public class CompositingView extends View {



    public CompositingView(Context context) {
        super(context);
    }

    public CompositingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompositingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.GREEN);

        Paint p = new Paint();
        Shader mShader = new LinearGradient(0, 0,0, 100+(float) (0.05*getHeight()),new int[] {
                Color.BLACK, Color.TRANSPARENT },
                null, Shader.TileMode.CLAMP);

        p.setShader(mShader);

        Rect r = new Rect(0,0,getWidth(),getHeight()/2);


        canvas.drawRect(r, p);


    }
}
