package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class TextViewHelper extends View {

    private final Paint paint = new Paint();
    private final Paint paintText = new Paint();

    private final int dp = 4;
    private final float top = AndroidUtilities.dp(dp);
    private final float left = AndroidUtilities.dp(dp);
    private final float bottom = AndroidUtilities.dp(5);
    private final float rx = AndroidUtilities.dp(8);

    private final float widthDp = AndroidUtilities.dp(5);
    private final float rightDp = AndroidUtilities.dp(3);
    private final float topBottom = AndroidUtilities.dp(3.58f);

    private final float leftText = AndroidUtilities.dp(15);
    private final float topText = AndroidUtilities.dp(24.5f);

    private final String str = "Forwards from this channel are restricted";

    private final Theme.ResourcesProvider resourcesProvider;

    private final Runnable dismissTunnable = this::hideInternal;

    private void hideInternal() {
        setVisibility(GONE);
    }

    public TextViewHelper(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;

        paint.setColor(getThemedColor(Theme.key_undo_background));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        paintText.setColor(getThemedColor(Theme.key_undo_infoColor));
        paintText.setTextSize(spToPx(15, getContext()));

        setVisibility(GONE);
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidate();
    }

    public int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = (int) widthDp / 2;

        RectF rectF = new RectF(left, top, getWidth() - widthDp, getHeight() - bottom * 2 - radius);
        canvas.drawRoundRect(rectF, rx, rx, paint);

        Path path = new Path();
        path.moveTo(getWidth() - 8 * widthDp - rightDp, getHeight() - bottom * 2 - radius);
        path.lineTo(getWidth() - 6 * widthDp - rightDp, getHeight() - radius);
        path.lineTo(getWidth() - 5 * widthDp - rightDp, getHeight() - radius);
        path.lineTo(getWidth() - 3 * widthDp - rightDp, getHeight() - bottom * 2 - radius);

        path.moveTo(getWidth() - 6 * widthDp - rightDp, getHeight() - bottom * 2 - radius);
        path.close();

        int cx = (int) (getWidth() - 6 * widthDp - rightDp);
        RectF rectF1 = new RectF(cx, getHeight() - topBottom - radius, cx + radius * 2.2f, getHeight() - topBottom + radius);
        canvas.drawArc(rectF1, 30f, 120f, false, paint);

        canvas.drawPath(path, paint);

        canvas.drawText(str, leftText, topText, paintText);
    }

    void show() {
        setVisibility(VISIBLE);
        AndroidUtilities.cancelRunOnUIThread(dismissTunnable);
        AndroidUtilities.runOnUIThread(dismissTunnable, 3000);
    }

}
