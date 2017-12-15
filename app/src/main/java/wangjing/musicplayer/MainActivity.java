package wangjing.musicplayer;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import wangjing.musicplayer.views.FftView;
import wangjing.musicplayer.views.VisualizerView;
import wangjing.musicplayer.views.WaveDrawable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WaveDrawable waveDrawable;
    private Button playBtn;
    private VisualizerView visualizerView;
    private FftView fftView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        playBtn = findViewById(R.id.play_btn);
        visualizerView = findViewById(R.id.visualizerView);
        fftView = findViewById(R.id.fftView);
        setSupportActionBar(toolbar);
        mediaPlayer = new MediaPlayer();
        waveDrawable = new WaveDrawable();
        waveDrawable = new WaveDrawable();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String wavPath = "/mnt/sdcard/AAA-test/Lay Low.mp3";
                if (!new File(wavPath).exists()) {
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this).setMessage("请先选择音频或录音！").show();
                    return;
                }
                try {
                    mediaPlayer.setDataSource(wavPath);
                    mediaPlayer.prepare();

                    Visualizer mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
                    Log.i(TAG, "onWaveFormDataCapture:11111111111111111111 ");

                    mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                        @Override
                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//                            visualizerView.updateVisualizer(waveform);
                        }

                        @Override
                        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                            Log.i(TAG, "onWaveFormDataCapture: " + Visualizer.getMaxCaptureRate() / 2);
                            byte[] fft = new byte[20];
                            for (int i = 0; i < fft.length; i++) {
                                float real = bytes[i];
                                float imag = bytes[i + 1];
                                fft[i] = (byte) ( Math.sqrt(Math.sqrt(Math.sqrt((real * real) + (imag * imag))*3)*6)*9);
                                fft[i] = (byte) Math.min(fft[i], 100);
                            }
                            fftView.update(fft);
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, true, true);
                    mVisualizer.setEnabled(true);
                    mediaPlayer.start();
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                    animator.setDuration(Integer.MAX_VALUE);
                    animator.setRepeatCount(Integer.MAX_VALUE);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            fftView.postInvalidate();
                        }
                    });
                    animator.start();
                    //                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = Uri.fromFile(new File(wavPath));
//                intent.setDataAndType(uri, "audio/*");
//                startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
