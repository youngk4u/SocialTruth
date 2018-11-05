package com.younghu.android.socialtruth.ui.vote;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.data.Vote;

import java.sql.SQLOutput;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoteActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVoteDatabaseReference;
    private DatabaseReference mResultDatabaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;

    private Question mQuestion;
    private Vote mVote;
    private double percentage = 0;

    private static String NOT_VOTED_YET = "Not Voted";
    private static String VOTED_YES = "Yes";
    private static String VOTED_NO = "No";

    @BindView(R.id.authorName) TextView authorName;
    @BindView(R.id.authorPic) ImageView authorImage;
    @BindView(R.id.quesitonText) TextView questionText;
    @BindView(R.id.questionImage) ImageView questionImage;

    @BindView(R.id.vote_button) Button yesButton;
    @BindView(R.id.vote_button2) Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        getSupportActionBar().setTitle("Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        mQuestion = intent.getParcelableExtra("Question");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mVoteDatabaseReference = mFirebaseDatabase.getReference().child("votes");
        mResultDatabaseReference = mFirebaseDatabase.getReference().child("voter_results");

        authorName.setText(mQuestion.getAuthorUserName());
        Glide.with(getApplicationContext())
                .load(mQuestion.getAuthorPhotoUrl())
                .apply(new RequestOptions().circleCrop())
                .into(authorImage);

        questionText.setText(mQuestion.getQuestion());

        Glide.with(getApplicationContext())
                .load(mQuestion.getPhotoUrl())
                .into(questionImage);

        if (mQuestion.getIsAnswered().equals(VOTED_YES) || mQuestion.getIsAnswered().equals(VOTED_NO)) {

            yesButton.setEnabled(false);
            noButton.setEnabled(false);

            mVoteDatabaseReference.child(mQuestion.getQuestionId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int yesPercent = dataSnapshot.child("yesVotePercent").getValue(int.class);
                    int noPercent = 100 - yesPercent;
                    setPercentageOnButton(yesPercent, noPercent);
                    if (mQuestion.getIsAnswered().equals(VOTED_YES)) {
                        yesButton.setBackgroundResource(R.drawable.button_yes_border);
                    } else {
                        noButton.setBackgroundResource(R.drawable.button_no_border);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.vote_button)
    public void yesButton(View view) {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

        voteQuery(VOTED_YES);
        userVote(VOTED_YES);

        yesButton.setBackgroundResource(R.drawable.button_yes_border);
    }

    @OnClick(R.id.vote_button2)
    public void noButton(View view) {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

        voteQuery(VOTED_NO);
        userVote(VOTED_NO);

        noButton.setBackgroundResource(R.drawable.button_no_border);
    }

    private void voteQuery(final String voteResult) {
        Query query = mVoteDatabaseReference.child(mQuestion.getQuestionId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mVote = dataSnapshot.getValue(Vote.class);

                    if (voteResult.equals(VOTED_YES)) {
                        int newYesVotes;
                        if (mVote != null) {
                            newYesVotes = mVote.getYesVotes() + 1;
                            mVote.setYesVotes(newYesVotes);
                        }

                    } else if (voteResult.equals(VOTED_NO)) {
                        int newNoVotes;
                        if (mVote != null) {
                            newNoVotes = mVote.getNoVotes() + 1;
                            mVote.setNoVotes(newNoVotes);
                        }
                    }
                    int yesPercent = getPercentage();
                    int noPercent = 100 - yesPercent;
                    mVote.setYesVotePercent(yesPercent);
                    mVote.setNoVotePercent(noPercent);
                    dataSnapshot.getRef().setValue(mVote);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void userVote(final String voteResult) {
        mResultDatabaseReference.child(mQuestion.getQuestionId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child(mUser.getUid()).getRef().setValue(voteResult);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getPercentage() {
        double yesVotes = (double) mVote.getYesVotes();
        double noVotes  = (double) mVote.getNoVotes();
        percentage = yesVotes/(noVotes + yesVotes);
        int yesPercent = (int) Math.round(percentage * 100);
        int noPercent  = 100 - yesPercent;
        setPercentageOnButton(yesPercent, noPercent);

        return yesPercent;
    }

    private void setPercentageOnButton(int yesPercent, int noPercent) {
        String yesString = "YES " + yesPercent + "%";
        String noString  = "NO " + noPercent + "%";
        yesButton.setText(yesString);
        noButton.setText(noString);
    }

}
