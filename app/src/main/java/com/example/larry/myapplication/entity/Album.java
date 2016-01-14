package com.example.larry.myapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 067231 on 2016/1/14.
 */
public class Album implements Parcelable {
    private Long id;
    private String albumName;
    private String author;
    private Long classifyId;
    private String descripe;
    private Date publishDate;
    private Date createTime;
    private String imgPath;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.albumName);
        dest.writeString(this.author);
        dest.writeValue(this.classifyId);
        dest.writeString(this.descripe);
        dest.writeLong(publishDate != null ? publishDate.getTime() : -1);
        dest.writeLong(createTime != null ? createTime.getTime() : -1);
        dest.writeString(this.imgPath);
    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.albumName = in.readString();
        this.author = in.readString();
        this.classifyId = (Long) in.readValue(Long.class.getClassLoader());
        this.descripe = in.readString();
        long tmpPublishDate = in.readLong();
        this.publishDate = tmpPublishDate == -1 ? null : new Date(tmpPublishDate);
        long tmpCreateTime = in.readLong();
        this.createTime = tmpCreateTime == -1 ? null : new Date(tmpCreateTime);
        this.imgPath = in.readString();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getDescripe() {
        return descripe;
    }

    public void setDescripe(String descripe) {
        this.descripe = descripe;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
