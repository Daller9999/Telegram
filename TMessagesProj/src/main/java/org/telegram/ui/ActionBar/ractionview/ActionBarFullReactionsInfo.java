package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.exoplayer2.util.Log;

import org.checkerframework.checker.units.qual.A;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TableLayout;

import java.util.ArrayList;

public class ActionBarFullReactionsInfo extends FrameLayout {

    private OnButtonBack onButtonBack;
    private MessageObject message;
    private LinearLayout layoutReactions;
    private RecyclerView recyclerView;
    private ArrayList<TLRPC.TL_availableReaction> availableReactions;
    private MessagesController messagesController;
    private RecyclerReactionsAdapter adapter;

    public interface OnButtonBack {
        void onBackPressed();
    }

    public ActionBarFullReactionsInfo(@NonNull Context context, OnButtonBack onButtonBack) {
        super(context);
        init();
        this.onButtonBack = onButtonBack;
    }

    public ActionBarFullReactionsInfo(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionBarFullReactionsInfo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ActionBarFullReactionsInfo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutParams(LayoutHelper.createFrame(270, LayoutHelper.WRAP_CONTENT));
        setBackground(Theme.createRoundRectDrawable(10, Color.WHITE));
        Button buttonBack = new Button(getContext());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_ab_back);
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        buttonBack.setBackground(drawable);
        buttonBack.setLayoutParams(LayoutHelper.createFrame(
                30, 30,
                Gravity.TOP | Gravity.LEFT,
                5, 5, 0, 0
        ));
        addView(buttonBack);

        TextView textView = new TextView(getContext());
        textView.setText("Back");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT,
                45, 8, 0, 0
        ));
        addView(textView);

        View viewBack = new View(getContext());
        viewBack.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 40,
                Gravity.TOP | Gravity.LEFT,
                0, 0, 0, 0
        ));
        viewBack.setOnClickListener(v -> {
            setVisibility(View.GONE);
            if (onButtonBack != null) {
                onButtonBack.onBackPressed();
            }
        });
        addView(viewBack);

        layoutReactions = new LinearLayout(getContext());
        layoutReactions.setOrientation(LinearLayout.HORIZONTAL);
        layoutReactions.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 50));
        layoutReactions.setHorizontalScrollBarEnabled(false);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        horizontalScrollView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, 40 , 0, 0
        ));
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.addView(layoutReactions);
        addView(horizontalScrollView);

        View view = new View(getContext());
        view.setBackgroundColor(Color.GRAY);
        view.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 2,
                Gravity.TOP | Gravity.LEFT,
                0, 51, 0, 0
        ));
        addView(view);

        adapter = new RecyclerReactionsAdapter();
        recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                50, 0, 0, 0
        ));
        addView(recyclerView);
    }

    public void setMessage(
            MessageObject message,
            ChatActivity chatActivity,
            long chatId,
            ArrayList<TLRPC.TL_availableReaction> availableReactions
    ) {
        this.message = message;
        this.availableReactions = availableReactions;
        TLRPC.TL_messageReactions reactions = message.getReactions();

        messagesController = chatActivity.getMessagesController();
        ConnectionsManager connectionsManager = chatActivity.getConnectionsManager();

        TLRPC.TL_messages_getMessageReactionsList getMessageReactionsList = new TLRPC.TL_messages_getMessageReactionsList();
        getMessageReactionsList.id = message.getId();
        getMessageReactionsList.peer = messagesController.getInputPeer(chatId);
        connectionsManager.sendRequest(getMessageReactionsList, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (response != null) {
                TLRPC.TL_messages_messageReactionsList list = (TLRPC.TL_messages_messageReactionsList) response;
                updateList(reactions, list);
                Log.i("telegramTest", "response get reaction list success");
            }
            if (error != null) {
                Log.i("telegramTest", "response get reaction list failed: " + error.text);
            }
        }));
    }

    private void updateList(TLRPC.TL_messageReactions reactions, TLRPC.TL_messages_messageReactionsList list) {
        int i = 0;
        for (TLRPC.TL_reactionCount reactionCount : reactions.results) {
            ReactionView reactionView = new ReactionView(getContext());
            for (TLRPC.TL_availableReaction availableReaction : availableReactions) {
                if (reactionCount.reaction.equals(availableReaction.reaction)) {
                    reactionView.setReaction(availableReaction, reactionCount.count);
                }
            }
            reactionView.setLayoutParams(LayoutHelper.createLinear(
                    60, 30,
                    Gravity.RIGHT | Gravity.LEFT,
                    i == 0 ? 20 : 5,
                    0,
                    i == reactions.results.size() - 1 ? 20 : 5,
                    0
            ));
            layoutReactions.addView(reactionView);
            i++;
        }

        ArrayList<UserReaction> userReactions = new ArrayList<>();
        for (TLRPC.TL_messageUserReaction userReaction : list.reactions) {
            UserReaction userReactionNew = new UserReaction();
            userReactionNew.reaction = getReaction(userReaction.reaction);
            userReactionNew.userChat = getUserOrChat(userReaction.user_id);
            userReactions.add(userReactionNew);
        }
        ArrayList<ArrayList<UserReaction>> userReactionList = new ArrayList<>();
        userReactionList.add(userReactions);
        adapter.submitList(userReactionList);
    }

    private TLRPC.TL_availableReaction getReaction(String reaction) {
        for (TLRPC.TL_availableReaction availableReaction : availableReactions) {
            if (availableReaction.reaction.equals(reaction)) {
                return availableReaction;
            }
        }
        return null;
    }

    private TLObject getUserOrChat(long id) {
        TLRPC.Chat chat = messagesController.getChat(id);
        TLRPC.User user = messagesController.getUser(id);
        return chat == null ? user : chat;
    }
}
