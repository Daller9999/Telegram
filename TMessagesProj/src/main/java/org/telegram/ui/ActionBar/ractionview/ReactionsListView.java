package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class ReactionsListView extends ScrollView {

    private ArrayList<UserReaction> userReactions;

    public ReactionsListView(Context context, ArrayList<UserReaction> userReactions) {
        super(context);
        this.userReactions = userReactions;
        init();
    }

    public ReactionsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactionsListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ReactionsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        UserReactionView userReactionView;
        for (UserReaction userReaction : userReactions) {
            userReactionView = new UserReactionView(getContext(), userReaction);
            userReactionView.setLayoutParams(LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            linearLayout.addView(userReactionView);
        }
    }

}
