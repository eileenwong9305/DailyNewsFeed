package com.example.android.dailynewsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Credit to: https://guides.codepath.com/android/using-the-recyclerview
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private static final String DATE_SEPARATOR = "T";
    private static final String CONTRIBUTOR_SEPARATOR = " in";
    // Store a member variable for the articles
    private List<Article> articles;
    // Store the context for easy access
    private Context context;

    /**
     * Pass in the article array into the constructor
     *
     * @param context
     * @param articles
     */
    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    /**
     * Inflate a layout from XML and returning the holder
     *
     * @param parent   ViewGroup
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.single_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(parent.getContext(), itemView);
    }

    /**
     * Populate data into the item through holder
     *
     * @param holder   ViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Article article = articles.get(position);

        // Display the title of the current article in that TextView
        holder.titleTextView.setText(article.getTitle());

        // Display the section name of the current article in that TextView
        holder.sectionTextView.setText(article.getSection());

        // Format the date and display in that TextView
        String date = formatString(article.getDate(), DATE_SEPARATOR);
        holder.dateTextView.setText(date);

        // Display the contributor of the current article in that TextView if contributor is not
        // empty, else remove that TextView
        if (!TextUtils.isEmpty(article.getContributor())) {
            String contributor = formatString(article.getContributor(), CONTRIBUTOR_SEPARATOR);
            holder.contributorTextView.setText(contributor);
        } else {
            holder.contributorTextView.setVisibility(View.GONE);
        }

        // Display the image of the current article in that ImageView if image is not
        // empty, else remove that ImageView
        if (article.getImage() != null) {
            holder.thumbnailImageView.setImageDrawable(article.getImage());
        } else {
            holder.thumbnailImageView.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total count of items in the list
     *
     * @return total count of items
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    // Clear the data
    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    // Add the new list of articles
    public void addall(List<Article> data) {
        articles.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * Format string
     *
     * @param originalString
     * @param separator
     * @return
     */
    private String formatString(String originalString, String separator) {
        String newString = originalString;
        if (originalString.contains(separator)) {
            newString = originalString.substring(0, originalString.indexOf(separator));
        }
        return newString;
    }

    /**
     * Provide a direct reference to each of the views within a data item
     * Used to cache the views within the item layout for fast access
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        public TextView sectionTextView;
        public TextView dateTextView;
        public TextView contributorTextView;
        public ImageView thumbnailImageView;

        private Context context;

        // Create a constructor that accepts the entire item row and does the view lookups
        // to find each subview
        public ViewHolder(Context context, View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.title_text);
            sectionTextView = (TextView) v.findViewById(R.id.section_text);
            dateTextView = (TextView) v.findViewById(R.id.date_text);
            contributorTextView = (TextView) v.findViewById(R.id.contributor_text);
            thumbnailImageView = (ImageView) v.findViewById(R.id.thumbnail_image);

            // Store context
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View v) {
            // Gets item position
            int position = getAdapterPosition();
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if (position != RecyclerView.NO_POSITION) {
                // Launch web page linked to article
                Uri webpage = Uri.parse(articles.get(position).getWebUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(webIntent);
                }
            }
        }
    }

}
