package com.younghu.android.socialtruth.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {

    private String question;
    private String questionId;
    private String authorId;
    private String authorUserName;
    private String authorPhotoUrl;
    private String photoUrl;
    private String isAnswered;


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
        questionId = parcel.readString();
        authorId = parcel.readString();
        authorUserName = parcel.readString();
        authorPhotoUrl = parcel.readString();
        photoUrl = parcel.readString();
        isAnswered = parcel.readString();
    }

    public Question(String question, String questionId, String authorId,
                    String authorUserName, String authorPhotoUrl, String photoUrl, String isAnswered) {
        this.question = question;
        this.questionId = questionId;
        this.authorId = authorId;
        this.authorUserName = authorUserName;
        this.authorPhotoUrl = authorPhotoUrl;
        this.photoUrl = photoUrl;
        this.isAnswered = isAnswered;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUserName() { return authorUserName; }

    public void setAuthorUserName(String authorUserName) { this.authorUserName = authorUserName; }

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

    public String getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(String isAnswered) {
        this.isAnswered = isAnswered;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(questionId);
        dest.writeString(authorId);
        dest.writeString(authorUserName);
        dest.writeString(authorPhotoUrl);
        dest.writeString(photoUrl);
        dest.writeString(isAnswered);
    }
}
