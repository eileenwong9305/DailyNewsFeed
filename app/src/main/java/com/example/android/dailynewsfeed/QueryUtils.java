package com.example.android.dailynewsfeed;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving article data from Guardian API.
 */
public class QueryUtils {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    /**
     * Read time out in milliseconds
     */
    private static final int READ_TIMEOUT = 10000;
    /**
     * Connect time out in milliseconds
     */
    private static final int CONNECT_TIMEOUT = 15000;
    /**
     * Success response code
     */
    private static final int SUCCESS_RESPONSE_CODE = 200;

    // Guardian API keys
    private static final String RESPONSE_KEY = "response";
    private static final String RESULTS_KEY = "results";
    private static final String TITLE_KEY = "webTitle";
    private static final String SECTION_NAME_KEY = "sectionName";
    private static final String PUBLICATION_DATE_KEY = "webPublicationDate";
    private static final String WEB_URL_KEY = "webUrl";
    private static final String FIELDS_KEY = "fields";
    private static final String BYLINE_KEY = "byline";
    private static final String TRAIL_TEXT_KEY = "trailText";
    private static final String THUMBNAIL_KEY = "thumbnail";

    /**
     * Meant to hold static variables and methods, which can be accessed directly from class name
     * QueryUtils.
     */
    private QueryUtils() {
    }

    /**
     * Query Guardian API and return a list of {@link Article} objects
     *
     * @param stringUrl to load articles from
     * @return list of articles
     */
    public static List<Article> fetchArticleData(String stringUrl) {
        // Create URL object from String
        URL url = createUrl(stringUrl);

        // Perform HTTP request to the URL and receive back JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in closing input stream", e);
        }

        // Extract relevant fields from JSON response and create a list of articles
        List<Article> articles = extractDataFromJSON(jsonResponse);
        // Return a list of articles
        return articles;
    }

    /**
     * Create and return new URL object from string url
     *
     * @param stringUrl string url
     * @return URL object
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating url", e);
        }
        return url;
    }

    /**
     * Make a HTTP request to the given URL and return a String as the response
     *
     * @param url URL object
     * @return json response string
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the url is null, return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request is successful (response code 200), read from the input stream
            // and parse the response.
            if (urlConnection.getResponseCode() == SUCCESS_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with retrieving JSON response", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from
     * server.
     *
     * @param inputStream
     * @return json response in string
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list {@link Article} objects that has been built up from parsing a JSON response.
     *
     * @param jsonResponse json response in String
     * @return list of {@link Article} objects
     */
    private static ArrayList<Article> extractDataFromJSON(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (jsonResponse == null) {
            return null;
        }

        // Create an empty Arraylist for adding article to
        ArrayList<Article> articles = new ArrayList<>();
        // Try to parse json response. If there is problem with the way the JSON is formatted,
        // a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash and print the error message to log
        try {
            // Create a JSONObject from the jsonResponse string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            // Extract JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject(RESPONSE_KEY);

            // If the response has key called "results", proceed with extracting data, else
            // set {@Article} object to be empty
            if (response.has(RESULTS_KEY)) {
                JSONArray resultsArray = response.getJSONArray(RESULTS_KEY);

                // For each article in the resultsArray, create a {@link Article} object
                for (int i = 0; i < resultsArray.length(); i++) {
                    // Get the result at position i
                    JSONObject result = resultsArray.getJSONObject(i);

                    // If the results has key called "title", extract the String title
                    String title = "";
                    if (result.has(TITLE_KEY)) {
                        title = result.getString(TITLE_KEY);
                    }

                    // If the results has key called "sectionName", extract the String section
                    String section = "";
                    if (result.has(SECTION_NAME_KEY)) {
                        section = result.getString(SECTION_NAME_KEY);
                    }

                    // If the results has key called "webPublicationDate", extract the String publicationDate
                    String publicationDate = "";
                    if (result.has(PUBLICATION_DATE_KEY)) {
                        publicationDate = result.getString(PUBLICATION_DATE_KEY);
                    }

                    // If the results has key called "webUrl", extract the String webUrl
                    String webUrl = "";
                    if (result.has(WEB_URL_KEY)) {
                        webUrl = result.getString(WEB_URL_KEY);
                    }

                    String contributor = "";
                    Drawable image = null;

                    // Extract JSONObject associated with key called "fields"
                    if (result.has(FIELDS_KEY)) {
                        JSONObject fields = result.getJSONObject(FIELDS_KEY);
                        // If the results has key called "byline", extract the String contributor
                        if (fields.has(BYLINE_KEY)) {
                            contributor = fields.getString(BYLINE_KEY);
                        }
                        // If the results has key called "thumbnail", extract the String thumbnail
                        // and convert it to Drawable object
                        if (fields.has(THUMBNAIL_KEY)) {
                            String thumbnail = fields.getString(THUMBNAIL_KEY);
                            image = createDrawable(thumbnail);
                        }
                    }

                    // Create a new {@link Article} object with title, contributor, section name,
                    // publication date, web url and image from jsonResponse
                    articles.add(new Article(title, contributor, section, publicationDate,
                            webUrl, image));
                }
            } else {
                articles = null;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error with parsing JSON response", e);
        }
        // Return the list of articles
        return articles;
    }

    /**
     * Create image drawable from string url
     *
     * @param stringImage string url to extract drawable from
     * @return drawable image
     */
    private static Drawable createDrawable(String stringImage) {
        if (TextUtils.isEmpty(stringImage)) {
            return null;
        }
        InputStream content = null;
        try {
            URL imageUrl = new URL(stringImage);
            content = (InputStream) imageUrl.getContent();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating image url", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with obtaining url content", e);
        }
        return Drawable.createFromStream(content, "src");
    }
}
