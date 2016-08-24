package com.example.mare.findgo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mare.findgo.service.ServiceProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    private  UserSignInTask mAuthTask = null;
    private  UserCheckUserTask mCheckTask = null;
    FindGoApp appState;

    private String mUsername;
    private String mPassword;
    private String mName;
    private String mBirthDate;
    private String mAdress;
    private String mPhone;
    private boolean checked;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mBirthDateView;
    private EditText mAdressView;
    private EditText mPhoneView;
    private Button mSignIn;
    private Button mCheckUser;
    private Button mOpenCamera;

    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsernameView = (EditText) findViewById(R.id.etRegUsername);

        mPasswordView = (EditText) findViewById(R.id.etRegPassword);

        mNameView = (EditText) findViewById(R.id.etRegName);

        mBirthDateView = (EditText) findViewById(R.id.etRegBirthDate);

        mAdressView = (EditText) findViewById(R.id.etRegAddress);

        mPhoneView = (EditText) findViewById(R.id.etRegPhone);

        mSignIn = (Button) findViewById(R.id.btRegButton);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked == true)
                    attemptSignin();
                else{
                    checkUser();
                    if(checked == true)
                        attemptSignin();
                }
            }
        });

        mCheckUser = (Button) findViewById(R.id.btCheckUserButton);
        mCheckUser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });

        checked = false;

        mOpenCamera = (Button) findViewById(R.id.btTakePhoto);
        mOpenCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        dispatchTakePictureIntent();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            R.string.no_camera_attached,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(),
                        R.string.error_creating_file,
                        Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                //Uri photoURI = FileProvider.getUriForFile(getActivity(),
                //       "com.morenaict.zztopdroid_mlekara.fileprovider",
                //       photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //mphotoCount++;
            //mPhotoPaths[mphotoCount - 1] = mCurrentPhotoPath;
            //mAvailavblePhotosTextView.setText(getString(R.string.return_photos_no) + " " + mphotoCount);
        }
    }

    public void attemptSignin() {
        if (mAuthTask != null) {
            return;
        }
        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mName = mNameView.getText().toString();
        mBirthDate = mBirthDateView.getText().toString();
        mAdress = mAdressView.getText().toString();
        mPhone = mPhoneView.getText().toString();

        boolean cancel = false;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError("Error");
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError("Error");
            cancel = true;
        }

        // Check for a valid address.
        if (TextUtils.isEmpty(mAdress)) {
            mUsernameView.setError("Error");
            cancel = true;
        }
        // Check for a valid username.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError("Error");
            cancel = true;
        }
        // Check for a valid phone number.
        if (TextUtils.isEmpty(mPhone)) {
            mUsernameView.setError("Error");
            cancel = true;
        }
        // Check for a valid birthday.
        if (TextUtils.isEmpty(mBirthDate)) {
            mUsernameView.setError("Error");
            cancel = true;
        }
        // Check for a valid name.
        if (TextUtils.isEmpty(mName)) {
            mUsernameView.setError("Error");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new  UserSignInTask();
            mAuthTask.execute((Void) null);
        }
    }

    public void checkUser() {
        if (mAuthTask != null) {
            return;
        }
        // Store values at the time of the login attempt.
        mUsername = mUsernameView.getText().toString();

        boolean cancel = false;

        // Check for a valid username.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError("Error");
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mCheckTask = new UserCheckUserTask();
            mCheckTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            /*mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            /*mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);*/
        }
    }


    public class UserSignInTask extends AsyncTask<Void, Void, Boolean> {
        String error = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String result = ServiceProvider.SignInUser(
                        getApplicationContext(), mUsername, mPassword, mName, mAdress, mBirthDate, mPhone, mCurrentPhotoPath);

                appState.Username = mUsername;
            } catch (InterruptedException e) {
                error = e.getMessage();
                return false;
            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return false;
            }

            // TODO store user id for later use
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Context context = SignUpActivity.this;
                context.startActivity(new Intent(context, AppActivity.class));
                finish();
            } else {
                if (error != null) {
                    mPasswordView.setError(error);
                } else {
                    mPasswordView
                            .setError(getString(R.string.error_incorrect_password));
                }
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserCheckUserTask extends AsyncTask<Void, Void, Boolean> {
        String error = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String result = null;

            try {
                result = ServiceProvider.CheckUsername(
                        getApplicationContext(), mUsername);

            } catch (InterruptedException e) {
                error = e.getMessage();
                return false;
            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
                return false;
            }

            // TODO store user id for later use
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Context context = SignUpActivity.this;
                context.startActivity(new Intent(context, MainActivity.class));
            } else {
                if (error != null) {
                    mPasswordView.setError(error);
                } else {
                    mPasswordView
                            .setError(getString(R.string.error_incorrect_password));
                }
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
