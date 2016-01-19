package com.example.larry.myapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Larry on 2016/1/19.
 */
public class Artist implements Parcelable {

    private long artistId;
    private String artistName;
    private String artistPath;
    private String artistImg;
    private long albumId;


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
        dest.writeLong(this.albumId);
    }

    public Artist() {
    }

    protected Artist(Parcel in) {
        this.artistId = in.readLong();
        this.artistName = in.readString();
        this.artistPath = in.readString();
        this.artistImg = in.readString();
        this.albumId = in.readLong();
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

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
}
