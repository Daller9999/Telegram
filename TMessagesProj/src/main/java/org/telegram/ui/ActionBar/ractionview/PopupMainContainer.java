package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ractionview.views.ActionBarFullReactionsInfo;
import org.telegram.ui.ActionBar.ractionview.views.ActionBarReactionsItem;
import org.telegram.ui.ActionBar.ractionview.views.ReactionsListView;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.ActionBar.ractionview.views.ReactionSelectView;

import java.util.ArrayList;

public class PopupMainContainer extends FrameLayout {

    private static final int width = 260;

    private LinearLayout scrimPopupContainerLayout;
    private ActionBarFullReactionsInfo actionBarFullReactionsInfo;
    private ActionBarReactionsItem actionBarReactionsItem;
    private ArrayList<TLRPC.TL_availableReaction> availableReactions;
    private ReactionSelectView reactionSelectView = null;
    private MessageObject messageObject;
    private SendMessagesHelper sendMessagesHelper;
    private ChatActivity chatActivity;
    private ActionBarPopupWindow actionBarPopupWindow;

    public PopupMainContainer(
            @NonNull Context context,
            MessageObject messageObject,
            ChatActivity chatActivity,
            LinearLayout scrimPopupContainerLayout,
            ActionBarFullReactionsInfo actionBarFullReactionsInfo,
            ActionBarReactionsItem actionBarReactionsItem,
            ArrayList<TLRPC.TL_availableReaction> availableReactions
    ) {
        super(context);
        this.messageObject = messageObject;
        this.chatActivity = chatActivity;
        sendMessagesHelper = chatActivity.getSendMessagesHelper();
        this.scrimPopupContainerLayout = scrimPopupContainerLayout;
        this.actionBarFullReactionsInfo = actionBarFullReactionsInfo;
        this.actionBarReactionsItem = actionBarReactionsItem;
        this.availableReactions = availableReactions;
        init();
    }

    public void setActionBarPopupWindow(ActionBarPopupWindow actionBarPopupWindow) {
        this.actionBarPopupWindow = actionBarPopupWindow;
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
        setLayoutParams(LayoutHelper.createFrame(300, LayoutHelper.WRAP_CONTENT));
        Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        Rect backgroundPaddings = new Rect();
        shadowDrawable.getPadding(backgroundPaddings);

        reactionSelectView = null;
        if (!availableReactions.isEmpty()) {
            reactionSelectView = new ReactionSelectView(getContext());
            reactionSelectView.setLayoutParams(LayoutHelper.createFrame(
                    width + 40, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP | Gravity.LEFT,
                    0, 0, 0, 0
            ));
            reactionSelectView.setReactions(availableReactions);
            addView(reactionSelectView);
            reactionSelectView.setOnReactionCallBack(reaction -> {
                sendMessagesHelper.sendReaction(messageObject, reaction, chatActivity);
                actionBarPopupWindow.dismiss();
            });
        }
        int margin = reactionSelectView == null ? 0 : 80;
        int length = 0;

        addView(scrimPopupContainerLayout);
        scrimPopupContainerLayout.setLayoutParams(LayoutHelper.createFrame(
                width + length, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, margin, 0, 0
        ));
        addView(actionBarFullReactionsInfo);
        actionBarFullReactionsInfo.setVisibility(GONE);
        actionBarFullReactionsInfo.setLayoutParams(LayoutHelper.createFrame(
                width + length, 400,
                Gravity.TOP | Gravity.LEFT,
                0, 80, 0, 0
        ));
        actionBarFullReactionsInfo.setOnButtonBack(new ActionBarFullReactionsInfo.OnButtonBack() {
            @Override
            public void onBackPressed() {
                scrimPopupContainerLayout.setVisibility(View.VISIBLE);
                actionBarFullReactionsInfo.setVisibility(View.GONE);
                if (reactionSelectView != null) {
                    reactionSelectView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDataLoaded(int count) {
                int size = Math.min(count * 50 + (count <= 2 ? 80 : 120), 400);
                actionBarFullReactionsInfo.setLayoutParams(LayoutHelper.createFrame(
                        width + length, size,
                        Gravity.TOP | Gravity.LEFT,
                        0, 80, 0, 0
                ));
            }
        });

        if (actionBarReactionsItem != null) {
            actionBarReactionsItem.setOnClickListener(v -> {
                scrimPopupContainerLayout.setVisibility(View.GONE);
                actionBarFullReactionsInfo.setVisibility(View.VISIBLE);
                if (reactionSelectView != null) {
                    reactionSelectView.setVisibility(View.GONE);
                }
            });
        }
    }
}
