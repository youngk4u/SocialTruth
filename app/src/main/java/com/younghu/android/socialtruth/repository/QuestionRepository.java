package com.younghu.android.socialtruth.repository;

import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.mapper.QuestionMapper;

public class QuestionRepository extends FirebaseDatabaseRepository<Question> {

    public QuestionRepository() {
        super(new QuestionMapper());
    }

    @Override
    protected String getRootNode() {
        return "questions";
    }
}
