package com.example.mare.findgo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button mSignUp;
    private  Button mLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLogIn = (Button) findViewById(R.id.btnMainLogin);
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                context.startActivity(new Intent(context, LogInActivity.class));
            }
        });

        mSignUp = (Button) findViewById(R.id.btnMainRegister);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                context.startActivity(new Intent(context, SignUpActivity.class));
            }
        });

        DeleteOldFiles();

    }

    public void DeleteOldFiles()
    {
        int filesDeleted;
        filesDeleted = 0;
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] fList = storageDir.listFiles();
        for (File f : fList) {
            if (f.isFile() && IsFileOld(f))
            {
                f.delete();
                filesDeleted++;
            }
        }
        //String text = getString(R.string.deleted_old_files);
        //text = text + " " + filesDeleted;
        if(filesDeleted != 0)
        {
            /*Toast.makeText(getApplicationContext(),
                    text,
                    Toast.LENGTH_SHORT).show();*/
        }
    }

    public boolean IsFileOld(File file)
    {
        if(file.exists()) {
            Calendar time = Calendar.getInstance();
            time.add(Calendar.DAY_OF_YEAR, -8);
            //time.add(Calendar.HOUR_OF_DAY, -1);

            Date lastModified = new Date(file.lastModified());
            if (lastModified.before(time.getTime())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
