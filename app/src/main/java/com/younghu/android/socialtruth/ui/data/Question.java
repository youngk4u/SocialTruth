package com.younghu.android.socialtruth.ui.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {

    private String question;
    private String authorId;
    private String authorPhotoUrl;
    private String photoUrl;

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>(){

        @Override
        public Question createFromParcel(Parcel parcel) {
            return new Question(parcel);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[0];
        }
    };

    public Question() {
    }

    public Question(Parcel parcel) {
        question = parcel.readString();
        authorId = parcel.readString();
        authorPhotoUrl = parcel.readString();
        photoUrl = parcel.readString();
    }

    public Question(String question, String authorId, String authorPhotoUrl, String photoUrl) {
        this.question = question;
        this.authorId = authorId;
        this.authorPhotoUrl = authorPhotoUrl;
        this.photoUrl = photoUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPhotoUrl() {
        return authorPhotoUrl;
    }

    public void setAuthorPhotoUrl(String authorPhotoUrl) {
        this.authorPhotoUrl = authorPhotoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(authorId);
        dest.writeString(authorPhotoUrl);
        dest.writeString(photoUrl);
    }
}
