package com.younghu.android.socialtruth.data;

public class Vote {

    private int yesVotes;
    private int noVotes;
    private String questionId;
    private int yesVotePercent;
    private int noVotePercent;

    public Vote(int yesVotes, int noVotes, String questionId, int yesVotePercent, int noVotePercent) {
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
        this.questionId = questionId;
        this.yesVotePercent = yesVotePercent;
        this.noVotePercent = noVotePercent;
    }

    public Vote() {
    }

    public int getYesVotes() {
        return yesVotes;
    }

    public void setYesVotes(int yesVotes) {
        this.yesVotes = yesVotes;
    }

    public int getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(int noVotes) {
        this.noVotes = noVotes;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getYesVotePercent() {
        return yesVotePercent;
    }

    public void setYesVotePercent(int yesVotePercent) {
        this.yesVotePercent = yesVotePercent;
    }

    public int getNoVotePercent() {
        return noVotePercent;
    }

    public void setNoVotePercent(int noVotePercent) {
        this.noVotePercent = noVotePercent;
    }
}
