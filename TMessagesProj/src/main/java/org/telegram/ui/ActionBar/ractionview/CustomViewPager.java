package org.telegram.ui.ActionBar.ractionview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class CustomViewPager extends PagerAdapter {

    private Context context;
    private ArrayList<ArrayList<UserReaction>> userReactions;

    CustomViewPager(Context context, ArrayList<ArrayList<UserReaction>> userReactions) {
        this.context = context;
        this.userReactions = userReactions;
    }

    @Override
    public int getCount() {
        return userReactions.size();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ReactionsListView reactionsListView = new ReactionsListView(context, userReactions.get(position));
        collection.addView(reactionsListView, 0);
        return reactionsListView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
