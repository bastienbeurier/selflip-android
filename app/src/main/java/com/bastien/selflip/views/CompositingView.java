package com.bastien.selflip.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by guillaumelachaud on 6/15/14.
 */
public class CompositingView extends View {

    private Bitmap mBackgroundBitmap;
    private Bitmap mFadedBitmap;
    private Bitmap mMaskBitmap;

    private Paint mShaderPaint;
    private Paint mBackgroundPaint = new Paint();

    private float mGradientPosition = 0.5f;

    private int mX = 0;
    private int mY = 0;

    private Matrix mTempMatrix = new Matrix();

    private int mPreviousX = 0;
    private int mPreviousY = 0;

    private int topOffset;


    public CompositingView(Context context) {
        super(context);
    }

    public CompositingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompositingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setupGradient();
        setupBitmapShader();
        setupBackgroundBitmap();
        int[] coords = {0,0};
        getLocationOnScreen(coords);
        topOffset = coords[1];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(canvas.isHardwareAccelerated()){
            //Absolute coordinates

            mShaderPaint.getShader().getLocalMatrix(mTempMatrix);
            mTempMatrix.postTranslate(0, topOffset);
            mShaderPaint.getShader().setLocalMatrix(mTempMatrix);
        }

        if(mBackgroundBitmap != null){
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
        }
        canvas.drawBitmap(mMaskBitmap,mX,mY,mShaderPaint);

    }

    private void setupGradient() {
        Paint p = new Paint();
        int offset = (int) ((mGradientPosition - 0.5) * (getHeight() / 2));

        Shader shader = new LinearGradient(0, getHeight()/2 + offset,0, offset + getHeight()/2+(float) (0.05*getHeight()),new int[] {
                Color.BLACK, Color.TRANSPARENT },
                null, Shader.TileMode.CLAMP);

        p.setShader(shader);

        Rect r = new Rect(0,0,getWidth(),getHeight());

        //MASK
        mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ALPHA_8);
        Canvas maskCanvas = new Canvas(mMaskBitmap);
        maskCanvas.drawRect(r, p);
    }

    private void setupBitmapShader(){
        if(mFadedBitmap != null){
            mShaderPaint = new Paint();
            BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(mFadedBitmap, getWidth(), getHeight(), true), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mShaderPaint.setShader(shader);
        }
    }

    private void setupBackgroundBitmap(){
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, getWidth(), getHeight(), true);
    }

    public void setGradientPosition(float position){
        mGradientPosition = position;
        setupGradient();
        invalidate();
    }

    public void setBackgroundBitmap(Bitmap bgBitmap){
        mBackgroundBitmap = bgBitmap;
        invalidate();
    }

    public void setFadedBitmap(Bitmap fadedBitmap){
        mFadedBitmap = fadedBitmap;
        if(getWidth() > 0){
            setupBitmapShader();
            invalidate();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mPreviousX = (int) event.getRawX();
            mPreviousY = (int) event.getRawY();
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            int deltaX = (int) (event.getRawX() - mPreviousX);
            int deltaY = (int) (event.getRawY() - mPreviousY);
            mX += deltaX;
            mY += deltaY;
            Matrix m = new Matrix();
            m.setTranslate(mX, mY);
            mShaderPaint.getShader().setLocalMatrix(m);
            invalidate();
            mPreviousX = (int) event.getRawX();
            mPreviousY = (int) event.getRawY();
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                init();

                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public void saveToDisk() throws FileNotFoundException {
        File f = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Selflip/selflip_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = new FileOutputStream(f);
        Bitmap b = Bitmap.createBitmap(getWidth() , getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        mShaderPaint.getShader().setLocalMatrix(new Matrix());
        draw(c);
        b.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }

}
