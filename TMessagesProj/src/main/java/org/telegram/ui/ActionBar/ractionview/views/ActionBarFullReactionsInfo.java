package org.telegram.ui.ActionBar.ractionview.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

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
import org.telegram.ui.ActionBar.ractionview.adapters.RecyclerReactionsAdapter;
import org.telegram.ui.ActionBar.ractionview.adapters.RecyclerReactionsItemsAdapter;
import org.telegram.ui.ActionBar.ractionview.data.TotalReaction;
import org.telegram.ui.ActionBar.ractionview.data.UserReaction;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionBarFullReactionsInfo extends FrameLayout {

    private OnButtonBack onButtonBack;
    private MessageObject message;
    private RecyclerView recyclerViewReactions;
    private RecyclerView recyclerView;
    private ArrayList<TLRPC.TL_availableReaction> availableReactions;
    private MessagesController messagesController;
    private RecyclerReactionsAdapter adapter;
    private RecyclerReactionsItemsAdapter reactionsItemsAdapter;
    private long lastTimeClick = 0;

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
        /*setLayoutParams(LayoutHelper.createLinear(
                270, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT,
                5, 80, 30, 5
        ));*/

        Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        Rect backgroundPaddings = new Rect();
        shadowDrawable.getPadding(backgroundPaddings);
        setBackground(shadowDrawable);

        Button buttonBack = new Button(getContext());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_ab_back);
        drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        buttonBack.setBackground(drawable);
        buttonBack.setLayoutParams(LayoutHelper.createFrame(
                30, 30,
                Gravity.TOP | Gravity.LEFT,
                10, 10, 0, 0
        ));
        addView(buttonBack);

        TextView textView = new TextView(getContext());
        textView.setText("Back");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT,
                50, 10, 0, 0
        ));
        addView(textView);

        View viewBack = new View(getContext());
        viewBack.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 71,
                Gravity.TOP | Gravity.LEFT,
                0, 0, 0, 0
        ));
        viewBack.setOnClickListener(v -> {
            if (onButtonBack != null) {
                onButtonBack.onBackPressed();
            }
        });
        addView(viewBack);

        reactionsItemsAdapter = new RecyclerReactionsItemsAdapter(pos -> {
            recyclerView.smoothScrollToPosition(pos);
            lastTimeClick = System.currentTimeMillis();
        });
        recyclerViewReactions = new RecyclerView(getContext());
        recyclerViewReactions.setAdapter(reactionsItemsAdapter);
        recyclerViewReactions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewReactions.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 50,
                Gravity.TOP,
                0, 50, 0, 0
        ));
        recyclerViewReactions.setHorizontalScrollBarEnabled(false);
        addView(recyclerViewReactions);

        View view = new View(getContext());
        view.setBackgroundColor(Color.GRAY);
        view.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 1,
                Gravity.TOP | Gravity.LEFT,
                0, 91, 0, 0
        ));
        addView(view);

        adapter = new RecyclerReactionsAdapter();
        recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, 92, 0, 0
        ));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int pos = linearLayoutManager.findFirstVisibleItemPosition();
                if (System.currentTimeMillis() - lastTimeClick > 500) {
                    recyclerViewReactions.smoothScrollToPosition(pos);
                    reactionsItemsAdapter.setActivePosition(pos);
                }
            }
        });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
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
        ArrayList<TotalReaction> totalReactions = new ArrayList<>();
        totalReactions.add(getAllReactionView(reactions));
        ArrayList<TLRPC.TL_availableReaction> currentReaction = new ArrayList<>();
        HashMap<TLRPC.TL_availableReaction, ArrayList<UserReaction>> hashMap = new HashMap<>();
        for (TLRPC.TL_reactionCount reactionCount : reactions.results) {
            for (TLRPC.TL_availableReaction availableReaction : availableReactions) {
                if (reactionCount.reaction.equals(availableReaction.reaction)) {
                    currentReaction.add(availableReaction);
                    TotalReaction totalReaction = new TotalReaction();
                    totalReaction.reaction = availableReaction;
                    totalReaction.count = reactionCount.count;
                    totalReactions.add(totalReaction);
                }
            }
            i++;
        }
        reactionsItemsAdapter.submitList(totalReactions);

        ArrayList<UserReaction> userReactions = new ArrayList<>();
        for (TLRPC.TL_messageUserReaction userReaction : list.reactions) {
            UserReaction userReactionNew = new UserReaction();
            TLObject userOrChat = getUserOrChat(userReaction.user_id);
            TLRPC.TL_availableReaction availableReaction = getReaction(userReaction.reaction);
            userReactionNew.reaction = availableReaction;
            userReactionNew.userChat = userOrChat;
            userReactions.add(userReactionNew);
            if (!hashMap.containsKey(availableReaction)) {
                hashMap.put(availableReaction, new ArrayList<>());
            }
            hashMap.get(availableReaction).add(userReactionNew);
        }
        ArrayList<ArrayList<UserReaction>> userReactionList = new ArrayList<>();
        userReactionList.add(userReactions);

        for (TLRPC.TL_availableReaction reaction : currentReaction) {
            userReactionList.add(hashMap.get(reaction));
        }
        adapter.submitList(userReactionList);

        int size = Math.min(userReactions.size(), 7);
        // setLayoutParams(LayoutHelper.createLinear(250, 80 + 50 * size));
    }

    private TotalReaction getAllReactionView(TLRPC.TL_messageReactions reactions) {
        int count = 0;
        for (TLRPC.TL_reactionCount reaction : reactions.results) {
            count += reaction.count;
        }
        TotalReaction totalReaction = new TotalReaction();
        totalReaction.reaction = null;
        totalReaction.count = count;
        totalReaction.isSelected = true;
        return totalReaction;
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
