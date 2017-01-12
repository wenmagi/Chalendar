package com.magi.chlendar.ui.widget.reveal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.magi.chlendar.ui.widget.reveal.animation.RevealAnimator;
import com.magi.chlendar.ui.widget.reveal.animation.SupportAnimator;
import com.magi.chlendar.ui.widget.reveal.animation.ViewAnimationUtils;


public class RevealFrameLayout extends FrameLayout implements RevealAnimator {

	private Paint mRevealClearPaint;
    private Path mRevealPath;
    private final Rect mTargetBounds = new Rect();
    private RevealAnimator.RevealInfo mRevealInfo;
    private boolean mRunning;
    private float mRadius;

    public RevealFrameLayout(Context context) {
        this(context, null);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRevealPath = new Path();

		mRevealClearPaint = new Paint();
		mRevealClearPaint.setColor(Color.TRANSPARENT);
		mRevealClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

    @Override
    public void onRevealAnimationStart() {
        mRunning = true;
    }

    @Override
    public void onRevealAnimationEnd() {
        mRunning = false;
        invalidate(mTargetBounds);
    }

    @Override
    public void onRevealAnimationCancel() {
        onRevealAnimationEnd();
    }

    /**
     * Circle radius size
     *
     * @hide
     */
    @Override
    public void setRevealRadius(float radius){
        mRadius = radius;
        mRevealInfo.getTarget().getHitRect(mTargetBounds);
        invalidate(mTargetBounds);
    }

    /**
     * Circle radius size
     *
     */
    @Override
    public float getRevealRadius(){
        return mRadius;
    }

	@Override
	public RevealInfo getRevealInfo() {
		return null;
	}

	/**
     * @hide
     */
    @Override
    public void attachRevealInfo(RevealInfo info) {
        mRevealInfo = info;
    }

    /**
     * @hide
     */
    @Override
    public SupportAnimator startReverseAnimation() {
        if(mRevealInfo != null && mRevealInfo.hasTarget() && !mRunning) {
            return ViewAnimationUtils.createCircularReveal(mRevealInfo.getTarget(),
                    mRevealInfo.centerX, mRevealInfo.centerY,
                    mRevealInfo.endRadius, mRevealInfo.startRadius);
        }
        return null;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if(mRunning && child == mRevealInfo.getTarget()) {
			if (mRevealInfo.isClear) {
				Bitmap bitmap = Bitmap.createBitmap(child.getWidth(), child.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas bitmapCanvas = new Canvas(bitmap);
				boolean isInvalided = super.drawChild(bitmapCanvas, child, drawingTime);
				bitmapCanvas.drawCircle(mRevealInfo.centerX,  mRevealInfo.centerY, mRadius, mRevealClearPaint);
				canvas.drawBitmap(bitmap, child.getLeft(), child.getTop(), new Paint(Paint.ANTI_ALIAS_FLAG));
				return isInvalided;
			} else {
				final int state = canvas.save();

				mRevealPath.reset();
				mRevealPath.addCircle(mRevealInfo.centerX, mRevealInfo.centerY, mRadius, Path.Direction.CW);

				canvas.clipPath(mRevealPath);

				boolean isInvalided = super.drawChild(canvas, child, drawingTime);

				canvas.restoreToCount(state);

				return isInvalided;
			}
        }

        return super.drawChild(canvas, child, drawingTime);
    }

	public boolean isRunning() {
		return mRunning;
	}

}
