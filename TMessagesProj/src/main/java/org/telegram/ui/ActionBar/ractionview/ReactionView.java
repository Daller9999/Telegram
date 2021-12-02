package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.messenger.ImageLocation;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ReactionView extends FrameLayout {

    private BackupImageView backupImageReaction;
    private TextView textView;

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
        setBackground(Theme.createRoundRectDrawable(50, Color.parseColor("#1d368dd0")));
        backupImageReaction = new BackupImageView(getContext());
        backupImageReaction.setLayoutParams(LayoutHelper.createFrame(
                22, 22,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                5, 0, 0, 0
        ));
        addView(backupImageReaction);

        textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundChecked));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLayoutParams(LayoutHelper.createFrame(
                20, 20,
                Gravity.CENTER_VERTICAL | Gravity.LEFT,
                40, 0, 0, 0
        ));
        textView.setTextColor(Color.parseColor("#368dd0"));
        addView(textView);
    }



    public void setReaction(TLRPC.TL_availableReaction availableReaction, int count) {
        backupImageReaction.setImage(
                ImageLocation.getForDocument(availableReaction.static_icon),
                "30_30",
                "webp",
                null,
                this
        );
        textView.setText(String.valueOf(count));
    }
}
