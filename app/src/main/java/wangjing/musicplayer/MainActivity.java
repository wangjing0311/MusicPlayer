package wangjing.musicplayer;

import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import wangjing.musicplayer.utils.StringUtils;
import wangjing.musicplayer.views.FftDrawable;
import wangjing.musicplayer.views.WaveDrawable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WaveDrawable waveDrawable;
    private FftDrawable fftDrawable;
    private Button playSelectBtn;
    private Button playBtn;
    //    private WaveView visualizerView;
//    private FftView fftView;
    private View view;
    private TextView playName;
    private SeekBar playBar;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private MediaPlayer mediaPlayer;
    private boolean isWave = false;
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private String wavFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initOnClick();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        timer.cancel();
        timer = null;
        mediaPlayer = null;
        super.onDestroy();
    }

    private void initView() {
        playSelectBtn = findViewById(R.id.play_select_btn);
        playBtn = findViewById(R.id.play_btn);
        view = findViewById(R.id.view);
        playName = findViewById(R.id.play_name_tv);
        playBar = findViewById(R.id.play_bar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        mediaPlayer = new MediaPlayer();
        waveDrawable = new WaveDrawable();
        fftDrawable = new FftDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(isWave ? waveDrawable : fftDrawable);
        } else {
            view.setBackgroundDrawable(isWave ? waveDrawable : fftDrawable);
        }
        wavFile = "/mnt/sdcard/AAA-test/Lay Low.mp3";
        playName.setText("Lay Low");
    }

    private void initData() {
        if (!new File(wavFile).exists()) {
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this).setMessage("请先选择音频或录音！").show();
            return;
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(wavFile);
            mediaPlayer.prepare();
            Visualizer mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//                            visualizerView.updateVisualizer(waveform);
                    waveDrawable.update(waveform);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    Log.i(TAG, "onWaveFormDataCapture: " + Visualizer.getMaxCaptureRate() / 2);
                    byte[] fft = new byte[20];
                    for (int i = 0; i < fft.length; i++) {
                        float real = bytes[i];
                        float imag = bytes[i + 1];
                        fft[i] = (byte) (Math.sqrt(Math.sqrt(Math.sqrt((real * real) + (imag * imag)) * 3) * 6) * 9);
                        fft[i] = (byte) Math.min(fft[i], 100);
                    }
                    fftDrawable.update(fft);
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, true);
            mVisualizer.setEnabled(true);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        playBar.setMax(mediaPlayer.getDuration());
        //监听播放时回调函数
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isSeekBarChanging) {
                    playBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 50);
    }

    private void initOnClick() {
        playSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                playBtn.setText("播放");
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File("/mnt/sdcard");
                properties.error_dir = new File("/mnt/sdcard");
                properties.extensions = new String[]{"wav"};

                FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
                dialog.setTitle("Select a wav file");

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //files is the array of the paths of files selected by the Application User.
                        wavFile = StringUtils.join(files, ',');
//                        PreferencesUtils.putString(activity, WAV_PATH, wavFile);
                        playName.setText(wavFile);
                        initData();
                    }
                });
                dialog.show();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                    postInvalidate();
                }
                playBtn.setText(mediaPlayer.isPlaying() ? "停止" : "播放");
                //使用系统音乐播放器播放
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = Uri.fromFile(new File(wavPath));
//                intent.setDataAndType(uri, "audio/*");
//                startActivity(intent);
            }
        });
        playBar.setOnSeekBarChangeListener(new MySeekBar());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fabView) {
                isWave = !isWave;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(isWave ? waveDrawable : fftDrawable);
                } else {
                    view.setBackgroundDrawable(isWave ? waveDrawable : fftDrawable);
                }
            }
        });
    }

    private void postInvalidate() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(Integer.MAX_VALUE);
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.postInvalidate();
            }
        });
        animator.start();
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
        }
    }
}
