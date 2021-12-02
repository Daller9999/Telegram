package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ReactionView extends FrameLayout {

    private BackupImageView backupImageReaction;
    private TextView textView;
    private View viewMain;
    private View viewSelected;
    private static final int margin = 3;
    private ImageView imageView;
    private static final int marginBorder = 10;
    private static final int marginText = 35;

    public ReactionView(@NonNull Context context) {
        super(context);
        init();
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

    private void init() {
        viewSelected = new View(getContext());
        viewSelected.setLayoutParams(LayoutHelper.createFrame(
                68, 33,
                Gravity.LEFT | Gravity.RIGHT,
                margin, 0, margin, 0
        ));
        viewSelected.setBackground(Theme.createRoundRectDrawable(50, Color.parseColor("#ff579ed9")));
        viewSelected.setVisibility(View.GONE);
        addView(viewSelected);

        viewMain = new View(getContext());
        viewMain.setLayoutParams(LayoutHelper.createFrame(
                64, 29,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + 2, 2, margin + 2, 0
        ));
        viewMain.setBackground(Theme.createRoundRectDrawable(50, Color.parseColor("#D6E8F5")));
        addView(viewMain);

        imageView = new ImageView(getContext());
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.msg_reactions_filled));
        imageView.setColorFilter(Color.parseColor("#ff579ed9"));
        imageView.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        imageView.setVisibility(View.GONE);
        addView(imageView);

        backupImageReaction = new BackupImageView(getContext());
        backupImageReaction.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        addView(backupImageReaction);

        textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundChecked));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, 20,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + marginText, 5, 10, 0
        ));
        textView.setTextColor(Color.parseColor("#368dd0"));
        addView(textView);
    }

    public void setActive(boolean isActive) {
        viewSelected.setVisibility(isActive ? View.VISIBLE : View.GONE);
    }

    public void setMarginLeft() {
        viewSelected.setLayoutParams(LayoutHelper.createFrame(
                68, 33,
                Gravity.LEFT | Gravity.RIGHT,
                margin + marginBorder, 0, margin, 0
        ));
        viewMain.setLayoutParams(LayoutHelper.createFrame(
                64, 29,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + 2 + marginBorder, 2, margin + 2, 0
        ));
        imageView.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10 + marginBorder, 0, 0, 0
        ));
        backupImageReaction.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10 + marginBorder, 0, 0, 0
        ));
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, 20,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + marginText + marginBorder, 5, 10, 0
        ));
    }

    public void setMarginRight() {
        viewSelected.setLayoutParams(LayoutHelper.createFrame(
                68, 33,
                Gravity.LEFT | Gravity.RIGHT,
                margin, 0, margin + marginBorder, 0
        ));
        viewMain.setLayoutParams(LayoutHelper.createFrame(
                64, 29,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + 2, 2, margin + 2 + marginBorder, 0
        ));
        imageView.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        backupImageReaction.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, 20,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + marginText, 5, 10 + marginBorder, 0
        ));
    }

    public void setMarginDefault() {
        viewSelected.setLayoutParams(LayoutHelper.createFrame(
                68, 33,
                Gravity.LEFT | Gravity.RIGHT,
                margin, 0, margin, 0
        ));
        viewMain.setLayoutParams(LayoutHelper.createFrame(
                64, 29,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + 2, 2, margin + 2, 0
        ));
        imageView.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        backupImageReaction.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                margin + 10, 0, 0, 0
        ));
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, 20,
                Gravity.TOP | Gravity.LEFT | Gravity.RIGHT,
                margin + marginText, 5, 10, 0
        ));
    }


    public void setReaction(TLRPC.TL_availableReaction availableReaction, int count) {
        backupImageReaction.setImage(
                ImageLocation.getForDocument(availableReaction.static_icon),
                "30_30",
                "webp",
                null,
                this
        );
        backupImageReaction.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        textView.setText(String.valueOf(count));
    }

    public void setDefault(int count) {
        backupImageReaction.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        textView.setText(String.valueOf(count));
    }
}
