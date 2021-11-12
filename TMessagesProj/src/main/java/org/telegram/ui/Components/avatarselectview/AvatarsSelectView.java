package org.telegram.ui.Components.avatarselectview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Log;

import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.checkerframework.checker.units.qual.A;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class AvatarsSelectView extends FrameLayout {

    private AvatarsAdapter avatarsAdapter;
    private Theme.ResourcesProvider resourcesProvider;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private View viewShadow;
    private int size;
    private int scroll = 0;
    private TextView textView;
    private static final int shadowTop = 4;
    private static final int textViewHeight = 50;

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
        setDefaultHeight();
    }

    public void setDefaultHeight() {
        updateHeight(500);
    }

    public int getSize() {
        return size;
    }

    public void updateHeight(int newSize) {
        size = Math.min(newSize, avatarsAdapter.getItemSize() * avatarsAdapter.getItemCount() + textView.getHeight());
        viewShadow.setY(linearLayout.getTop() + textView.getHeight());
        setLayoutParams(LayoutHelper.createFrame(300, size));
        linearLayout.setLayoutParams(LayoutHelper.createFrame(300, size));
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

        linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(LayoutHelper.createFrame(width, height));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout);

        textView = new TextView(getContext());
        textView.setText("Send message as...");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setLayoutParams(LayoutHelper.createFrameLL(
                width,
                textViewHeight,
                Gravity.NO_GRAVITY,
                20,
                0,
                0,
                0
        ));
        linearLayout.addView(textView);

        viewShadow = new View(getContext());
        viewShadow.setLayoutParams(LayoutHelper.createFrame(width, shadowTop));
        viewShadow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shadow_top));
        viewShadow.setAlpha(0f);
        addView(viewShadow);

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(LayoutHelper.createFrameLL(
                width,
                height - textViewHeight,
                Gravity.CENTER,
                -4,
                0,
                0,
                0
        ));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scroll += dy;
                Log.i("telegramTest", "scroll = " + scroll);
                if (scroll < 100) {
                    viewShadow.setAlpha(scroll / 10f);
                } else {
                    viewShadow.setAlpha(1f);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(avatarsAdapter);
        linearLayout.addView(recyclerView);
    }
}
