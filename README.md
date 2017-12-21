# MusicPlayer
动感音乐播放器

## 演示图片
<img src="https://github.com/wangjing0311/MusicPlayer/blob/master/app/demoImg/music_player_img.mp4"/>

## 主要波形显示代码：
```java
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
```
## 两种波形类：
```java
WaveDrawable.java  //心电图波浪Wave波形
FftDrawable.java  //柱状图频率Fft波形
```