package com.bastien.selflip;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.bastien.selflip.views.CompositingView;

import java.io.FileNotFoundException;

public class CompositionActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar mGradientSeekBar;
    private CompositingView mCompositingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        mCompositingView = (CompositingView) findViewById(R.id.compositingView);

        mGradientSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mGradientSeekBar.setOnSeekBarChangeListener(this);
        mGradientSeekBar.setProgress(50);

        mCompositingView.setFadedBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("topImage")));
        mCompositingView.setBackgroundBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("bottomImage")));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.composition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            mCompositingView.setGradientPosition(progress / 100f);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onSave(View v){
        try {
            mCompositingView.saveToDisk();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
