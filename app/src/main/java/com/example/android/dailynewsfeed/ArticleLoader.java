package com.example.android.dailynewsfeed;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Eileen on 10/26/2017.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<Article> loadInBackground() {
        if (url == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of articles.
        return QueryUtils.fetchArticleData(url);
    }

    @Override
    protected void onStartLoading() {
        onForceLoad();
    }
}
