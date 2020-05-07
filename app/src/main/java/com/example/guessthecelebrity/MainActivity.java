package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ArrayList<String> answers= new ArrayList<String>();
    ArrayList<String> celebsURLs = new ArrayList<String>();
    ArrayList<String> celebsNames= new ArrayList<String>();
    int correctAns;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result="";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!=-1){
                    char current = (char) data;

                    result+=current;
                    data=reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }

        }






    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result =null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            //Log.i("Result",result);

            String[] splitResult =  result.split(("<div class=\"col-xs-12 col-sm-6 col-md-4\">"));


            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebsURLs.add(m.group(1));

             }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
            celebsNames.add(m.group(1));
            }

        } catch (Exception e) { //fills link and image into  arrays
                e.printStackTrace();
        }


        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        setImage();
    }



    public void setImage (){

        Random rand = new Random();
        int a = rand.nextInt(celebsURLs.size());

        String correctUrl = celebsURLs.get(a);
        String correctName = celebsNames.get(a);
        correctAns = rand.nextInt(4);
        answers.clear();
        for(int i=0;i<4;i++){
            if(i==correctAns){
                answers.add(correctName);
            }
            else{
                int wrongAnswer = rand.nextInt(celebsURLs.size());
                while(wrongAnswer==correctAns){
                    wrongAnswer = rand.nextInt(celebsURLs.size());
                }
                answers.add(celebsNames.get(wrongAnswer));
            }
        }
        button0.setText(answers.get(0));
        button1.setText(answers.get(1));
        button2.setText(answers.get(2));
        button3.setText(answers.get(3));


        ImageDownloader task = new ImageDownloader();

        try {
            Bitmap celebImage= task.execute(correctUrl).get();

            imageView.setImageBitmap(celebImage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void selectAnswer(View view){
    if(view.getTag().toString().equals( Integer.toString( correctAns))){
        Toast.makeText(MainActivity.this,"Correct",Toast.LENGTH_SHORT).show();
    }
    else{
        Toast.makeText(MainActivity.this,"Wrong :'(  It was " + celebsNames.get(correctAns),Toast.LENGTH_SHORT).show();

    }
    setImage();
    }

}
