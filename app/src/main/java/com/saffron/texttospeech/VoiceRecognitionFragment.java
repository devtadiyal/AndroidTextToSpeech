package com.saffron.texttospeech;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.saffron.texttospeech.RecordingActivity.RequestPermissionCode;


public class VoiceRecognitionFragment extends Fragment implements OnClickListener {

    private Context context;
    private TextView mTextTimer, mText, mTextCaptured;
    private SpeechRecognizer recognizer;
    private static final String TAG = "MyStt3Activity";
    private boolean voiceRecognitionInProgress = false;
    int currIndex = 0, arrayIndex = 0;
    private Intent intent;
    private CountDownTimer countDownTimer;
    ImageButton imagePlay, imagePause, imageStop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.voice_recog_layout, null);
        Button speakButton = view.findViewById(R.id.btn_speakVoice);
        Button stopButton = view.findViewById(R.id.btn_stopVoice);
        mTextTimer = view.findViewById(R.id.textViewTimer);
        mText = view.findViewById(R.id.textView1);
        mTextCaptured = view.findViewById(R.id.et_from);
        imagePlay = view.findViewById(R.id.img_play);
        imagePause = view.findViewById(R.id.img_pause);
        imageStop = view.findViewById(R.id.img_stop);

        imagePlay.setOnClickListener(this);
        imagePause.setOnClickListener(this);
        imageStop.setOnClickListener(this);
        speakButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        if (checkPermission()) {

            //recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer = SpeechRecognizer.createSpeechRecognizer(context, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
            recognizer.setRecognitionListener(new listener());
            timerStart(61000);

        } else {

            requestPermission();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @TargetApi(23)
    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);

        return (result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED);
    }


    @TargetApi(23)
    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }


    class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
            mTextCaptured.setText("Start");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
            imagePlay.setVisibility(View.VISIBLE);
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            mText.setText("error " + error);

            if (error == 7) {

                if (arrayIndex < mText.getText().toString().split(" ").length) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            recognizer.startListening(intent);
                        }
                    }, 2000);

                } else {
                    voiceRecognitionInProgress = false;
                    recognizer.stopListening();
                }
            }
        }

        public void onResults(Bundle results) {

            Log.d(TAG, "onResults " + results);
            /*ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
            }*/

            if (arrayIndex < mText.getText().toString().split(" ").length) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recognizer.startListening(intent);
                    }
                }, 2000);

            } else {
                voiceRecognitionInProgress = false;
                recognizer.stopListening();
            }
        }

        public void onPartialResults(Bundle partialResults) {
            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            if (!word.isEmpty()) {
                mTextCaptured.setText(word);
                textColorHighlighter(word);
            }
            Log.i("onPartialResults", "partial_results: " + word);
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_speakVoice:
                if (checkPermission()) {
                    recognizer.setRecognitionListener(new listener());
                    startRecording();
                } else {
                    requestPermission();
                }
                break;

            case R.id.btn_stopVoice:
                recognizer.stopListening();
                recognizer.setRecognitionListener(null);
                countDownTimer.cancel();
                mTextTimer.setText(R.string.read_time);
                mText.setText(R.string.text_data);
                mTextCaptured.setText("");
                voiceRecognitionInProgress = false;
                arrayIndex = 0;
                currIndex = 0;
                break;

            case R.id.img_play:
                startRecording();
                imagePlay.setVisibility(View.GONE);
                break;

            case R.id.img_pause:
                countDownTimer.cancel();
                break;

            case R.id.img_resume:
                long timeInMilliSec = Long.valueOf(
                        mTextTimer.getText().toString().replace(":", "0"));
                timerStart(timeInMilliSec);
                countDownTimer.start();
                recognizer.startListening(intent);
                break;

            case R.id.img_stop:
                recognizer.stopListening();
                mText.setText(R.string.text_data);
                voiceRecognitionInProgress = false;
                arrayIndex = 0;
                currIndex = 0;
                countDownTimer.cancel();
                imagePlay.setVisibility(View.VISIBLE);
                break;

        }
    }

    public void timerStart(long millisInFuture) {

        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTextTimer.setText(String.valueOf(millisUntilFinished / 1000) + ":00");
            }

            @Override
            public void onFinish() {

                mTextTimer.setText(R.string.read_time);
            }
        };
    }

    public void startRecording() {

        if (intent == null) {
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getApplicationInfo().packageName);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            /*intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, Long.valueOf(30000));
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, Long.valueOf(30000));
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 60000);*/
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault());
        }

        //if (!voiceRecognitionInProgress)
        {
            recognizer.startListening(intent);
            mText.setText(R.string.text_data);
            voiceRecognitionInProgress = true;
            arrayIndex = 0;
            currIndex = 0;
            countDownTimer.start();
        }

        Log.i("111111", "11111111");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        recognizer.stopListening();
    }

    public void textColorHighlighter(String pronouncedWord) {

        String s = mText.getText().toString();
        ForegroundColorSpan span = new ForegroundColorSpan(Color.GREEN);
        SpannableString ss = new SpannableString(s);
        String[] st = s.split(" ");

        if (arrayIndex < st.length) {

            if (pronouncedWord.contains(st[arrayIndex].toLowerCase()
                    .replace(".", "")
                    .replace(",", ""))) {

                String word = st[arrayIndex];
                ss.setSpan(span, 0, currIndex + word.length(), 0);
                currIndex += (word.length() + 1);
                arrayIndex++;
                mText.setText(ss);

            } else {

                String word = st[arrayIndex];
                ss.setSpan(span, 0, currIndex + word.length(), 0);
                mText.setText(ss);
                span = new ForegroundColorSpan(Color.RED);
                ss.setSpan(span, currIndex, currIndex + word.length(), 0);
                mText.setText(ss);
            }
        } else {

            mTextCaptured.setText("Completed");
            countDownTimer.cancel();
        }
    }

}