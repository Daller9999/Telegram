package org.telegram.ui.Components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.tgnet.TLObject;

import java.util.ArrayList;

public class AvatarsSelectView extends FrameLayout {
//--------------------Part code that works with select of current user or chat to send in group--------------------------------------------------------------------------

    public AvatarsSelectView(@NonNull Context context) {
        super(context);
    }

    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void setCurrentObjects(ArrayList<TLObject> object) {

    }

    private void init() {

    }
}
