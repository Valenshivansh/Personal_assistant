package com.example.speechtotext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.example.speechtotext.Greet.wishMe;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextToSpeech tts;
    private Button startButton;
    private SpeechRecognizer speechRecognizer;

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        startButton = findViewById(R.id.startButton);

        requestMicrophonePermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        setupSpeechRecognizer();

        startButton.setOnClickListener(v -> startRecording());
    }

    private void requestMicrophonePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        initTextToSpeech();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void initTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && tts.getEngines().size() > 0) {
                String s = wishMe();
                speak("Hi, I'm Sparky Version-1 " + s);
            } else {
                Toast.makeText(MainActivity.this, "TTS Engine not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private String handleResponse(String msg) {
        String lowerMsg = msg.toLowerCase(Locale.ROOT);

        if (lowerMsg.contains("hello") || lowerMsg.contains("hey") || lowerMsg.contains("hi")) {
            speak("Hello Master Sparky. At your service. What's the order?");
        }

        if (lowerMsg.contains("time")) {
            String time = DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);
            speak("The current time is " + time);
        }

        if (lowerMsg.contains("date")) {
            SimpleDateFormat dt = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
            String todaysDate = dt.format(Calendar.getInstance().getTime());
            speak("The date today is " + todaysDate);
        }
        if (lowerMsg.contains("hello") || lowerMsg.contains("hey") || lowerMsg.contains("hi")) {
            speak("Hello Master Sparky. At your service. What's the order?");
        }

        if (lowerMsg.contains("time")) {
            String time = DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);
            speak("The current time is " + time);
        }

        if (lowerMsg.contains("date")) {
            SimpleDateFormat dt = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
            String todaysDate = dt.format(Calendar.getInstance().getTime());
            speak("The date today is " + todaysDate);
        }
        if (lowerMsg.contains("google")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(intent);
        }
        if (lowerMsg.contains("youtube")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
            startActivity(intent);
        }
        if (lowerMsg.contains("hub")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Valenshivansh"));
            startActivity(intent);
        }
        if (lowerMsg.contains("search")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + lowerMsg.replace("search", " ")));
            startActivity(intent);
        }
        if (lowerMsg.contains("amazon")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/s?k=" + lowerMsg));
            startActivity(intent);
        }
        if (lowerMsg.contains("mdn")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.mozilla.org/en-US/search?q=" + lowerMsg));
            startActivity(intent);
        }
        if (lowerMsg.contains("twitter")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/search?q=" + lowerMsg));
            startActivity(intent);
        }
        if (lowerMsg.contains("reddit")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/search/?q=" + lowerMsg));
            startActivity(intent);
        }
        if (lowerMsg.contains("imdb")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/find?q=" + lowerMsg));
            startActivity(intent);
        }

        if (lowerMsg.contains("remember")) {
            speak("Ok master, I will remember that for you!");
            writeToFile(lowerMsg.replace("remember that", " "));
        }

        if (lowerMsg.contains("know")) {
            String data = readFromFile();
            speak("Yes sir, you told me to remember that: " + data);
        }
        if (lowerMsg.contains("play")) {
            play();
        }
        if (lowerMsg.contains("pause")){
            Pause();
        }
        if (lowerMsg.contains("stop")){
            stop();
        }


        return lowerMsg;
    }

    private void stop() {
        stopPlayer();
    }

    private void Pause() {
        if (player!=null){
            player.pause();
        }
    }

    private void play() {
        if (player==null){
            player = MediaPlayer.create(this , R.raw.song);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }

    private void stopPlayer() {
        if (player!= null){
            player.release();;
            player = null ;
            Toast.makeText(this, "Media player released", Toast.LENGTH_SHORT).show();
        }
    }

    private String readFromFile() {
        String ret = "";
        try {
            InputStream inputStream = openFileInput("data.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String receiveStr;

                while ((receiveStr = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveStr).append("\n");
                }
                inputStream.close();
                ret = stringBuilder.toString().trim();
            }
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "Cannot read file: " + e.toString());
        }
        return ret;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    textView.setText(spokenText);
                    Toast.makeText(MainActivity.this, spokenText, Toast.LENGTH_SHORT).show();
                    handleResponse(spokenText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
