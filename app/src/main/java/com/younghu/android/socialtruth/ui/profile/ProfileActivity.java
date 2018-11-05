package com.younghu.android.socialtruth.ui.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.adapter.QuestionAdapter;
import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.data.User;
import com.younghu.android.socialtruth.data.UserViewModel;
import com.younghu.android.socialtruth.ui.vote.VoteActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity
        implements QuestionAdapter.QuestionOnClickHandler {

    @BindView(R.id.profile_profile_image) ImageView profileImage;
    @BindView(R.id.profile_name) TextView profileName;
    @BindView(R.id.profile_question_count) TextView profileQuestionCount;
    @BindView(R.id.profile_count_text) TextView profileCount;

    @BindView(R.id.recyclerview_questions) RecyclerView recyclerView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private ChildEventListener mChildEventListener;
    private StorageReference mUserhotoStorageReference;
    private FirebaseAuth mFirebaseAuth;

    FirebaseUser mUser;
    private UserViewModel userViewModel;

    private QuestionAdapter questionAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Question> questions;

    private DatabaseReference mResultDatabaseReference;
    private ChildEventListener mVoteListener;

    private static String NOT_VOTED_YET = "Not Voted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference().child("questions");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mUserhotoStorageReference = mFirebaseStorage.getReference().child("user_photos");
        mResultDatabaseReference  = mFirebaseDatabase.getReference().child("voter_results");

        mUser = mFirebaseAuth.getCurrentUser();

        layoutManager = new LinearLayoutManager(ProfileActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        questions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this, this);
        questionAdapter.swapList(questions);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(questionAdapter);

        attachDatabaseReadListner();
        attachVoteDbReadListener();

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        LiveData<User> userLiveData = userViewModel.getUserLiveData();
        userLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User liveUser) {
                profileName.setText(liveUser.getUsername());
                Glide.with(getApplicationContext())
                        .load(liveUser.getPhotoUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(profileImage);
            }
        });
    }

    public void attachVoteDbReadListener() {
        if (mVoteListener == null) {
            mVoteListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int index = 0;
                    for (Question question : questions) {
                        if (question.getQuestionId().equals(dataSnapshot.getRef().getKey())) {
                            String value = dataSnapshot.child(mUser.getUid()).getValue(String.class);
                            if (value != null) {
                                question.setIsAnswered(value);
                                questions.remove(index);
                                questions.add(index, question);
                                break;
                            }
                        }
                        index++;
                    }
                    questionAdapter.notifyDataSetChanged();
                    layoutManager.smoothScrollToPosition(recyclerView, null,
                            questionAdapter.getItemCount());
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int index = 0;
                    for (Question question : questions) {
                        if (question.getQuestionId().equals(dataSnapshot.getRef().getKey())) {
                            String value = dataSnapshot.child(mUser.getUid()).getValue(String.class);
                            if (value != null) {
                                question.setIsAnswered(value);
                                questions.remove(index);
                                questions.add(index, question);
                                break;
                            }
                        }
                        index++;
                    }
                    questionAdapter.notifyDataSetChanged();
                    layoutManager.smoothScrollToPosition(recyclerView, null,
                            questionAdapter.getItemCount());
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
            mResultDatabaseReference.addChildEventListener(mVoteListener);
        }
    }

    private void attachDatabaseReadListner() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null && question.getAuthorId().equals(mUser.getUid())) {
                        getVoterResult(question, mResultDatabaseReference);
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null) {
                        for (int i = 0; i < questions.size(); i++) {
                            if (questions.get(i).getQuestion().equals(question.getQuestion())) {
                                questions.remove(i);
                                questions.add(i, question);
                                break;
                            }
                        }
                    }
                    questionAdapter.notifyDataSetChanged();
                    profileQuestionCount.setText(String.valueOf(questions.size()));
                    layoutManager.smoothScrollToPosition(recyclerView, null,
                            questionAdapter.getItemCount());
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_profile) {
            Intent EditProfileIntent =
                    new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(EditProfileIntent);
            return true;
        } else if (id == R.id.action_sign_out) {
            mFirebaseAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Question question) {
        Intent voteIntent = new Intent(ProfileActivity.this, VoteActivity.class);
        voteIntent.putExtra("Question", question);
        startActivity(voteIntent);
    }

    private void getVoterResult(final Question question, final DatabaseReference ref) {
        Query mQuery = ref.child(question.getQuestionId()).child(mUser.getUid());
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String result = dataSnapshot.getValue(String.class);
                    question.setIsAnswered(result);
                } else {
                    question.setIsAnswered(NOT_VOTED_YET);
                }
                questions.add(question);
                profileQuestionCount.setText(String.valueOf(questions.size()));
                questionAdapter.swapList(questions);

                //Go to the top
                layoutManager.smoothScrollToPosition(recyclerView, null,
                        questionAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
