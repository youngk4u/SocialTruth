package com.younghu.android.socialtruth.ui.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.younghu.android.socialtruth.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.nameEditText) EditText nameEditText;
    @BindView(R.id.imageEditButton) ImageButton imageButton;
    @BindView(R.id.progress) ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    private FirebaseUser user;
    private UserProfileChangeRequest profileUpdates;

    private String mUsername;
    private String mNewUsername;
    private String mUserPhotoUrl = "http://qoopapp.com/testsite/img/PoyoSquared.png";
    private String mNewUserPhotoUrl;
    private static final int RC_PHOTO_PICKER =  2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profile");

        ButterKnife.bind(this);

        // FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("questions");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mPhotosStorageReference = mFirebaseStorage.getReference().child("user_photos");

        user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            mUsername = user.getDisplayName();
            nameEditText.setText(mUsername);

            if (user.getPhotoUrl() != null) {
                mUserPhotoUrl = user.getPhotoUrl().toString();
            }
            Glide.with(getApplicationContext())
                    .load(mUserPhotoUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(imageButton);
        }

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

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mNewUsername = editable.toString();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

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
                            mNewUserPhotoUrl = downloadUri.toString();
                            Glide.with(getApplicationContext())
                                    .load(mNewUserPhotoUrl)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                    Target<Drawable> target, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model,
                                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .apply(new RequestOptions().centerCrop())
                                    .into(imageButton);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this,
                                "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mNewUserPhotoUrl == null && mNewUsername == null) {
            finish();
        } else {
            if (mNewUsername == null) mNewUsername = mUsername;
            if (mNewUserPhotoUrl == null) mNewUserPhotoUrl = mUserPhotoUrl;

            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mNewUsername)
                    .setPhotoUri(Uri.parse(mNewUserPhotoUrl))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateUser();
                                updateQuestions(mNewUsername, mNewUserPhotoUrl);
                                Toast.makeText(getApplicationContext(),
                                        "User profile updated.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUser() {
        mUserDatabaseReference.child(user.getUid()).child("username").setValue(mNewUsername);
        mUserDatabaseReference.child(user.getUid()).child("photoUrl").setValue(mNewUserPhotoUrl);
    }

    public void updateQuestions(final String mNewUserName, final String mNewPhotoUrl) {
        Query query = mDatabaseReference.orderByChild("authorId").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String mKey = snapshot.getKey();
                    mDatabaseReference.child(mKey).child("authorUserName").setValue(mNewUserName);
                    mDatabaseReference.child(mKey).child("authorPhotoUrl").setValue(mNewPhotoUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
