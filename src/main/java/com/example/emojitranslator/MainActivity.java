package com.example.emojitranslator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE = 100;
    private Toolbar mToolbar;
    private TextInputLayout inputField;
    private EditText outputField;
    private Button translateButton;
    private ToggleButton switchButton;
    private ImageView ttsButton;
    private ImageView sttButton;
    private ListView listView;
    private EditText outputHeading;
    private EditText inputHeading;
    TextToSpeech t1;
    private List<String> result;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        inputField = findViewById(R.id.inputTextField);
        outputField = findViewById(R.id.outputTextField);
        translateButton = findViewById(R.id.translateButton);
        ttsButton = findViewById(R.id.ttsButton);
        sttButton = findViewById(R.id.sttButton);
        inputHeading = findViewById(R.id.inputHeading);
        inputHeading.setText("Emoji");
        outputHeading = findViewById(R.id.outputHeading);
        outputHeading.setText("Description");
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = outputField.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });



        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!switchButton.isChecked()) {
                    String input = inputField.getEditText().getText().toString();
                    String url = "http://10.0.2.2:8080/getDescription?emojiIcon=" + input;
                    new MyTask().execute(url);
                } else {
                    String input = inputField.getEditText().getText().toString();
                    String url = "http://10.0.2.2:8080/getTagMatches?tags=" + input;
                    new MyTask().execute(url);

                }

            }
        });

        switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputHeading.setText("Description");
                    outputHeading.setText("Emoji");
                } else {
                    inputHeading.setText("Emoji");
                    outputHeading.setText("Description");
                }
            }
        });
        sttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String words = result.get(0);
                    String url = "http://10.0.2.2:8080/getTagMatches?tags=" + words;
                    new MyTask().execute(url);
                }
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            outputField.setText(StringEscapeUtils.unescapeJava(result));
        }
    }

}
