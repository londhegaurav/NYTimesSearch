package com.example.glondhe.nytimessearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.glondhe.nytimessearch.R;
import com.example.glondhe.nytimessearch.model.Article;

import java.util.ArrayList;

/**
 * Created by glondhe on 2/14/16.
 */

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvTitle;
        public ImageView imageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

     }
    private ArrayList<Article> mContacts;

    // Pass in the contact array into the constructor
    public RecyclerAdapter(Context context, ArrayList<Article> contacts) {
        mContacts = contacts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
// Get the data model based on position
        Article contact = mContacts.get(position);

        // Set item views based on the data model
        TextView textView = viewHolder.tvTitle;
        textView.setText(contact.getHeadline());

        ImageView imageView = viewHolder.imageView;
        imageView.setImageResource(0);

        String thumbnail = contact.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)){
            Glide.with(context).load(thumbnail).fitCenter().placeholder(R.drawable.clock).error(R.drawable.settinglogo).into(imageView);
        }
        else
            Glide.with(context).load(R.mipmap.ic_launcher1).fitCenter().placeholder(R.drawable.clock).into(imageView);

    }

    public void clearData() {
        int size = this.mContacts.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mContacts.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}