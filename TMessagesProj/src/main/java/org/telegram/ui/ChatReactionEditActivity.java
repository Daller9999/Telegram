package org.telegram.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatReactionEditActivity extends BaseFragment {

    public ChatReactionEditActivity(Bundle bundle) {
    }

    private TLRPC.ChatFull chatFull;
    private TextCheckCell textCheckCellEnableReaction;
    private TextView textViewAvailableReactions;
    private LinearLayout linearLayoutMain;
    private HashMap<TextCheckCell, TLRPC.TL_availableReaction> cellHashMap = new HashMap<>();

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setTitle("Reactions");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                finishFragment();
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = initGestureBackView(context);
        fragmentView = sizeNotifierFrameLayout;
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        sizeNotifierFrameLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        linearLayoutMain = new LinearLayout(context);
        linearLayoutMain.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayoutMain, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textCheckCellEnableReaction = new TextCheckCell(context);
        textCheckCellEnableReaction.setColors(
                Theme.key_windowBackgroundWhite,
                Theme.key_switchTrack,
                Theme.key_switchTrackChecked,
                Theme.key_windowBackgroundWhite,
                Theme.key_windowBackgroundWhite
        );
        textCheckCellEnableReaction.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundChecked));
        textCheckCellEnableReaction.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(168)));
        textCheckCellEnableReaction.setOnClickListener(v -> {
            textCheckCellEnableReaction.setChecked(!textCheckCellEnableReaction.isChecked());
            int newColor = Theme.getColor(textCheckCellEnableReaction.isChecked() ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked);
            textCheckCellEnableReaction.setBackgroundColor(newColor);
        });
        textCheckCellEnableReaction.setTextAndCheck("Enable Reactions", true, false);
        linearLayoutMain.addView(textCheckCellEnableReaction);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(50)));
        textView.setText("Allow subscribers to react to channel posts.");
        textView.setPadding(AndroidUtilities.dp(20), 0, 0, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        linearLayoutMain.addView(textView);

        textViewAvailableReactions = new TextView(context);
        textViewAvailableReactions.setTextColor(Theme.getColor(Theme.key_windowBackgroundChecked));
        textViewAvailableReactions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textViewAvailableReactions.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        textViewAvailableReactions.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(60)));
        textViewAvailableReactions.setText("Available reactions");
        textViewAvailableReactions.setPadding(AndroidUtilities.dp(20), 0, 0, 0);
        textViewAvailableReactions.setGravity(Gravity.CENTER_VERTICAL);
        linearLayoutMain.addView(textViewAvailableReactions);

        madeRequestToBack();

        return fragmentView;
    }

    private void madeRequestToBack() {
        TLRPC.TL_messages_getAvailableReactions messagesGetAvailableReactions = new TLRPC.TL_messages_getAvailableReactions();
        getConnectionsManager().sendRequest(messagesGetAvailableReactions, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (response != null) {
                TLRPC.TL_messages_availableReactions availableReactions = (TLRPC.TL_messages_availableReactions) response;
                TextCheckCell textCheckCell;
                LinearLayout linearLayout;
                BackupImageView imageView;
                for (TLRPC.TL_availableReaction reaction : availableReactions.reactions) {
                    linearLayout = new LinearLayout(linearLayoutMain.getContext());

                    imageView = new BackupImageView(linearLayoutMain.getContext());
                    imageView.setImage(
                            ImageLocation.getForDocument(reaction.static_icon),
                            "50_50",
                            "webp",
                            null,
                            linearLayout
                    );
                    imageView.setLayoutParams(LayoutHelper.createFrame(30, 30, Gravity.LEFT | Gravity.TOP, 20f, 10f, 0f, 0f));
                    linearLayout.addView(imageView);

                    textCheckCell = new TextCheckCell(linearLayoutMain.getContext());
                    linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.addView(textCheckCell);

                    textCheckCell.setTextAndCheck(reaction.title, true, false);
                    textCheckCell.setOnClickListener(this::onClick);
                    cellHashMap.put(textCheckCell, reaction);
                    linearLayoutMain.addView(linearLayout);
                }
            }
        }));
    }

    private void onClick(View view) {
        TextCheckCell textCheckCell = (TextCheckCell) view;
        TLRPC.TL_availableReaction reaction = cellHashMap.get(textCheckCell);
        textCheckCell.setChecked(!textCheckCell.isChecked());
        if (reaction != null) {

        }
    }

    private SizeNotifierFrameLayout initGestureBackView(Context context) {
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {

            private boolean ignoreLayout;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);

                setMeasuredDimension(widthSize, heightSize);

                measureChildWithMargins(actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);

                int keyboardSize = measureKeyboardHeight();
                if (keyboardSize > AndroidUtilities.dp(20)) {
                    ignoreLayout = true;
                    ignoreLayout = false;
                }

                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child == null || child.getVisibility() == GONE || child == actionBar) {
                        continue;
                    }

                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                final int count = getChildCount();

                int paddingBottom = 0;
                setBottomClip(paddingBottom);

                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == GONE) {
                        continue;
                    }
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();

                    int childLeft;
                    int childTop;

                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = Gravity.TOP | Gravity.LEFT;
                    }

                    final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                    final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                    switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                        case Gravity.CENTER_HORIZONTAL:
                            childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                            break;
                        case Gravity.RIGHT:
                            childLeft = r - width - lp.rightMargin;
                            break;
                        case Gravity.LEFT:
                        default:
                            childLeft = lp.leftMargin;
                    }

                    switch (verticalGravity) {
                        case Gravity.TOP:
                            childTop = lp.topMargin + getPaddingTop();
                            break;
                        case Gravity.CENTER_VERTICAL:
                            childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                            break;
                        case Gravity.BOTTOM:
                            childTop = ((b - paddingBottom) - t) - height - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }

                notifyHeightChanged();
            }

            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        sizeNotifierFrameLayout.setOnTouchListener((v, event) -> true);
        return sizeNotifierFrameLayout;
    }

    void setInfo(TLRPC.ChatFull chatFull) {
        this.chatFull = chatFull;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(textCheckCellEnableReaction, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText));
        return themeDescriptions;
    }
}
