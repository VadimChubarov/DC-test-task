package com.example.vadim.dc_test_task;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;




public class GoogleDriveService
{
    private MainActivity targetActivity;
    private Executor mExecutor;
    private static GoogleDriveService googleDriveService;

    private GoogleDriveService(MainActivity targetActivity)
    {
       this.targetActivity = targetActivity;
       this.mExecutor = Executors.newSingleThreadExecutor();
       googleDriveService = this;
    }

    public static GoogleDriveService getInstance(MainActivity targetActivity)
    {
       if(googleDriveService == null){return new GoogleDriveService(targetActivity);}
       else {
               googleDriveService.setTargetActivity(targetActivity);
               return googleDriveService;
            }
    }

    public void setTargetActivity(MainActivity targetActivity) {
        this.targetActivity = targetActivity;
    }

    public void requestSignIn()
    {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(targetActivity, signInOptions);
        targetActivity.startActivityForResult(client.getSignInIntent(), 1);
    }

    public  void proceedSignIn(Intent result) {

        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount ->
                {
                    Toast.makeText(targetActivity,"Signed in as " + googleAccount.getEmail(), Toast.LENGTH_SHORT).show();

                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    targetActivity, Collections.singleton(DriveScopes.DRIVE_READONLY));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("DC test task")
                                    .build();

                    targetActivity.onGoogleServiceReady(googleDriveService);
                })
                .addOnFailureListener(exception ->{
                    targetActivity.onNetworkFailure();
                    Log.e("Error", "Unable to sign in.", exception);});
    }

    public void readTextFile(String fileId, Drive driveService) {
        Tasks.call(mExecutor, () -> {
            File metadata = driveService.files().get(fileId).execute();
            String name = metadata.getName();

            try (InputStream is = driveService.files().get(fileId).executeMediaAsInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                 StringBuilder stringBuilder = new StringBuilder();
                 String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

               return Pair.create(name, contents);
            }
        }).addOnSuccessListener(nameAndContent -> { targetActivity.onFileReadingReady(nameAndContent.second);})
                .addOnFailureListener(exception ->{
                        targetActivity.onNetworkFailure();
                        Log.e("Error", "Couldn't read file.", exception);});
    }
}
