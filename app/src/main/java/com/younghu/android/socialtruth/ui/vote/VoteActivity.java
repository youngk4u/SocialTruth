package com.younghu.android.socialtruth.ui.vote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.ui.data.Question;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteActivity extends AppCompatActivity {

    private Question mQuestion;

    @BindView(R.id.authorId) TextView authorId;
    @BindView(R.id.authorPic) ImageView authorImage;
    @BindView(R.id.quesitonText) TextView questionText;
    @BindView(R.id.questionImage) ImageView questionImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mQuestion = intent.getParcelableExtra("Question");

        authorId.setText(mQuestion.getAuthorId());
        Glide.with(getApplicationContext())
                .load(mQuestion.getAuthorPhotoUrl())
                .apply(new RequestOptions().circleCrop())
                .into(authorImage);

        questionText.setText(mQuestion.getQuestion());

        Glide.with(getApplicationContext())
                .load(mQuestion.getPhotoUrl())
                .into(questionImage);
    }
}
