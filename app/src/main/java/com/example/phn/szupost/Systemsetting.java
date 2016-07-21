package com.example.phn.szupost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableRow;

public class Systemsetting extends AppCompatActivity {
    private TableRow quitlogin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.activityList.add(this);
        setContentView(R.layout.systemsetting_layout);
        quitlogin=(TableRow)findViewById(R.id.squitlogin);
        quitlogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(Systemsetting.this,Login.class);
                startActivity(intent);
                ActivityManager.exitClient();
            }
        });
    }
}
