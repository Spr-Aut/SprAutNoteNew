package com.spraut.sprautnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.spraut.sprautnote.Blur.RealtimeBlurView;

public class RoundShapeBulr extends RealtimeBlurView {
    Paint mPaint;
    RectF mRectF;

    public RoundShapeBulr(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mRectF = new RectF();
    }

    /**
     * Custom oval shape
     */
    @Override
    protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap, int overlayColor) {
        if (blurredBitmap != null) {
            mRectF.right = getWidth();
            mRectF.bottom = getHeight();

            mPaint.reset();
            mPaint.setAntiAlias(true);
            BitmapShader shader = new BitmapShader(blurredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            matrix.postScale(mRectF.width() / blurredBitmap.getWidth(), mRectF.height() / blurredBitmap.getHeight());
            shader.setLocalMatrix(matrix);
            mPaint.setShader(shader);
            canvas.drawOval(mRectF, mPaint);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setColor(overlayColor);
            canvas.drawOval(mRectF, mPaint);
        }
    }
}
