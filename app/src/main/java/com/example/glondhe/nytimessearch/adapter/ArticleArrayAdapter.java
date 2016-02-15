package com.example.glondhe.nytimessearch.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.glondhe.nytimessearch.R;
import com.example.glondhe.nytimessearch.model.Article;

import java.util.List;

/**
 * Created by glondhe on 2/12/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles){
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = this.getItem(position);

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)){
            Glide.with(getContext()).load(thumbnail).fitCenter().placeholder(R.drawable.clock).error(R.drawable.settinglogo).into(imageView);
        }
        else
            Glide.with(getContext()).load(R.mipmap.ic_launcher1).fitCenter().placeholder(R.drawable.clock).into(imageView);
        return convertView;
    }
}
