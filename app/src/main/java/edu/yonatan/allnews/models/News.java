package edu.yonatan.allnews.models;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable {



    private String title;
    private String execrpt;
    private String img;
    private String link;


    public News(String title, String execrpt, String img, String link) {
        this.title = title;
        this.execrpt = execrpt;
        this.img = img;
        this.link = link;
    }

    protected News(Parcel in) {
        title = in.readString();
        execrpt = in.readString();
        img = in.readString();
        link = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getExecrpt() {
        return execrpt;
    }

    public String getImg() {
        return img;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(execrpt);
        dest.writeString(img);
        dest.writeString(link);
    }
}
