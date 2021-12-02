package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class UserReactionView extends FrameLayout {

    private UserReaction userReaction;

    public UserReactionView(@NonNull Context context, UserReaction userReaction) {
        super(context);
        this.userReaction = userReaction;
        init();
    }

    public UserReactionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UserReactionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UserReactionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        BackupImageView avatarImageView = new BackupImageView(getContext());
        avatarImageView.setLayoutParams(LayoutHelper.createFrame(
                40, 40,
                Gravity.TOP | Gravity.LEFT,
                5, 5, 0, 0
        ));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarImageView.setForUserOrChat(userReaction.userChat, avatarDrawable);
        avatarDrawable.setInfo(userReaction.userChat);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(20));
        addView(avatarImageView);

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.LEFT | Gravity.TOP,
                50, 5, 0, 0
        ));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        String text = "";
        if (userReaction.userChat instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) userReaction.userChat;
            text = user.username;
        } else if (userReaction.userChat instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) userReaction.userChat;
            text = chat.title;
        }
        textView.setText(text);
        addView(textView);


        BackupImageView reactionImageView = new BackupImageView(getContext());
        reactionImageView.setImage(
                ImageLocation.getForDocument(userReaction.reaction.static_icon),
                "50_50",
                "webp",
                null,
                reactionImageView
        );
        reactionImageView.setLayoutParams(LayoutHelper.createFrame(
                40, 40,
                Gravity.TOP | Gravity.RIGHT,
                5, 5, 0, 0
        ));
        addView(reactionImageView);
    }
}
