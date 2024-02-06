package com.example.project.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircularImageView extends AppCompatImageView {

    private final Paint paint = new Paint();
    private final RectF rect = new RectF();
    private Bitmap bitmap;

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Method to set a circular bitmap to the CircularImageView
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate(); // Redraw the view
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        float radius = Math.min(width, height) / 2.0f;

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bitmap);
            super.onDraw(tempCanvas);
        }

        // Create a new bitmap scaled to fit the circular region
        Bitmap scaledBitmap = scaleBitmapToCircle(bitmap, width, height);

        // Create a new bitmap for the circular mask
        Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas circularCanvas = new Canvas(circularBitmap);
        Paint circularPaint = new Paint();
        circularPaint.setAntiAlias(true);
        circularCanvas.drawCircle(width / 2.0f, height / 2.0f, radius, circularPaint);

        // Use PorterDuffXfermode to apply the circular mask to the scaled bitmap
        Paint maskPaint = new Paint();
        maskPaint.setFilterBitmap(false);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        circularCanvas.drawBitmap(scaledBitmap, (width - scaledBitmap.getWidth()) / 2.0f, (height - scaledBitmap.getHeight()) / 2.0f, maskPaint);

        // Draw the circular bitmap to the view canvas
        canvas.drawBitmap(circularBitmap, 0, 0, null);
    }

    private Bitmap scaleBitmapToCircle(Bitmap bitmap, int width, int height) {
        int minSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        float scale = (float) Math.max(width, height) / minSize;
        int scaledWidth = Math.round(scale * bitmap.getWidth());
        int scaledHeight = Math.round(scale * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }


}

