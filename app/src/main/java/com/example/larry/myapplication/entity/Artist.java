package com.example.larry.myapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.larry.myapplication.media.ConstMsg;

/**
 * Created by Larry on 2016/1/19.
 */
public class Artist implements Parcelable {

    private long artistId;
    private String artistName;
    private String artistPath;
    private String artistImg;
    private Integer artistTraceLength;
    private long albumId;
    private int state = ConstMsg.STATE_NONE;

    public Artist() {
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistPath() {
        return artistPath;
    }

    public void setArtistPath(String artistPath) {
        this.artistPath = artistPath;
    }

    public String getArtistImg() {
        return artistImg;
    }

    public void setArtistImg(String artistImg) {
        this.artistImg = artistImg;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Integer getArtistTraceLength() {
        return artistTraceLength;
    }

    public void setArtistTraceLength(Integer artistTraceLength) {
        this.artistTraceLength = artistTraceLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.artistId);
        dest.writeString(this.artistName);
        dest.writeString(this.artistPath);
        dest.writeString(this.artistImg);
        dest.writeValue(this.artistTraceLength);
        dest.writeLong(this.albumId);
        dest.writeInt(this.state);
    }

    protected Artist(Parcel in) {
        this.artistId = in.readLong();
        this.artistName = in.readString();
        this.artistPath = in.readString();
        this.artistImg = in.readString();
        this.artistTraceLength = (Integer) in.readValue(Integer.class.getClassLoader());
        this.albumId = in.readLong();
        this.state = in.readInt();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
