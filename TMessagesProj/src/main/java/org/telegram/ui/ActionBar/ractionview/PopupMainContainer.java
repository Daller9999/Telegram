package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ractionview.views.ActionBarFullReactionsInfo;
import org.telegram.ui.Components.LayoutHelper;

public class PopupMainContainer extends FrameLayout {

    private static final int width = 250;
    private int minHeight = 300;
    private int maxHeight = 400;

    private LinearLayout scrimPopupContainerLayout;
    private ActionBarFullReactionsInfo actionBarFullReactionsInfo;

    public PopupMainContainer(
            @NonNull Context context,
            LinearLayout scrimPopupContainerLayout,
            ActionBarFullReactionsInfo actionBarFullReactionsInfo
    ) {
        super(context);
        this.scrimPopupContainerLayout = scrimPopupContainerLayout;
        this.actionBarFullReactionsInfo = actionBarFullReactionsInfo;
        init();
    }

    public PopupMainContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PopupMainContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PopupMainContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        setLayoutParams(LayoutHelper.createFrame(300, 700));
        Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        Rect backgroundPaddings = new Rect();
        shadowDrawable.getPadding(backgroundPaddings);
        // setBackground(shadowDrawable);*/

        View viewBack = new View(getContext());
        viewBack.setBackground(shadowDrawable);
        addView(viewBack);

        scrimPopupContainerLayout.setLayoutParams(LayoutHelper.createFrame(
                width, minHeight
        ));
        addView(scrimPopupContainerLayout);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(LayoutHelper.createFrame(
                width, FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.TOP | Gravity.LEFT,
                0, 0, 0, 0
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setHorizontalScrollBarEnabled(false);


        View viewEmpty = new View(getContext());
        viewEmpty.setLayoutParams(LayoutHelper.createLinear(width, maxHeight));
        linearLayout.addView(viewEmpty);

        actionBarFullReactionsInfo.setLayoutParams(LayoutHelper.createLinear(width, maxHeight));
        linearLayout.addView(actionBarFullReactionsInfo);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        horizontalScrollView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            Log.i("testApp", "x = " + horizontalScrollView.getScrollX());
            float percent = (float) horizontalScrollView.getScrollX() / (float) AndroidUtilities.dp(width);
            viewBack.setLayoutParams(LayoutHelper.createFrame(
                    width, minHeight + (maxHeight - minHeight) * percent,
                    Gravity.TOP,
                    0, 0, 0, 0
            ));
            linearLayout.setLayoutParams(LayoutHelper.createFrame(
                    width, minHeight + (maxHeight - minHeight) * percent,
                    Gravity.TOP,
                    0, 0, 0, 0
            ));
            actionBarFullReactionsInfo.setLayoutParams(LayoutHelper.createLinear(
                    width, (int) (minHeight + (maxHeight - minHeight) * percent),
                    Gravity.TOP,
                    0, 0, 0, 0
            ));
        });
        horizontalScrollView.setLayoutParams(LayoutHelper.createFrame(
                width, FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP,
                0, 0, 0, 0
        ));
        horizontalScrollView.addView(linearLayout);
        addView(horizontalScrollView);

        viewBack.setLayoutParams(LayoutHelper.createFrame(
                width, minHeight,
                Gravity.TOP,
                0, 0, 0, 0
        ));
        linearLayout.setLayoutParams(LayoutHelper.createFrame(
                width, minHeight,
                Gravity.TOP,
                0, 0, 0, 0
        ));
    }
}
