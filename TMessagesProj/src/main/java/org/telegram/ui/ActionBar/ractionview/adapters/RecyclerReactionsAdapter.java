package org.telegram.ui.ActionBar.ractionview.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.ui.ActionBar.ractionview.ReactionsListView;
import org.telegram.ui.ActionBar.ractionview.data.UserReaction;

import java.util.ArrayList;

public class RecyclerReactionsAdapter extends RecyclerView.Adapter<RecyclerReactionsAdapter.ReactionHolder> {

    private final ArrayList<ArrayList<UserReaction>> list = new ArrayList<>();

    @NonNull
    @Override
    public ReactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReactionsListView reactionsListView = new ReactionsListView(parent.getContext());
        reactionsListView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ReactionHolder(reactionsListView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(ArrayList<ArrayList<UserReaction>> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public static class ReactionHolder extends RecyclerView.ViewHolder {

        private final ReactionsListView reactionsListView;

        public ReactionHolder(@NonNull View itemView) {
            super(itemView);
            reactionsListView = (ReactionsListView) itemView;
        }

        public void bind(ArrayList<UserReaction> userReactions) {
            reactionsListView.bind(userReactions);
        }
    }

}
