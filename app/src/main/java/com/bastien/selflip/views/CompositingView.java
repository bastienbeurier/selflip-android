package com.bastien.selflip.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.bastien.selflip.R;

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

        canvas.drawColor(Color.RED);

        Paint p = new Paint();
        Shader mShader = new LinearGradient(0, getHeight()/2,0, getHeight()/2+(float) (0.05*getHeight()),new int[] {
                Color.BLACK, Color.TRANSPARENT },
                null, Shader.TileMode.CLAMP);

        p.setShader(mShader);

        Rect r = new Rect(0,0,getWidth(),getHeight());

        //MASK
        Bitmap mask = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ALPHA_8);
        Canvas maskCanvas = new Canvas(mask);
        maskCanvas.drawRect(r, p);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap scaled = Bitmap.createScaledBitmap(b, getWidth(), getHeight(), true);
        Shader bShader = new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint bp = new Paint();
        bp.setShader(bShader);
        canvas.drawBitmap(mask,0,0,bp);

    }

    private Bitmap convertToAlphaMask(Bitmap b) {
        Bitmap a = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(a);
        c.drawBitmap(b, 0.0f, 0.0f, null);
        return a;
    }

}
