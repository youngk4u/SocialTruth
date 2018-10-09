package com.younghu.android.socialtruth.ui.vote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.ui.vote.VoteFragment;

public class VoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, VoteFragment.newInstance())
                    .commitNow();
        }
    }
}
