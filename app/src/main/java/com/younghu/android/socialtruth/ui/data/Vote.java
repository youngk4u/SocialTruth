package com.younghu.android.socialtruth.ui.data;

public class Vote {

    private String questionId;
    private boolean yesOrNo;

    public Vote(String questionId, boolean yesOrNo) {
        this.questionId = questionId;
        this.yesOrNo = yesOrNo;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public boolean isYesOrNo() {
        return yesOrNo;
    }

    public void setYesOrNo(boolean yesOrNo) {
        this.yesOrNo = yesOrNo;
    }
}
