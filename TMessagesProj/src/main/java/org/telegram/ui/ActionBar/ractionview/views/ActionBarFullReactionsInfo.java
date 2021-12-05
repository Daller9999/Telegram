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
import android.widget.LinearLayout;
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
    private Theme.ResourcesProvider resourcesProvider;
    private int maxSize;
    private View viewDivide;
    private View viewDivideGray;

    public interface OnButtonBack {
        void onBackPressed();
        void onDataLoaded(int count);
    }

    public ActionBarFullReactionsInfo(@NonNull Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        init();
        this.resourcesProvider = resourcesProvider;
    }

    public void setOnButtonBack(OnButtonBack onButtonBack) {
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
        Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        Rect backgroundPaddings = new Rect();
        shadowDrawable.getPadding(backgroundPaddings);
        setBackground(shadowDrawable);

        int color = getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon);
        Button buttonBack = new Button(getContext());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_ab_back);
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        buttonBack.setBackground(drawable);
        buttonBack.setLayoutParams(LayoutHelper.createFrame(
                27, 27,
                Gravity.TOP | Gravity.LEFT,
                12, 12, 0, 0
        ));
        addView(buttonBack);

        TextView textView = new TextView(getContext());
        textView.setText("Back");
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | Gravity.LEFT,
                58, 12, 0, 0
        ));
        addView(textView);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, 50, 0, 0
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

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
        recyclerViewReactions.setNestedScrollingEnabled(false);
        recyclerViewReactions.setAdapter(reactionsItemsAdapter);
        recyclerViewReactions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewReactions.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 50,
                Gravity.TOP,
                0, 0, 0, 0
        ));
        recyclerViewReactions.setHorizontalScrollBarEnabled(false);
        linearLayout.addView(recyclerViewReactions);

        viewDivide = new View(getContext());
        viewDivide.setBackgroundColor(color);
        viewDivide.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 1,
                Gravity.TOP | Gravity.LEFT,
                0, 0, 0, 0
        ));
        linearLayout.addView(viewDivide);

        viewDivideGray = new View(getContext());
        viewDivideGray.setBackgroundColor(color);
        viewDivideGray.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, 1,
                Gravity.TOP | Gravity.LEFT,
                0, 0, 0, 0
        ));
        viewDivideGray.setVisibility(View.GONE);
        viewDivideGray.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 10));
        viewDivideGray.setBackgroundColor(getThemedColor(Theme.key_dialogBackgroundGray));
        linearLayout.addView(viewDivideGray);

        adapter = new RecyclerReactionsAdapter(resourcesProvider);
        recyclerView = new RecyclerView(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP,
                0, 0, 0, 0
        ));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerViewReactions.getVisibility() == View.GONE) return;

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
        linearLayout.addView(recyclerView);
        addView(linearLayout);
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
        getMessageReactionsList.limit = 100;
        connectionsManager.sendRequest(getMessageReactionsList, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (response != null) {
                TLRPC.TL_messages_messageReactionsList list = (TLRPC.TL_messages_messageReactionsList) response;
                if (reactions == null) return;

                updateList(reactions, list);
                Log.i("telegramTest", "response get reaction list success");
            }
            if (error != null) {
                Log.i("telegramTest", "response get reaction list failed: " + error.text);
            }
        }));
    }

    private void updateList(TLRPC.TL_messageReactions reactions, TLRPC.TL_messages_messageReactionsList list) {
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
        }
        if (totalReactions.size() <= 2) {
            viewDivide.setVisibility(View.GONE);
            viewDivideGray.setVisibility(View.VISIBLE);
            recyclerViewReactions.setVisibility(View.GONE);
        } else {
            reactionsItemsAdapter.submitList(totalReactions);
        }

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

        maxSize = totalReactions.get(0).count;
        ArrayList<UserReaction> arrayList;
        for (TLRPC.TL_availableReaction reaction : currentReaction) {
            arrayList = hashMap.get(reaction);
            if (arrayList != null && arrayList.size() > maxSize) {
                maxSize = arrayList.size();
            }
            userReactionList.add(arrayList);
        }
        if (onButtonBack != null) {
            onButtonBack.onDataLoaded(maxSize);
        }
        adapter.submitList(userReactionList);
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

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }
}
