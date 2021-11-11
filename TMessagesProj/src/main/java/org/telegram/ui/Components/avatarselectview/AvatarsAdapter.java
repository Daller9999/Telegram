package org.telegram.ui.Components.avatarselectview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ThemeEditorView;

import java.util.ArrayList;

public class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.AvatarHolder> {

    private final Theme.ResourcesProvider resourcesProvider;
    private final ArrayList<TLObject> objects = new ArrayList<>();
    private static final int heightItem = 70;
    private AvatarSelectCallback avatarSelectCallback;

    AvatarsAdapter(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    void setAvatarSelectCallback(AvatarSelectCallback avatarSelectCallback) {
        this.avatarSelectCallback = avatarSelectCallback;
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }

    int getItemSize() {
        return heightItem;
    }

    @NonNull
    @Override
    public AvatarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, heightItem));

        BackupImageView backupImageView = new BackupImageView(parent.getContext());
        int size = heightItem - 20;
        backupImageView.setLayoutParams(LayoutHelper.createFrame(
                size,
                size,
                Gravity.BOTTOM | Gravity.LEFT,
                15,
                10,
                0,
                5
        ));
        backupImageView.setRoundRadius(AndroidUtilities.dp(24));
        frameLayout.addView(backupImageView);

        int textViewTop = 17;
        int textViewLeft = size + 30;
        float height = size / 2f;
        String textStyle = "fonts/rmediun.ttf";

        TextView textViewName = new TextView(parent.getContext());
        textViewName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textViewName.setTextColor(Color.BLACK);
        textViewName.setTypeface(AndroidUtilities.getTypeface(textStyle));
        textViewName.setLayoutParams(LayoutHelper.createFrame(
                parent.getWidth(),
                height,
                Gravity.TOP,
                textViewLeft,
                textViewTop,
                0,
                0
        ));
        frameLayout.addView(textViewName);

        TextView textViewInfo = new TextView(parent.getContext());
        textViewInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textViewInfo.setTextColor(Color.GRAY);
        textViewName.setTypeface(AndroidUtilities.getTypeface(textStyle));
        textViewInfo.setLayoutParams(LayoutHelper.createFrame(
                parent.getWidth(),
                height,
                Gravity.TOP,
                textViewLeft,
                size / 2f + textViewTop,
                0,
                0
        ));
        frameLayout.addView(textViewInfo);

        return new AvatarHolder(frameLayout, backupImageView, textViewName, textViewInfo, avatarSelectCallback, objects);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarHolder holder, int position) {
        holder.bind(objects.get(position), position);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void submitList(ArrayList<TLObject> objects) {
        this.objects.clear();
        this.objects.addAll(objects);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    protected static class AvatarHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final TextView textViewInfo;
        private final BackupImageView backupImageView;
        private final AvatarDrawable avatarDrawable = new AvatarDrawable();
        private int positionCurrent = 0;

        public AvatarHolder(
                @NonNull View itemView,
                BackupImageView backupImageView,
                TextView textViewName,
                TextView textViewInfo,
                AvatarSelectCallback avatarSelectCallback,
                ArrayList<TLObject> objects
        ) {
            super(itemView);
            this.backupImageView = backupImageView;
            this.textViewInfo = textViewInfo;
            this.textViewName = textViewName;
            backupImageView.setImageDrawable(avatarDrawable);
            itemView.setOnClickListener(v -> {
                if (avatarSelectCallback != null) {
                    avatarSelectCallback.onObjectSelect(objects.get(positionCurrent));
                }
            });
        }

        @SuppressLint("SetTextI18n")
        void bind(TLObject object, int positionCurrent) {
            backupImageView.setForUserOrChat(object, avatarDrawable);
            this.positionCurrent = positionCurrent;
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                textViewName.setText(user.username);
                textViewInfo.setText("personal account");
            } else if (object instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) object;
                textViewName.setText(chat.title);
                textViewInfo.setText(chat.participants_count + " subscribers");
            }
        }
    }

}
