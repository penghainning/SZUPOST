package com.example.phn.szupost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class Changename extends AppCompatActivity {


    private EditText change;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.activityList.add(this);
        setContentView(R.layout.changename_layout);
        ActionBar ab = getSupportActionBar();
        Intent intent=getIntent();
        change=(EditText)findViewById(R.id.changename);
        change.setText(intent.getExtras().get("name").toString());
        change.requestFocus();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("更改名字");
        ab.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.item1:
                final ProgressDialog proDialog =ProgressDialog.show(this,"","正在保存中...");

                Thread thread = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            sleep(1000);

                        } catch (InterruptedException e) {
                            // TODO 自动生成的 catch 块
                            e.printStackTrace();
                        }
                        proDialog.dismiss();//万万不可少这句，否则会程序会卡死。
                        Intent intent=new Intent();
                        intent.putExtra("name",change.getText());
                        setResult(0,intent);
                        Changename.this.finish();

                    }
                };
                thread.start();

                break;
            case android.R.id.home:// 点击返回图标事件
                Intent intent1=new Intent();
                intent1.putExtra("name",change.getText());
                setResult(1,intent1);
                this.finish();
            default:
                break;
        }
        return  super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("name",change.getText());
        setResult(1,intent);
        this.finish();
    }

}
