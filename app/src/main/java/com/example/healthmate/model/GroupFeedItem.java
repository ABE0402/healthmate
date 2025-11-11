package com.example.healthmate.model;

import java.util.Date;

public class GroupFeedItem {

    // React의 type: 'meal' | 'challenge'
    public enum FeedType { MEAL, CHALLENGE }

    private long id;
    private String content;
    private Date timestamp;
    private int likes;
    private boolean likedByMe;
    private long groupId;
    private FeedType type;
    private String userName;

    public GroupFeedItem(long id, long groupId, FeedType type, String userName, String content, int likes, boolean likedByMe, Date timestamp) {
        this.id = id;
        this.groupId = groupId;
        this.type = type;
        this.userName = userName;
        this.content = content;
        this.likes = likes;
        this.likedByMe = likedByMe;
        this.timestamp = timestamp;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FeedType getType() {
        return type;
    }

    public void setType(FeedType type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }



    // 생성자, Getters, Setters
}