package org.telegram.ui.Components.avatarselectview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class AvatarsSelectView extends LinearLayout {

    private AvatarsAdapter avatarsAdapter;
    private Theme.ResourcesProvider resourcesProvider;
    private RecyclerView recyclerView;

    public AvatarsSelectView(@NonNull Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        init(resourcesProvider);
    }

    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs, Theme.ResourcesProvider resourcesProvider) {
        super(context, attrs);
        init(resourcesProvider);
    }

    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, Theme.ResourcesProvider resourcesProvider) {
        super(context, attrs, defStyleAttr);
        init(resourcesProvider);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AvatarsSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, Theme.ResourcesProvider resourcesProvider) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(resourcesProvider);
    }

    public void setAvatarSelectCallback(AvatarSelectCallback avatarSelectCallback) {
        avatarsAdapter.setAvatarSelectCallback(avatarSelectCallback);
    }

    public void setCurrentObjects(ArrayList<TLObject> object) {
        avatarsAdapter.submitList(object);
        int size = Math.min(500, object.size() * avatarsAdapter.getItemSize() + 50);
        setLayoutParams(LayoutHelper.createFrame(300, size));
        recyclerView.setLayoutParams(LayoutHelper.createFrameLL(
                300,
                size - 50,
                Gravity.LEFT,
                0,
                -10,
                0,
                0
        ));
    }

    public int getViewHeight() {
        return AndroidUtilities.dp(400);
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }

    private void init(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        avatarsAdapter = new AvatarsAdapter(resourcesProvider);

        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bacground_send_as));
        int width = 300;
        int height = 400;
        setLayoutParams(LayoutHelper.createFrame(width, height));
        setOrientation(VERTICAL);

        int textViewHeight = 50;
        TextView textView = new TextView(getContext());
        textView.setText("Send message as...");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(LayoutHelper.createFrame(
                width,
                textViewHeight,
                Gravity.LEFT,
                20,
                0,
                0,
                0
        ));
        addView(textView);

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(LayoutHelper.createFrame(
                width,
                height - textViewHeight,
                Gravity.CENTER,
                -4,
                0,
                0,
                0
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(avatarsAdapter);
        addView(recyclerView);
    }
}
