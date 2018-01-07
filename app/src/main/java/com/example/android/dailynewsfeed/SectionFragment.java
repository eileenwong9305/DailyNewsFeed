package com.example.android.dailynewsfeed;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SectionFragment extends Fragment implements LoaderCallbacks<List<Article>>, SwipeRefreshLayout.OnRefreshListener {

    /**
     * Base url for query
     */
    private static final String BASE_URL = "https://content.guardianapis.com/search?";
    /**
     * Constant value for the article loader ID
     */
    private static final int ARTICLE_LOADER_ID = 0;
    /**
     * Adapter for the list of articles
     */
    private ArticleAdapter adapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * View layout
     */
    private View view;
    /**
     * Swipe refresh layout for refreshing view layout
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Section name extracted from fragment pager adapter
     */
    private String section;

    /**
     * Obtain section name from fragment pager adapter
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            section = b.getString("section");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        view = inflater.inflate(R.layout.fragment_section, container, false);

        // Find reference to swipeRefreshLayoutSet and set refreshlistener on it
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Find reference to the empty state TextView in the layout
        mEmptyStateTextView = (TextView) view.findViewById(R.id.empty_state_text);

        // Find reference to the recyclerView in the layout
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // Set layout manager to position the items
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Create adapter passing in the sample user data
        adapter = new ArticleAdapter(getContext(), new ArrayList<Article>());

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);

        // Displays dividers between each item within the list
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // Check internet connection and start loader to fetch book data and update the adapter.
        // If there is a network connection, fetch data
        if (isConnected()) {
            LoaderManager loadermanager = getLoaderManager();
            loadermanager.initLoader(ARTICLE_LOADER_ID, null, this);

        } else {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Check if there is internet connection
     */
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        // Get parameter from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String productionOffice = sharedPreferences.getString(
                getString(R.string.settings_production_by_key),
                getString(R.string.settings_production_by_default));
        String searchTerm = sharedPreferences.getString(
                getString(R.string.settings_search_term_key), "");

        // Create query uri and add on parameter
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuider = baseUri.buildUpon();

        uriBuider.appendQueryParameter("format", "json");
        if (!TextUtils.isEmpty(section)) {
            uriBuider.appendQueryParameter("section", section);
        }
        if (!TextUtils.isEmpty(productionOffice)) {
            uriBuider.appendQueryParameter("production-office", productionOffice);
        }
        if (!TextUtils.isEmpty(productionOffice)) {
            uriBuider.appendQueryParameter("production-office", productionOffice);
        }
        if (!TextUtils.isEmpty(searchTerm)) {
            uriBuider.appendQueryParameter("q", searchTerm);
        }
        uriBuider.appendQueryParameter("show-fields", "all");
        uriBuider.appendQueryParameter("order-by", orderBy);
        uriBuider.appendQueryParameter("api-key", "test");

        // Create a new loader for the query url
        return new ArticleLoader(getContext(), uriBuider.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        // Hide relative layout with progress bar because the data has been loaded
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        adapter.clear();

        // Check if there is internet connection
        if (isConnected()) {
            // IF there is a valid list of {@link Article}s, then add them to the adapter's
            // data set This will trigger the ListView to update. Else, show error message
            if (data != null && !data.isEmpty()) {
                mEmptyStateTextView.setVisibility(View.GONE);
                // Update recyclerView
                adapter.addall(data);
            } else {
                // Set empty state text to display "No book found."
                mEmptyStateTextView.setText(getString(R.string.no_news_found));
            }
        } else {
            // Set empty state text to display "No internet connection."
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
    }

    /**
     * Refresh the view and restart loader
     */
    @Override
    public void onRefresh() {
        if (isConnected()) {
            LoaderManager loadermanager = getLoaderManager();
            loadermanager.restartLoader(ARTICLE_LOADER_ID, null, this);

        } else {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }
}
