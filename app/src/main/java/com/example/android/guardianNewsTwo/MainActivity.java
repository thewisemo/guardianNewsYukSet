package com.example.android.guardianNewsTwo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>>,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = MainActivity.class.getName();
    // Constant value for the story loader ID.
    private static final int STORY_LOADER_ID = 1;
    // URL for story data from the Guardian data set
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search";
    // Adapter for the list of stories
    private StoryAdapter mAdapter;
    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;
    // Progress bar object
    private ProgressBar mProgressBar;
    // SwipeRefreshLayout
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar_top_view);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Find the Swipe layout
        swipeLayout = findViewById(R.id.swipe_container);
        // Setting the Swipe layout setOnRefreshListener
        swipeLayout.setOnRefreshListener(this);
        // Setting the colors of the Swipe layout while refreshing
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Find a reference to the {@link ListView} in the layout
        ListView storiesListView = findViewById(R.id.list);
        // setting the empty view
        mEmptyStateTextView = findViewById(R.id.empty_tv);
        storiesListView.setEmptyView(mEmptyStateTextView);
        // Progress bar widget
        mProgressBar = findViewById(R.id.loading_spinner);
        // Create a new adapter that takes an empty list of stories as input
        mAdapter = new StoryAdapter(this, new ArrayList<Story>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        storiesListView.setAdapter(mAdapter);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open the Guardian website with more information about the selected story.
        storiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current story that was clicked on
                Story currentStory = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentStory != null;
                Uri storyUri = Uri.parse(currentStory.getShortUrl());
                // Create a new intent to view the story URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_about:
                // Create a factory object as a LayoutInflater from the MainActivity.class
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                // Create a View object to the factory LayoutInflater and use the .inflate method to the about_layout.xml, null for the root view.
                final View aboutDialogView = factory.inflate(R.layout.about_layout, null);
                // Create an AlertDialog object and pass this as the .Builder context params then create it by .create().
                final AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                // Sets the AlertDialog view to the aboutDialogView View object.
                aboutDialog.setView(aboutDialogView);
                // Assign a setOnClickListener (new View) to the Linkedin button to open my in URL with an ACTION_VIEW intent, and leave the user to choose a browser.
                aboutDialogView.findViewById(R.id.in_connect_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String moInUrl = "https://www.linkedin.com/in/wisemo/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(moInUrl));
                        startActivity(i);
                        aboutDialog.dismiss();
                    }
                });
                // Assign a setOnClickListener (new View) to dismiss the about view.
                aboutDialogView.findViewById(R.id.about_back_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutDialog.dismiss();
                    }
                });
                // Make the About view background transparent.
                aboutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                aboutDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    // onCreateLoader method
    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "This is the OnCreateLoader Method");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String searchValue = sharedPrefs.getString(
                getString(R.string.settings_search_key),
                getString(R.string.settings_search_default));
        Log.d(LOG_TAG, "This is the search string default value " + searchValue);

        String dateValue = sharedPrefs.getString(
                getString(R.string.settings_date_key),
                getString(R.string.settings_date_default));
        // If the date value in the preference screen is empty
        // Set the date to the local date format
        if (dateValue.isEmpty()) {
            dateValue = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        // Assigning strings values for both thumbnail & contributor to be used in the final query link string
        String thumbnail = "thumbnail";
        String contributor = "contributor";
        // Assigning a boolean value for the load images preference checkbox.
        boolean imagesThumbnail = sharedPrefs.getBoolean(
                getString(R.string.settings_images_key),
                Boolean.parseBoolean(getString(R.string.settings_images_default)));
        if (!imagesThumbnail) {
            thumbnail = "false";
        }
        Log.d(LOG_TAG, "This is the image string default value " + imagesThumbnail);
        // Assigning a boolean value for the author name preference checkbox.
        boolean authorContributor = sharedPrefs.getBoolean(
                getString(R.string.settings_author_key),
                Boolean.parseBoolean(getString(R.string.settings_author_default)));
        if (!authorContributor) {
            contributor = "false";
        }
        Log.d(LOG_TAG, "This is the author string default value " + authorContributor);
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Append query parameter and its value. For example, the `page-size=100`
        uriBuilder.appendQueryParameter("q", searchValue);
        uriBuilder.appendQueryParameter("from-date", dateValue);
        uriBuilder.appendQueryParameter("page-size", "100");
        uriBuilder.appendQueryParameter("show-tags", contributor);
        uriBuilder.appendQueryParameter("show-fields", "trailText,headline," + thumbnail + ",shortUrl");
        uriBuilder.appendQueryParameter("api-key", "4bfdeaf5-f178-4ebe-9859-5d065c52c213");
        // Return the completed uri:
        // https://content.guardianapis.com/search?
        // q=news&page-size=10&show-tags=contributor&show-fields=trailText,headline,thumbnail,shortUrl&api-key=4bfdeaf5-f178-4ebe-9859-5d065c52c213"
        return new StoryLoader(this, uriBuilder.toString());
    }

    // onLoadFinished method
    @Override
    public void onLoadFinished
    (Loader<List<Story>> loader, List<Story> stories) {
        Log.d(LOG_TAG, "This is the onLoadFinished Method");
        // Set empty state text to display "No stories found."
        mEmptyStateTextView.setText(R.string.no_stories);
        // Setting the progress spinner to gone when list is fetched
        mProgressBar.setVisibility(View.GONE);
        // Clear the adapter of previous story data
        mAdapter.clear();
        // If there is a valid list of {@link Story}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (stories != null && !stories.isEmpty()) {
            // Add the Stories data
            mAdapter.addAll(stories);
        }
    }

    // onLoaderReset method
    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        Log.d(LOG_TAG, "This is the onLoaderReset Method");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    // onRefresh method
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
