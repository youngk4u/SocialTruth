package com.younghu.android.socialtruth.data;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserViewModel extends ViewModel {

    private static final DatabaseReference userRef =
            FirebaseDatabase.getInstance().getReference().child("users");

    private final FirebaseQueryLiveData mLiveData = new FirebaseQueryLiveData(userRef);

    private final LiveData<User> userLiveData =
            Transformations.map(mLiveData, new Deserializer());

    private class Deserializer implements Function<DataSnapshot, User> {
        @Override
        public User apply(DataSnapshot dataSnapshot) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            User newUser = dataSnapshot.child(user.getUid()).getValue(User.class);
            return newUser;
        }
    }

    @NonNull
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

}
