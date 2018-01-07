package com.example.android.dailynewsfeed;

import android.graphics.drawable.Drawable;

/**
 * {@link Article} represents news article from Guardian API.
 * It contains the title, contributor, section name, publication date, url link to the article, and
 * thumbnail
 */
public class Article {

    /**
     * Title of the article
     */
    private String title;
    /**
     * Contributor of the article
     */
    private String contributor;
    /**
     * Section name of the article
     */
    private String section;
    /**
     * Publication date of the article
     */
    private String date;
    /**
     * Web url to the article
     */
    private String webUrl;
    /**
     * Image of article
     */
    private Drawable image;

    /**
     * Create a new Article class
     */
    public Article(String title, String contributor, String section, String date, String webUrl,
                   Drawable image) {
        this.title = title;
        this.contributor = contributor;
        this.section = section;
        this.date = date;
        this.webUrl = webUrl;
        this.image = image;
    }

    /**
     * Get the title of the article
     *
     * @return title of article
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the contributor of the article
     *
     * @return contributor of article
     */
    public String getContributor() {
        return contributor;
    }

    /**
     * Get the section name of the article
     *
     * @return section name of article
     */
    public String getSection() {
        return section;
    }

    /**
     * Get the publication date of the article
     *
     * @return publication date of article
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the web url of the article
     *
     * @return web url of article
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Get the image of the article
     *
     * @return image of article
     */
    public Drawable getImage() {
        return image;
    }
}
