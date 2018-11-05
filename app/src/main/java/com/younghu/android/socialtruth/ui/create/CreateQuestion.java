package com.younghu.android.socialtruth.ui.create;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.data.User;
import com.younghu.android.socialtruth.data.Vote;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateQuestion extends AppCompatActivity {

    @BindView(R.id.questionEditText) EditText questionEditText;
    @BindView(R.id.createButton) Button createButton;
    @BindView(R.id.imageButton) ImageButton imageButton;
    @BindView(R.id.progress) ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionDatabaseReference;
    private DatabaseReference mVoteDatabaseReference;
    private DatabaseReference mYesVoteDatabaseReference;
    private DatabaseReference mNoVoteDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mQuestionPhotosStorageReference;

    private String mUserId;
    private String mUserName;
    private String mUserPhotoUrl;
    private String mPhotoUrl = "http://qoopapp.com/testsite/img/PoyoSquared.png";
    private User mUser;
    private static final int RC_PHOTO_PICKER =  2;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);

        // FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth     = FirebaseAuth.getInstance();
        mFirebaseStorage  = FirebaseStorage.getInstance();
        mQuestionDatabaseReference = mFirebaseDatabase.getReference().child("questions");
        mVoteDatabaseReference     = mFirebaseDatabase.getReference().child("votes");
        mUserDatabaseReference     = mFirebaseDatabase.getReference().child("users");
        mYesVoteDatabaseReference  = mFirebaseDatabase.getReference().child("yes_voters");
        mNoVoteDatabaseReference   = mFirebaseDatabase.getReference().child("no_voters");
        mQuestionPhotosStorageReference = mFirebaseStorage.getReference().child("question_photos");

        mUserDatabaseReference.child(Objects.requireNonNull(mFirebaseAuth.getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                if (mUser != null) {
                    mUserId = mUser.getId();
                    mUserName = mUser.getUsername();
                    mUserPhotoUrl = mUser.getPhotoUrl();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // imageButton shows an image picker to upload a image for a message
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        // Enable Create button when there's text to send
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    createButton.setEnabled(true);
                } else {
                    createButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        questionEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }


    // Initiate new question and vote object in Firebasedatabase
    @OnClick(R.id.createButton)
    public void createButton(View view) {
        createButton.setEnabled(false);

        String questionId = mQuestionDatabaseReference.push().getKey();

        if (questionId != null) {
            Question sampleQuestion = new Question(questionEditText.getText().toString(), questionId,
                    mUserId, mUserName, mUserPhotoUrl, mPhotoUrl, "Not Voted");
            Vote vote = new Vote(0, 0, questionId, 0, 0);

            mQuestionDatabaseReference.child(questionId).setValue(sampleQuestion);
            mVoteDatabaseReference.child(questionId).setValue(vote);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef =
                    mQuestionPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            mPhotoUrl = downloadUri.toString();
                            Glide.with(getApplicationContext())
                                    .load(mPhotoUrl)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e,
                                                                    Object model, Target<Drawable> target,
                                                                    boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model,
                                                                       Target<Drawable> target,
                                                                       DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .apply(new RequestOptions().centerCrop())
                                    .into(imageButton);
                        }
                    } else {
                        Toast.makeText(CreateQuestion.this, "upload failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
