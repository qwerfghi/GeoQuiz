package com.qwerfghi.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainQuiz extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private Button mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private boolean[] mAnswers = new boolean[mQuestionBank.length];
    private int mCurrentIndex;
    private int mQuestionCount;
    private boolean mIsCheater;
    private static final String KEY_INDEX = "index";
    private static final String IS_CHEATER = "cheater";
    private static final String TAG = "QuizActivity";
    private static final int REQUEST_CODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATER, false);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(view -> nextQuestion());
        updateQuestion();

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(view -> setAnswer(true));

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(view -> setAnswer(false));

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(view -> {
            mCurrentIndex = mCurrentIndex > 0 ? mCurrentIndex - 1 : 0;
            setButtons(!mQuestionBank[mCurrentIndex].isAnswered());
            updateQuestion();
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(view -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(MainQuiz.this, answerIsTrue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(view -> nextQuestion());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(IS_CHEATER, mIsCheater);
    }

    private void nextQuestion() {
        mCurrentIndex = mCurrentIndex < mQuestionBank.length - 1 ? mCurrentIndex + 1 : mQuestionBank.length - 1;
        setButtons(!mQuestionBank[mCurrentIndex].isAnswered());
        mIsCheater = false;
        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void setAnswer(boolean userPressedTrue) {
        mQuestionBank[mCurrentIndex].setAnswered(true);
        setButtons(false);
        boolean answerIsRight = userPressedTrue == mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = mIsCheater ? R.string.judgment_toast : answerIsRight ? R.string.correct_toast : R.string.incorrect_toast;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        mAnswers[mCurrentIndex] = answerIsRight;
        mQuestionCount++;
        if (mQuestionCount == mQuestionBank.length) {
            showStatistic();
            mNextButton.setEnabled(false);
            mPrevButton.setEnabled(false);
        }
    }

    private void showStatistic() {
        int rightAnswers = 0;
        for (boolean mAnswer : mAnswers) {
            if (mAnswer) rightAnswers++;
        }
        Toast.makeText(this, "You have " + rightAnswers + " right answers", Toast.LENGTH_SHORT).show();
    }

    private void setButtons(boolean enable) {
        mTrueButton.setEnabled(enable);
        mFalseButton.setEnabled(enable);
    }
}