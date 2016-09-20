package com.social.mannu.integrationchatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;

public class MainActivity extends AppCompatActivity {

    Button btnChat, btnLogout;
    UserLogoutTask userLogoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChat = (Button)findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChat = new Intent(MainActivity.this, ConversationActivity.class);
                startActivity(intentChat);
            }
        });

        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });

        MobiComUserPreference userPreference = MobiComUserPreference.getInstance(this);
        if (!userPreference.isRegistered()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return;
        }
    }

    public void onLogout(){
        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener(){

            @Override
            public void onSuccess(Context context) {
                userLogoutTask = null;
                Toast.makeText(getBaseContext(), "Log out successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception exception) {
                userLogoutTask = null;
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(getString(R.string.text_alert));
                alertDialog.setMessage(exception.toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (!isFinishing()) {
                    alertDialog.show();
                }
            }
        };

        userLogoutTask = new UserLogoutTask(userLogoutTaskListener,this);
        userLogoutTask.execute((Void)null);
    }
}
