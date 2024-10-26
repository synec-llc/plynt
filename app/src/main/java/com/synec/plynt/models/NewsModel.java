package com.synec.plynt.models;

import java.util.List;

public class NewsModel {
    private String ai_org;
    private String ai_region;
    private String ai_tag;
    private String article_id;
    private String author;
    private int bookmarks_count;
    private List<String> category;
    private int comments_count;
    private String content;
    private List<String> country;
    private String description;
    private int downvotes_count;
    private boolean duplicate;
    private String image_url;
    private List<String> keywords;
    private String language;
    private int link_copied_count;
    private String pubDateTZ;
    private String publisher;
    private String publishing_date;
    private String sentiment;
    private String sentiment_stats;
    private String source_icon;
    private String source_id;
    private int source_priority;
    private String source_url;
    private String title;
    private String topic_area;
    private int upvotes_count;
    private String url;
    private String video_url;
    private int views_count;

    // Constructor
    public NewsModel() {}

    // Getters and Setters
    public String getAi_org() {
        return ai_org;
    }

    public void setAi_org(String ai_org) {
        this.ai_org = ai_org;
    }

    public String getAi_region() {
        return ai_region;
    }

    public void setAi_region(String ai_region) {
        this.ai_region = ai_region;
    }

    public String getAi_tag() {
        return ai_tag;
    }

    public void setAi_tag(String ai_tag) {
        this.ai_tag = ai_tag;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBookmarks_count() {
        return bookmarks_count;
    }

    public void setBookmarks_count(int bookmarks_count) {
        this.bookmarks_count = bookmarks_count;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDownvotes_count() {
        return downvotes_count;
    }

    public void setDownvotes_count(int downvotes_count) {
        this.downvotes_count = downvotes_count;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getLink_copied_count() {
        return link_copied_count;
    }

    public void setLink_copied_count(int link_copied_count) {
        this.link_copied_count = link_copied_count;
    }

    public String getPubDateTZ() {
        return pubDateTZ;
    }

    public void setPubDateTZ(String pubDateTZ) {
        this.pubDateTZ = pubDateTZ;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishing_date() {
        return publishing_date;
    }

    public void setPublishing_date(String publishing_date) {
        this.publishing_date = publishing_date;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getSentiment_stats() {
        return sentiment_stats;
    }

    public void setSentiment_stats(String sentiment_stats) {
        this.sentiment_stats = sentiment_stats;
    }

    public String getSource_icon() {
        return source_icon;
    }

    public void setSource_icon(String source_icon) {
        this.source_icon = source_icon;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public int getSource_priority() {
        return source_priority;
    }

    public void setSource_priority(int source_priority) {
        this.source_priority = source_priority;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic_area() {
        return topic_area;
    }

    public void setTopic_area(String topic_area) {
        this.topic_area = topic_area;
    }

    public int getUpvotes_count() {
        return upvotes_count;
    }

    public void setUpvotes_count(int upvotes_count) {
        this.upvotes_count = upvotes_count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    // ToString Method
    @Override
    public String toString() {
        return "NewsModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", publishing_date='" + publishing_date + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
