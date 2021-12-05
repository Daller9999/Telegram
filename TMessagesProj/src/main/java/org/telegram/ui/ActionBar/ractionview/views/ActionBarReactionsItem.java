package org.telegram.ui.ActionBar.ractionview.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarReactionsItem extends FrameLayout {

    private TextView textView;
    private TextView subtextView;
    private ImageView imageView;
    private ImageView rightIcon;

    private int textColor;
    private int iconColor;
    private int selectorColor;

    private int itemHeight = 48;
    private Theme.ResourcesProvider resourcesProvider;
    private MessageObject message;
    private int currentAccount;

    public ActionBarReactionsItem(@NonNull Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        init(context, resourcesProvider);
    }

    public ActionBarReactionsItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionBarReactionsItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ActionBarReactionsItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;

        textColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItem);
        iconColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon);
        selectorColor = getThemedColor(Theme.key_dialogButtonSelector);

        updateBackground();
        setPadding(AndroidUtilities.dp(10), 0, AndroidUtilities.dp(18), 0);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        addView(imageView, LayoutHelper.createFrame(30, 30, Gravity.CENTER_VERTICAL | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT)));

        textView = new TextView(context);
        textView.setLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(textView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL,
                -8, 0, 0, 0
        ));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(itemHeight), View.MeasureSpec.EXACTLY));
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setMessage(MessageObject message, ChatActivity chatActivity) {
        this.message = message;
        TLRPC.TL_messageReactions reactions = message.getReactions();
        if (reactions == null) return;

        String text = reactions.results.size() + " Reactions";
        textView.setText(text);
        currentAccount = UserConfig.selectedAccount;
        MessagesController messagesController = chatActivity.getMessagesController();
        showLastReactions(reactions, messagesController);
    }

    private void showLastReactions(TLRPC.TL_messageReactions list, MessagesController messagesController) {
        int i = 0;
        BackupImageView imageView;
        AvatarDrawable avatarDrawable;
        int rightMargin = 0;

        for (TLRPC.TL_messageUserReaction userReaction : list.recent_reactons) {
            if (i == 3) {
                break;
            }

            TLRPC.User user = messagesController.getUser(userReaction.user_id);
            if (user != null) {
                avatarDrawable = new AvatarDrawable();
                imageView = new BackupImageView(getContext());
                imageView.setForUserOrChat(user, avatarDrawable);
                avatarDrawable.setInfo(user);
                imageView.setRoundRadius(AndroidUtilities.dp(20));
                imageView.setLayoutParams(LayoutHelper.createFrame(
                        30, 30,
                        Gravity.RIGHT | Gravity.CENTER_VERTICAL,
                        0, 0, rightMargin, 0
                ));
                addView(imageView);

                rightMargin += 15;
                i++;
            }
        }
    }

    public void setRightIcon(int icon) {
        if (rightIcon == null) {
            rightIcon = new ImageView(getContext());
            rightIcon.setScaleType(ImageView.ScaleType.CENTER);
            rightIcon.setColorFilter(textColor, PorterDuff.Mode.MULTIPLY);
            if (LocaleController.isRTL) {
                rightIcon.setScaleX(-1);
            }
            addView(rightIcon, LayoutHelper.createFrame(24, LayoutHelper.MATCH_PARENT, Gravity.CENTER_VERTICAL | (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT)));
        }
        setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 8 : 18), 0, AndroidUtilities.dp(LocaleController.isRTL ? 18 : 8), 0);
        rightIcon.setImageResource(icon);
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        setTextAndIcon(text, icon, null);
    }

    public void setMultiline() {
        textView.setLines(2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setSingleLine(false);
    }

    public void setTextAndIcon(CharSequence text, int icon, Drawable iconDrawable) {
        textView.setText(text);
        if (icon != 0 || iconDrawable != null) {
            if (iconDrawable != null) {
                imageView.setImageDrawable(iconDrawable);
            } else {
                imageView.setImageResource(icon);
            }
            imageView.setVisibility(VISIBLE);
            textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43), 0, LocaleController.isRTL ? AndroidUtilities.dp(43) : 0, 0);
        } else {
            imageView.setVisibility(INVISIBLE);
            textView.setPadding(0, 0, 0, 0);
        }
    }

    public void setTextColor(int textColor) {
        if (this.textColor != textColor) {
            textView.setTextColor(this.textColor = textColor);
        }
    }

    public void setIconColor(int iconColor) {
        if (this.iconColor != iconColor) {
            imageView.setColorFilter(new PorterDuffColorFilter(this.iconColor = iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setIcon(int resId) {
        imageView.setImageResource(resId);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setSubtextColor(int color) {
        subtextView.setTextColor(color);
    }

    public void setSubtext(String text) {
        if (subtextView == null) {
            subtextView = new TextView(getContext());
            subtextView.setLines(1);
            subtextView.setSingleLine(true);
            subtextView.setGravity(Gravity.LEFT);
            subtextView.setEllipsize(TextUtils.TruncateAt.END);
            subtextView.setTextColor(0xff7C8286);
            subtextView.setVisibility(GONE);
            subtextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            subtextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43), 0, LocaleController.isRTL ? AndroidUtilities.dp(43) : 0, 0);
            addView(subtextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, 0, 10, 0, 0));
        }
        boolean visible = !TextUtils.isEmpty(text);
        boolean oldVisible = subtextView.getVisibility() == VISIBLE;
        if (visible != oldVisible) {
            subtextView.setVisibility(visible ? VISIBLE : GONE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.bottomMargin = visible ? AndroidUtilities.dp(10) : 0;
            textView.setLayoutParams(layoutParams);
        }
        subtextView.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setSelectorColor(int selectorColor) {
        if (this.selectorColor != selectorColor) {
            this.selectorColor = selectorColor;
            updateBackground();
        }
    }

    void updateBackground() {
        int topBackgroundRadius = 6;
        int bottomBackgroundRadius = 6;
        setBackground(Theme.createRadSelectorDrawable(selectorColor, topBackgroundRadius, bottomBackgroundRadius));
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }
}
