package com.younghu.android.socialtruth.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class QuestionEntity {

    private String question;
    private String questionId;
    private String authorId;
    private String authorUserName;
    private String authorPhotoUrl;
    private String photoUrl;
    private String isAnswered;

    public QuestionEntity() {
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

}
