package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ReactionsListView extends LinearLayout {


    public ReactionsListView(Context context) {
        super(context);
    }

    public ReactionsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReactionsListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ReactionsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
