package com.younghu.android.socialtruth.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
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
import com.younghu.android.socialtruth.data.User;
import com.younghu.android.socialtruth.ui.create.CreateQuestion;
import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.ui.profile.ProfileActivity;
import com.younghu.android.socialtruth.ui.vote.VoteActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements QuestionAdapter.QuestionOnClickHandler {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mResultDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ChildEventListener mVoteListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mQuestionPhotosStorageReference;

    public static final String ANONYMOUS = "Anonymous";
    private User mUser;
    private String mUsername = ANONYMOUS;
    private String mUserId;
    private String mUserPhotoUrl = "http://qoopapp.com/testsite/img/PoyoSquared.png";
    public static final int RC_SIGN_IN = 1;
    private static String NOT_VOTED_YET = "Not Voted";


    public QuestionAdapter questionAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Question> questions = null;
    @BindView(R.id.recyclerview_questions) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mUsername = ANONYMOUS;

        // FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mQuestionDatabaseReference = mFirebaseDatabase.getReference().child("questions");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mResultDatabaseReference  = mFirebaseDatabase.getReference().child("voter_results");
        mQuestionPhotosStorageReference = mFirebaseStorage.getReference().child("question_photos");

        layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        questions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this, this);
        questionAdapter.swapList(questions);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(questionAdapter);

        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    mUserId = user.getUid();

                    if (user.getDisplayName() != null) {
                        mUsername = user.getDisplayName();
                    }
                    if (user.getPhotoUrl() != null) {
                        mUserPhotoUrl = user.getPhotoUrl().toString();
                    }
                    userIdQuery(mUserId, mUsername, mUserPhotoUrl);
                    attachDatabaseReadListner();
                    attachVoteDbReadListener();

                } else {
                    //user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.AppTheme_NoActionBar)
                                    .setLogo(R.drawable.logo)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createIntent =
                        new Intent(MainActivity.this, CreateQuestion.class);
                startActivity(createIntent);
            }
        });
    }

    public void userIdQuery(final String userId, final String mUsername, final String mUserPhotoUrl) {

        Query query = mUserDatabaseReference.child("id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                } else {
                    mUser = new User(userId, mUsername, null, mUserPhotoUrl);
                    mUserDatabaseReference.child(userId).setValue(mUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * This method handles explicit intent when RecyclerView item gets clicked.
     *
     * @param question The clicked question object.
     */
    @Override
    public void onClick(Question question) {
        Intent voteIntent = new Intent(MainActivity.this, VoteActivity.class);
        voteIntent.putExtra("Question", question);
        startActivity(voteIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // for Signout recyclerview refresh, intent is setup
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        questionAdapter.swapList(null);
        questions.clear();
        detachDatabaseReadListner();
    }

    private void attachDatabaseReadListner() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null) {
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
            mQuestionDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void attachVoteDbReadListener() {
        if (mVoteListener == null) {
            mVoteListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int index = 0;
                    for (Question question : questions) {
                        if (question.getQuestionId().equals(dataSnapshot.getRef().getKey())) {
                            String value = dataSnapshot.child(mUserId).getValue(String.class);
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
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int index = 0;
                    for (Question question : questions) {
                        if (question.getQuestionId().equals(dataSnapshot.getRef().getKey())) {
                            String value = dataSnapshot.child(mUserId).getValue(String.class);
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


    private void detachDatabaseReadListner() {
        if (mChildEventListener != null) {
            mQuestionDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
        if (mVoteListener != null) {
            mResultDatabaseReference.removeEventListener(mVoteListener);
            mVoteListener = null;
        }
    }

    private void getVoterResult(final Question question, final DatabaseReference ref) {
        Query mQuery = ref.child(question.getQuestionId()).child(mUserId);
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
                questionAdapter.swapList(questions);
                layoutManager.smoothScrollToPosition(recyclerView, null,
                        questionAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
