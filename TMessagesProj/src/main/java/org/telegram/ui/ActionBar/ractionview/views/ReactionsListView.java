package org.telegram.ui.ActionBar.ractionview.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ractionview.data.UserReaction;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class ReactionsListView extends ScrollView {

    private LinearLayout linearLayout;
    private Theme.ResourcesProvider resourcesProvider;

    public ReactionsListView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(linearLayout);
        this.resourcesProvider = resourcesProvider;
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

    public void bind(ArrayList<UserReaction> userReactions) {
        if (userReactions == null) return;

        linearLayout.removeAllViews();
        UserReactionView userReactionView;
        for (UserReaction userReaction : userReactions) {
            userReactionView = new UserReactionView(getContext(), userReaction, resourcesProvider);
            userReactionView.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 50));
            linearLayout.addView(userReactionView);
        }
    }

}
