package io.github.froger.instamaterial.pojo;

/**
 * Created by alex on 2/9/17.
 */

public class FeedItem {
    private String userId;
    private String photoId;
    private String caption;
    private boolean accessLevel;
    private boolean picture;
    private String date;
    private int nlike;
    private boolean liked;
    private String profilePhoto;

    public FeedItem(String userId, String photoId, String caption, boolean accessLevel, boolean picture, String date, int nlike, boolean liked, String profilePhoto) {
        this.userId = userId;
        this.photoId = photoId;
        this.caption = caption;
        this.accessLevel = accessLevel;
        this.picture = picture;
        this.date = date;
        this.nlike = nlike;
        this.liked = liked;
        this.profilePhoto = profilePhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(boolean accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean isPicture() {
        return picture;
    }

    public void setPicture(boolean picture) {
        this.picture = picture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNlike() {
        return nlike;
    }

    public void setNlike(int nlike) {
        this.nlike = nlike;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    public void addNlike(){
        nlike++;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
