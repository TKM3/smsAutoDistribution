package com.example.takamasa.smsautodistribution;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Toast.makeText(MainActivity.this, "パスワードを入力し、ログインして下さい", Toast.LENGTH_LONG).show();

        //ボタンを設定
        final Button btnLgi = (Button) findViewById(R.id.btnLgi);
        final Button btnExit = (Button) findViewById(R.id.btnExit);

        //リスナーにボタンを登録
        btnLgi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //押下時にテキストの色を変更
                btnLgi.setTextColor(Color.RED);
                lgiExe();
            }
        });
        //リスナーにボタンを登録
        btnLgi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //押下時にテキストの色を変更
                btnLgi.setTextColor(Color.RED);
                lgiExe();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog();
            }
        });
    }

    //ログイン画面にて【EXIT】ボタンを押下した時の処理
    public void exitDialog() {
        // 確認ダイアログの生成
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("確認");
        alertDlg.setMessage("終了します。よろしいですか？");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        finish();
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                        //Toast.makeText(MainActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
                    }
                });
        // 表示
        alertDlg.create().show();
    }
    private void lgiExe(){
        //エディットテキストを設定
        EditText edtPwd = (EditText) findViewById(R.id.edtPwd);
        final Button btnLgi = (Button) findViewById(R.id.btnLgi);
        if (edtPwd.getText().toString().equals("")){
            Toast.makeText(MainActivity.this,"パスワードが入力されていません",Toast.LENGTH_SHORT).show();
            btnLgi.setTextColor(Color.WHITE);
        }else if(edtPwd.getText().toString().equals("pwd01")) {
            // インテントの生成
            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
            // SubActivity の起動
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this,"パスワードが誤っています",Toast.LENGTH_SHORT).show();
            btnLgi.setTextColor(Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        super.onPause();
        final Button btnLgi = (Button) findViewById(R.id.btnLgi);
        btnLgi.setTextColor(Color.WHITE);
    }

}
