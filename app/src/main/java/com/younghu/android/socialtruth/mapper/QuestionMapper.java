package com.younghu.android.socialtruth.mapper;

import com.younghu.android.socialtruth.data.Question;
import com.younghu.android.socialtruth.data.QuestionEntity;

// Credit to https://github.com/gonzalonm/viewmodel-firebase-sample/
// blob/master/data/src/main/java/com/gonzalonm/viewmodelfirebase/data/mapper/IMapper.java

public class QuestionMapper extends FirebaseMapper<QuestionEntity, Question> {

    @Override
    public Question map(QuestionEntity questionEntity) {
        Question question = new Question();
        question.setQuestion(questionEntity.getQuestion());
        question.setQuestionId(questionEntity.getQuestionId());
        question.setAuthorId(questionEntity.getAuthorId());
        question.setAuthorUserName(questionEntity.getAuthorUserName());
        question.setAuthorPhotoUrl(questionEntity.getAuthorPhotoUrl());
        question.setPhotoUrl(questionEntity.getPhotoUrl());
        question.setIsAnswered(questionEntity.getIsAnswered());
        return question;
    }
}
