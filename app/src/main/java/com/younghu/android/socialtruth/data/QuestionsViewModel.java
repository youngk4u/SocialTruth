package com.younghu.android.socialtruth.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.younghu.android.socialtruth.repository.FirebaseDatabaseRepository;
import com.younghu.android.socialtruth.repository.QuestionRepository;

import java.util.List;

public class QuestionsViewModel extends ViewModel {

    private MutableLiveData<List<Question>> questions;
    private QuestionRepository repository = new QuestionRepository();

    public LiveData<List<Question>> getQuestions() {
        if (questions == null) {
            questions = new MutableLiveData<>();
            loadQuestions();
        }
        return questions;
    }

    @Override
    protected void onCleared() {
        repository.removeListener();
    }

    private void loadQuestions() {
        repository.addListener(new FirebaseDatabaseRepository.FirebaseDatabaseRepositoryCallback<Question>() {
            @Override
            public void onSuccess(List<Question> result) {
                questions.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                questions.setValue(null);
            }
        });
    }
}
