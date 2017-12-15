package wangjing.musicplayer.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by 王静 on 2017/12/14 16:53.
 */

public class WaveDrawable extends Drawable {

    private Paint paint;
    private Paint paintTop;
    private byte[] datas;

    public WaveDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(60);
        paint.setColor(0xffff8888);
        paint.setStyle(Paint.Style.STROKE);
        paintTop = new Paint(paint);
        paintTop.setColor(0xaa950000);
        paint.setPathEffect(new DashPathEffect(new float[]{15, 4}, 0));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (datas == null) return;
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int dt = (width - 120) / 11;
        int start = 35;
        Shader shader = new LinearGradient(0, 0, 0, height * 0.6f, Color.parseColor("#04e800"),
                Color.parseColor("#ff0000"), Shader.TileMode.MIRROR);
        int[] colors = new int[]{0xff04e800, 0xfffff800, 0xffff0000};
        float[] position = new float[]{0.3f, 0.6f, 1.0f};
        Shader shader2 = new LinearGradient(0, 0, 0, height * 0.6f, colors,
                position, Shader.TileMode.MIRROR);
        paint.setShader(shader2);
        Path path = new Path();
        Path pathTop = new Path();
        for (int i = 0; i < datas.length; i++) {
            int data = datas[i];
            int y = (int) (data * height / 100 * 0.6f + 10);
//            canvas.drawLine(start, height - 10, start, height - y, paint);
            path.moveTo(start, height - 10);
            path.lineTo(start, height - y);
            if (data > 0) {
                datas[i] -= 1;
            }

            float dataTop = datasTop[i];
            int yTop = (int) (dataTop * height / 100 * 0.6f + 10);
//            canvas.drawLine(start, height - 10, start, height - y, paint);
            pathTop.moveTo(start, height - yTop - 10);
            pathTop.lineTo(start, height - yTop - 25);
            start += dt;
            if (dataTop > 0) {
                datasTop[i] -= 0.3f;
            }
        }
        canvas.drawPath(path, paint);
        canvas.drawPath(pathTop, paintTop);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    private float[] datasTop;

    public void update(byte[] datas) {
        this.datas = datas;
        if (datasTop == null) {
            datasTop = new float[datas.length];
        }
        for (int i = 0; i < datasTop.length; i++) {
            if (datasTop[i] < datas[i]) {
                datasTop[i] = datas[i];
            }
        }
    }
}
