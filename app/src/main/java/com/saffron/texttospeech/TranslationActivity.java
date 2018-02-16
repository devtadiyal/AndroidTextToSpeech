package com.saffron.texttospeech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TranslationActivity extends Activity {

    @SuppressLint("StaticFieldLeak")
    public static EditText inputText, outputText;
    public String language_code;
    Spinner spinnerLanguages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation_layout);

        final String[] arraySpinner = new String[]{"Translate to", "English", "French", "Dutch",};
        final String[] languageArray = new String[]{"", "en", "fr", "de"};
        spinnerLanguages = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        spinnerLanguages.setAdapter(adapter);

        inputText = findViewById(R.id.et_from);
        outputText = findViewById(R.id.et_translation);
        Button btnTranslate = findViewById(R.id.translate);

        spinnerLanguages = findViewById(R.id.spinner);
        spinnerLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!inputText.getText().toString().isEmpty()) {

                    if (position == 0) {
                        outputText.setText("");
                    } else if (position == 1) {
                        outputText.setText(inputText.getText().toString());
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

                if (!inputText.getText().toString().isEmpty()) {

                    if (spinnerLanguages.getSelectedItemPosition() == 0) {
                        outputText.setText("");
                    } else if (spinnerLanguages.getSelectedItemPosition() == 1) {
                        outputText.setText(inputText.getText().toString());
                    } else
                        new LongOperation(language_code).execute("");
                }


            }
        });


    }


    public int countWords(String s) {

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
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
            outputText.setText(result);
        }


        String getResult() {

            String a1 = inputText.getText().toString().replace(" ", "%20");

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