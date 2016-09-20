package com.social.mannu.integrationchatapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.social.mannu.integrationchatapp.fcmnotification.FCMRegistrationUtils;

public class LoginActivity extends AppCompatActivity {

    EditText txtUserID,txtNumber,txtEmailID,txtDisplayName,txtPassword;
    Button btnRegister;
    MobiComUserPreference mobiComUserPreference;
    UserLoginTask authTask = null;
    View progressView;
    View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUserID = (EditText)findViewById(R.id.txtUserID);
        txtNumber = (EditText)findViewById(R.id.txtNumber);
        txtEmailID = (EditText)findViewById(R.id.txtEmailID);
        txtDisplayName = (EditText)findViewById(R.id.txtDisplayName);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        mobiComUserPreference = MobiComUserPreference.getInstance(this);
        mobiComUserPreference.setUrl("https://apps.applozic.com");

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(User.AuthenticationType.APPLOZIC);
            }
        });

    }

    public void attemptLogin(User.AuthenticationType authenticationType) {
        if (authTask != null) {
            return;
        }

        // Reset errors.
        txtUserID.setError(null);
        txtEmailID.setError(null);
        txtPassword.setError(null);
        txtDisplayName.setError(null);

        // Store values at the time of the login attempt.
        String email = txtEmailID.getText().toString();
        String phoneNumber = txtNumber.getText().toString();
        String userId = txtUserID.getText().toString();
        String password = txtPassword.getText().toString();
        String displayName = txtDisplayName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(txtUserID.getText().toString()) || txtUserID.getText().toString().trim().length() == 0) {
            txtUserID.setError(getString(R.string.error_field_required));
            focusView = txtUserID;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if ((TextUtils.isEmpty(txtPassword.getText().toString())||txtPassword.getText().toString().trim().length() == 0) && !isPasswordValid(txtPassword.getText().toString())) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmailID.setError(getString(R.string.error_field_required));
            focusView = txtEmailID;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtEmailID.setError(getString(R.string.error_invalid_email));
            focusView = txtEmailID;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            /*mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;*/
        } else if (!isEmailValid(email)) {
            txtEmailID.setError(getString(R.string.error_invalid_email));
            focusView = txtEmailID;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            // callback for login process
            final Activity activity = LoginActivity.this;
            UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

                @Override
                public void onSuccess(RegistrationResponse registrationResponse, final Context context) {
                    authTask = null;
                    showProgress(false);
                    ApplozicSetting.getInstance(context).showStartNewButton().showPriceOption();

                    //Basic settings...

                    ApplozicSetting.getInstance(context).hideConversationContactImage().hideStartNewButton().hideStartNewFloatingActionButton();

                    ApplozicSetting.getInstance(context).showStartNewGroupButton()
                            .setCompressedImageSizeInMB(5)
                            .enableImageCompression()
                            .setMaxAttachmentAllowed(5);
                    ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true);
                    ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();//To enable the applozic Registered Users Contact Note:for disable that you can comment this line of code

                    //Set activity callbacks
                    /*Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                    activityCallbacks.put(ApplozicSetting.RequestCode.MESSAGE_TAP, MainActivity.class.getName());
                    ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);*/

                    //Start GCM registration....
                    FCMRegistrationUtils fcmRegistrationUtils = new FCMRegistrationUtils(activity);
                    fcmRegistrationUtils.setUpFcmNotification();

                    //starting main MainActivity
                    Intent mainActvity = new Intent(context, MainActivity.class);
                    startActivity(mainActvity);
                    Intent intent = new Intent(context, ConversationActivity.class);
                    if(ApplozicClient.getInstance(LoginActivity.this).isContextBasedChat()){
                        intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT,true);
                    }
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    authTask = null;
                    showProgress(false);

                    btnRegister.setVisibility(View.VISIBLE);
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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

            User user = new User();
            user.setUserId(userId);
            user.setEmail(email);
            user.setPassword(password);
            user.setDisplayName(displayName);
            user.setContactNumber(phoneNumber);
            user.setAuthenticationTypeId(authenticationType.getValue());

            authTask = new UserLoginTask(user, listener, this);
            btnRegister.setVisibility(View.INVISIBLE);
            authTask.execute((Void) null);
        }

    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
