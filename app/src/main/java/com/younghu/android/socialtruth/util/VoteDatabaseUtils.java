package com.younghu.android.socialtruth.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.younghu.android.socialtruth.data.Question;

import java.util.ArrayList;

//NOT USED
public class VoteDatabaseUtils {

    public void attachVoteDbReadListener(ChildEventListener listener, DatabaseReference mRef,
                                         final ArrayList<Question> questions, final String id) {
        if (listener == null) {
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int index = 0;
                    for (Question question : questions) {
                        if (question.getQuestionId().equals(dataSnapshot.getRef().getKey())) {
                            String value = dataSnapshot.child(id).getValue(String.class);
                            System.out.println("UTIL SAYS " + value);
                            question.setIsAnswered(value);
                            questions.remove(index);
                            questions.add(index, question);
                            break;
                        }
                        index++;
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
            mRef.addChildEventListener(listener);
        }
    }
}
