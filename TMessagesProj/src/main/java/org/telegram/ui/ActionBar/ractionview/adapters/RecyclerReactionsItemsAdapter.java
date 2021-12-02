package org.telegram.ui.ActionBar.ractionview.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.ui.ActionBar.ractionview.ReactionView;
import org.telegram.ui.ActionBar.ractionview.data.TotalReaction;

import java.util.ArrayList;

public class RecyclerReactionsItemsAdapter extends RecyclerView.Adapter<RecyclerReactionsItemsAdapter.ReactionViewHolder> {

    public interface OnReactionClicked {
        void onClicked(int pos);
    }

    private interface OnItemClicked {
        void onClick(int pos);
    }

    private final ArrayList<TotalReaction> totalReactions = new ArrayList<>();
    private final OnReactionClicked onReactionClicked;

    public RecyclerReactionsItemsAdapter(OnReactionClicked onReactionClicked) {
        this.onReactionClicked = onReactionClicked;
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReactionView reactionView = new ReactionView(parent.getContext());
        reactionView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ReactionViewHolder(reactionView, this::onItemClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        holder.bind(totalReactions.get(position));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(ArrayList<TotalReaction> totalReactions) {
        this.totalReactions.clear();
        this.totalReactions.addAll(totalReactions);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onItemClicked(int pos) {
        onReactionClicked.onClicked(pos);
        setActivePosition(pos);
    }

    @Override
    public int getItemCount() {
        return totalReactions.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setActivePosition(int position) {
        setAllTotalFalse();
        totalReactions.get(position).isSelected = true;
        notifyDataSetChanged();
    }


    private void setAllTotalFalse() {
        for (TotalReaction totalReaction : totalReactions) {
            totalReaction.isSelected = false;
        }
    }

    public static class ReactionViewHolder extends RecyclerView.ViewHolder {

        private final ReactionView reactionView;

        public ReactionViewHolder(@NonNull View itemView, OnItemClicked onItemClicked) {
            super(itemView);
            reactionView = (ReactionView) itemView;
            reactionView.setOnClickListener(v -> onItemClicked.onClick(getAdapterPosition()));
        }

        public void bind(TotalReaction totalReaction) {
            if (totalReaction.reaction == null) {
                reactionView.setDefault(totalReaction.count);
            } else {
                reactionView.setReaction(totalReaction.reaction, totalReaction.count);
            }
            reactionView.setActive(totalReaction.isSelected);
        }
    }
}
