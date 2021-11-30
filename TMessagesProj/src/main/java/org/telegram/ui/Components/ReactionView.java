package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.messenger.ImageLocation;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

import java.util.ArrayList;

public class ReactionView extends FrameLayout {


    public interface OnReactionCallBack {
        void onReactionClicked(String reaction);
    }

    private Theme.ResourcesProvider resourcesProvider;
    private final HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
    private final LinearLayout layoutReactions = new LinearLayout(getContext());
    private View viewBack;
    private View viewBackShadow;
    private OnReactionCallBack onReactionCallBack;

    public ReactionView(@NonNull Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        init();
        this.resourcesProvider = resourcesProvider;
    }

    public ReactionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ReactionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setOnReactionCallBack(OnReactionCallBack onReactionCallBack) {
        this.onReactionCallBack = onReactionCallBack;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setReactions(ArrayList<TLRPC.TL_availableReaction> reactions) {
        layoutReactions.removeAllViews();
        if (reactions.size() < 7) {
            int widthBack;
            int widthHorizontal;
            if (reactions.size() == 1) {
                widthBack = 70;
                widthHorizontal = 45;
            } else if (reactions.size() == 2) {
                widthBack = 100;
                widthHorizontal = 80;
            } else {
                widthBack = (int) (39 * reactions.size());
                widthHorizontal = (int) (35.5 * reactions.size());
            }
            viewBack.setLayoutParams(LayoutHelper.createFrame(
                    widthBack - 2,
                    50 - 2,
                    Gravity.BOTTOM | Gravity.RIGHT,
                    2,
                    2,
                    1,
                    22
            ));
            viewBackShadow.setLayoutParams(LayoutHelper.createFrame(
                    widthBack,
                    50,
                    Gravity.BOTTOM | Gravity.RIGHT,
                    0,
                    0,
                    0,
                    20
            ));
            horizontalScrollView.setLayoutParams(LayoutHelper.createFrame(
                    widthHorizontal,
                    50,
                    Gravity.BOTTOM | Gravity.TOP | Gravity.RIGHT,
                    10,
                    23,
                    10,
                    22
            ));
        }
        BackupImageView imageView;
        for (TLRPC.TL_availableReaction reaction : reactions) {
            imageView = new BackupImageView(getContext());
            imageView.setImage(
                    ImageLocation.getForDocument(reaction.static_icon),
                    "50_50",
                    "webp",
                    null,
                    layoutReactions
            );
            imageView.setOnClickListener(v -> {
                if (onReactionCallBack != null) {
                    onReactionCallBack.onReactionClicked(reaction.reaction);
                }
            });
            imageView.setLayoutParams(LayoutHelper.createFrame(30, 30, Gravity.LEFT | Gravity.TOP, 5f, 5f, 0f, 0f));
            layoutReactions.addView(imageView);
        }
    }

    private void init() {
        int colorShadow = Color.parseColor("#1a000000");
        View viewRoundShadow = new View(getContext());
        viewRoundShadow.setBackground(Theme.createRoundRectDrawable(40, colorShadow));
        viewRoundShadow.setLayoutParams(LayoutHelper.createFrame(22, 22, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 23, 12));
        addView(viewRoundShadow);

        View viewSmallRoundShadow = new View(getContext());
        viewSmallRoundShadow.setBackground(Theme.createRoundRectDrawable(40, colorShadow));
        viewSmallRoundShadow.setLayoutParams(LayoutHelper.createFrame(12, 12, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 22, 0));
        addView(viewSmallRoundShadow);

        viewBackShadow = new View(getContext());
        viewBackShadow.setBackground(Theme.createRoundRectDrawable(90, colorShadow));
        viewBackShadow.setLayoutParams(LayoutHelper.createFrame(262, 52, Gravity.BOTTOM, 0, 0, 0, 20));
        addView(viewBackShadow);


        View viewRound = new View(getContext());
        viewRound.setBackground(Theme.createRoundRectDrawable(40, Color.WHITE));
        viewRound.setLayoutParams(LayoutHelper.createFrame(18, 18, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 25, 14));
        addView(viewRound);

        viewBack = new View(getContext());
        viewBack.setBackground(Theme.createRoundRectDrawable(90, Color.WHITE));
        viewBack.setLayoutParams(LayoutHelper.createFrame(
                258,
                48,
                Gravity.BOTTOM | Gravity.RIGHT,
                2,
                2,
                1,
                22));
        addView(viewBack);


        View viewSmallRound = new View(getContext());
        viewSmallRound.setBackground(Theme.createRoundRectDrawable(40, Color.WHITE));
        viewSmallRound.setLayoutParams(LayoutHelper.createFrame(8, 8, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 24, 2));
        addView(viewSmallRound);

        horizontalScrollView.setLayoutParams(LayoutHelper.createFrame(
                240,
                50,
                Gravity.BOTTOM | Gravity.TOP | Gravity.RIGHT,
                10,
                23,
                10,
                22
        ));
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        layoutReactions.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        horizontalScrollView.addView(layoutReactions);

        addView(horizontalScrollView);
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }
}

