package com.younghu.android.socialtruth.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionsViewModel extends ViewModel {
    private static final DatabaseReference DATABASE_REFERENCE =
            FirebaseDatabase.getInstance().getReference();

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(DATABASE_REFERENCE);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }
}
