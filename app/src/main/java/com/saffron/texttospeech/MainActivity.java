package com.saffron.texttospeech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener, View.OnClickListener {

    private TextToSpeech textToSpeech;
    private ImageButton buttonSpeaker, buttonSpeakerVoice;
    private EditText inputText;

    int currIndex = 0, arrayIndex = 0;
    private TextView outputText;
    private LinearLayout layoutSpeakResult;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private SpeechRecognizer recognizer;
    private RecognitionListener listener;
    private Intent intent;
    private boolean firstTime = true;

    @SuppressLint("StaticFieldLeak")
    public static EditText et_fromText, et_toText;
    public String language_code;
    Spinner spinnerLanguages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        textToSpeech = new TextToSpeech(this, this);
        inputText = findViewById(R.id.txtSpeaker);
        buttonSpeaker = findViewById(R.id.btnSpeakOut);
        buttonSpeaker.setOnClickListener(this);

        buttonSpeakerVoice = findViewById(R.id.btnSpeakOutresult);
        buttonSpeakerVoice.setOnClickListener(this);
        layoutSpeakResult = findViewById(R.id.layout_speakResult);


        outputText = findViewById(R.id.txtMic);
        ImageButton buttonMic = findViewById(R.id.btnMic);
        buttonMic.setOnClickListener(this);

        ImageButton buttonParagraph = findViewById(R.id.btnPara);
        buttonParagraph.setOnClickListener(this);

        ImageButton buttonTranslation = findViewById(R.id.btnTranslation);
        buttonTranslation.setOnClickListener(this);

        ImageButton buttonVoiceRecording = findViewById(R.id.btnVoiceRec);
        buttonVoiceRecording.setOnClickListener(this);

        onCreateTranslation();
        // hide the action bar
        //getActionBar().hide();
    }

    @Override
    public void onClick(View v) {

        //inputText.setText("");
        //outputText.setText("");

        switch (v.getId()) {

            case R.id.btnSpeakOut:
                //speakOut();
                inputText.setText("");
                outputText.setText("");
                inputText.setVisibility(View.VISIBLE);
                layoutSpeakResult.setVisibility(View.VISIBLE);
                outputText.setVisibility(View.GONE);
                break;

            case R.id.btnSpeakOutresult:
                speakOut();
                break;

            case R.id.btnMic:
                promptSpeechInput();
                inputText.setVisibility(View.GONE);
                layoutSpeakResult.setVisibility(View.GONE);
                outputText.setVisibility(View.VISIBLE);
                break;

            case R.id.btnPara:
                Intent intent = new Intent(MainActivity.this, VoiceRecognitionTest.class);
                startActivity(intent);
                break;

            case R.id.btnTranslation:
                Intent intent2 = new Intent(MainActivity.this, TranslationActivity.class);
                startActivity(intent2);
                break;

            case R.id.btnVoiceRec:
                Intent intent3 = new Intent(MainActivity.this, RecordingActivity.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            // textToSpeech.setPitch(5); // set pitch level

            // textToSpeech.setSpeechRate(2); // set speech speed rate

            if ((result == TextToSpeech.LANG_MISSING_DATA)
                    || (result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                Log.e("TTS", "Language is not supported");
            } else {
                buttonSpeaker.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }

    private void speakOut() {

        textToSpeech.speak(inputText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        //speechRecognition();

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_CODE_SPEECH_INPUT: {

                if ((resultCode == RESULT_OK) && (null != data)) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    outputText.setText(result.get(0));
                }

                break;
            }

        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/


    public void speechRecognition() {

        if (listener == null){

            listener = new RecognitionListener() {

                @Override
                public void onReadyForSpeech(Bundle params) {
                    //Toast.makeText(MainActivity.this, "onReadyForSpeech", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBeginningOfSpeech() {
                    //Toast.makeText(MainActivity.this, "onBeginningOfSpeech", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    //Toast.makeText(MainActivity.this, "onRmsChanged", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    //Toast.makeText(MainActivity.this, "onBufferReceived", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onEndOfSpeech() {
                    //Toast.makeText(MainActivity.this, "onEndOfSpeech", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int error) {
                    //Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    //Toast.makeText(MainActivity.this, "onResults", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                    ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    String word = (String) data.get(data.size() - 1);
                    outputText.setText(word);

                    Log.i("TEST", "partial_results: " + word);
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    //Toast.makeText(MainActivity.this, "onEvent", Toast.LENGTH_SHORT).show();
                }
            };
        }


        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            if (recognizer == null){
                //recognizer = SpeechRecognizer.createSpeechRecognizer(this);
                recognizer = SpeechRecognizer.createSpeechRecognizer(this, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
                recognizer.setRecognitionListener(listener);
            }

            recognizer.startListening(intent);
        }
    }


    public void textColorHighlighter() {

        String s = outputText.getText().toString();
        ForegroundColorSpan span = new ForegroundColorSpan(Color.GREEN);
        SpannableString ss = new SpannableString(s);
        String[] st = s.split(" ");

		/*for (String word : st) {
            //if (word.startsWith("#"))
			{
				ss.setSpan(span, 0,currIndex+ word.length(), 0);
			}
			currIndex += (word.length() + 1);
		}*/

        if (arrayIndex < st.length) {

            String word = st[arrayIndex];
            ss.setSpan(span, 0, currIndex + word.length(), 0);
            currIndex += (word.length() + 1);
            arrayIndex++;
            outputText.setText(ss);
        }

    }


    public int countWords(String s) {

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && (i != endOfLine)) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && (i == endOfLine)) {
                wordCount++;
            }
        }
        return wordCount;
    }


    protected void onCreateTranslation() {

        final String[] arraySpinner = new String[]{"Select Language", "English", "French", "Dutch",};
        final String[] languageArray = new String[]{"", "en", "fr", "de"};
        spinnerLanguages = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spinnerLanguages.setAdapter(adapter);

        et_fromText = findViewById(R.id.et_from);
        et_toText = findViewById(R.id.et_translation);
        Button btnTranslate = findViewById(R.id.translate);

        spinnerLanguages = findViewById(R.id.spinner);
        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!et_fromText.getText().toString().isEmpty()) {

                    if (position == 0) {
                        et_toText.setText("");
                    } else if (position == 1) {
                        et_toText.setText(et_fromText.getText().toString());
                    } else {

                        language_code = "en%7C" + languageArray[position];
                        new LongOperation(language_code).execute("");
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_fromText.getText().toString().isEmpty()) {

                    if (spinnerLanguages.getSelectedItemPosition() == 0) {
                        et_toText.setText("");
                    } else if (spinnerLanguages.getSelectedItemPosition() == 1) {
                        et_toText.setText(et_fromText.getText().toString());
                    } else
                        new LongOperation(language_code).execute("");
                }


            }
        });


    }

    public static class LongOperation extends AsyncTask<String, Void, String> {

        String result, inputLine, language_code;

        LongOperation(String language_code) {
            this.language_code = language_code;
        }

        @Override
        protected String doInBackground(String... params) {
            return getResult();
        }

        @Override
        protected void onPostExecute(String result) {
            et_toText.setText(result);
        }


        String getResult() {

            String a1 = et_fromText.getText().toString().replace(" ", "%20");

            try {

                String url = "http://mymemory.translated.net/api/get?q=" + a1 + "&langpair=" + language_code;

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");


                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject j = new JSONObject(response.toString());
                    String a = j.getString("responseData");
                    JSONObject j1 = new JSONObject(a);
                    result = j1.getString("translatedText");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}