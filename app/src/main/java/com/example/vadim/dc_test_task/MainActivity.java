package com.example.vadim.dc_test_task;

import com.example.vadim.dc_test_task.PokerApp.PokerActivity;
import com.google.api.services.drive.Drive;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private String fileId;
    private String openParameter;
    private String link;

    private WebView webView;
    private ProgressBar progressBar;
    private GoogleDriveService googleDriveService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileId = ""; // to be inserted file ID from Google Drive

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);
        googleDriveService = GoogleDriveService.getInstance(this);

        if(savedInstanceState==null){
            showLoading(true);
            googleDriveService.requestSignIn();
        }
        else if(savedInstanceState.get("link")!=null){
            link = (String)savedInstanceState.get("link");
            webView.loadUrl(link);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("link",link);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode==1 && resultCode == Activity.RESULT_OK && resultData != null) {
            googleDriveService.proceedSignIn(resultData);
        }
        else{runPokerApp();}
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    public void onGoogleServiceReady(Drive drive) { googleDriveService.readTextFile(fileId,drive); }

    public void onFileReadingReady(String fileContent) {
        getLaunchParameters(fileContent);
        if(openParameter==null){runPokerApp();}
        else{launchApp();
        showLoading(false);}
    }

    public void onNetworkFailure() { runPokerApp();}

    public void getLaunchParameters (String fileContent) {
        try{
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(fileContent);
            openParameter = jsonObject.get("Open").toString().replace("\"","");
            link = jsonObject.get("Openlink").toString().replace("\"","");
        }catch (Exception e){}
    }

    public void launchApp() {
        switch (openParameter) {
            case "application" :
                Toast.makeText(this,"RUN POKER", Toast.LENGTH_SHORT).show();
                runPokerApp();
                break;

            case "link" :
                Toast.makeText(this,"RUN LINK"+link, Toast.LENGTH_SHORT).show();
                webView.loadUrl(link);
                break;

            default :
                Toast.makeText(this,"RUN POKER", Toast.LENGTH_SHORT).show();
                runPokerApp();
            }
    }

    private void runPokerApp() {
        showLoading(false);
        Intent pokerIntent = new Intent(this,PokerActivity.class);
        startActivity(pokerIntent);
    }

    public void showLoading(boolean show) {
        if(show){ progressBar.setVisibility(View.VISIBLE); }
        else { progressBar.setVisibility(View.INVISIBLE); }
    }
}

