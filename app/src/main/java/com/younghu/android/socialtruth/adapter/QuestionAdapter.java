package com.younghu.android.socialtruth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.younghu.android.socialtruth.R;
import com.younghu.android.socialtruth.data.Question;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context mContext;
    private List<Question> questions;
    final private QuestionOnClickHandler mClickHandler;

    private static String NOT_VOTED_YET = "Not Voted";
    private static String VOTED_YES = "Yes";
    private static String VOTED_NO = "No";

    private int YES_DRAWABLE = R.drawable.button_yes;
    private int NO_DRAWABLE = R.drawable.button_no;

    private int mItemCount;

    public QuestionAdapter(Context context, QuestionOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    public interface QuestionOnClickHandler {
        void onClick(Question question);
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.questionImage) public ImageView questionImage;
        @BindView(R.id.quesitonText)  public TextView question;
        @BindView(R.id.userNameText)  public TextView userName;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_list_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position) {
        final Question question = questions.get(position);

        Glide.with(mContext)
                .load(question.getPhotoUrl())
                .apply(new RequestOptions().centerCrop())
                .into(holder.questionImage);
        holder.question.setText(question.getQuestion());
        String answer = question.getIsAnswered();

        if (answer != null) {
            if (answer.equals(NOT_VOTED_YET)) {
                holder.userName.setText("");
                holder.userName.setBackgroundResource(0);
            } else if (answer.equals(VOTED_YES)) {
                holder.userName.setText("Voted");
                holder.userName.setBackgroundResource(YES_DRAWABLE);
            } else if (answer.equals(VOTED_NO)) {
                holder.userName.setText("Voted");
                holder.userName.setBackgroundResource(NO_DRAWABLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onClick(question);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (questions != null) {
            return questions.size();
        } else {
            return 0;
        }
    }

    public void swapList(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }
}
