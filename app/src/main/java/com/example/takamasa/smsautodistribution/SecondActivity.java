package com.example.takamasa.smsautodistribution;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SecondActivity extends AppCompatActivity
        implements FileSelectionDialog.OnFileSelectListener {

    // 初期フォルダ
    String m_strInitialDir = "/";
    AsyncSMSsend AsynkSMSsendTask;
    AsyncCSVread AsyncCSVreadTask;
    AlertDialog.Builder alertDlg;
    int argTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /////////////////////////////////////////////////
        File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Log.d("Multi", "getExternalFilesDirs→→→→→" + storage);

        final Button btnExitSecond = (Button) findViewById(R.id.btnExitSecond);
        final Button btnCsvRead = (Button) findViewById(R.id.btnCsvRead);
        final Button btnStartTimeSet = (Button) findViewById(R.id.btnStartTimeSet);
        final Button btnEndTimeSet = (Button) findViewById(R.id.btnEndTimeSet);
        final Button btnIntervalSet = (Button) findViewById(R.id.btnIntervalSet);
        final Button btnSmsAutoSend = (Button) findViewById(R.id.btnSmsAutoSend);
        final CheckBox CheckBox = (CheckBox) findViewById(R.id.cbxSim);

        final ViewGroup vg = (ViewGroup) findViewById(R.id.tblLayout);
        final TableLayout table = (TableLayout) findViewById(R.id.tblLayout);
        final TextView text = (TextView) findViewById(R.id.txtInfo);
        final TextView txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        final TextView txtIntervalTime = (TextView) findViewById(R.id.txtIntervalTime);
        final TextView txtStartTime = (TextView) findViewById(R.id.txtStartTime);


        //タスクの生成
        AsynkSMSsendTask=new AsyncSMSsend(this,vg,text,txtEndTime,txtIntervalTime,txtStartTime);
        AsyncCSVreadTask=new AsyncCSVread(this,text,CheckBox,vg,table);
        btnExitSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnExitSecond.setTextColor(Color.RED);
                exitDialogSecond();
            }
        });

        btnCsvRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCsvRead.setTextColor(Color.RED);
                csvRead();
            }
        });

        btnSmsAutoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDlg = new AlertDialog.Builder(SecondActivity.this);

                btnSmsAutoSend.setTextColor(Color.RED);

                alertDlg.setTitle("確認");

                alertDlg.setMessage("SMS一括配信処理を開始します。");
                alertDlg.setPositiveButton(
                        "OK",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //OKボタンクリック処理
                                btnSmsAutoSend.setTextColor(Color.WHITE);

                                String dateStr = txtIntervalTime.getText().toString();
                                String endtimeStr = txtEndTime.getText().toString();
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

                                try {
                                    Date formatDate = sdf2.parse(dateStr);

//                                  //終了時間チェック
                                    Calendar cal = Calendar.getInstance();
                                    cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
                                    cal.add(Calendar.MINUTE, formatDate.getMinutes());

                                    //現在時間チェック
                                    Calendar cal2 = Calendar.getInstance();
                                    Date nowtime = cal2.getTime();
                                    Date endtime = sdf2.parse(endtimeStr.toString());

                                    String endtimeC = (String.format(String.format("%1$02d", endtime.getHours()) + "." + String.format("%1$02d", endtime.getMinutes())));
                                    String nowtimeC = (String.format(String.format("%1$02d", nowtime.getHours()) + "." + String.format("%1$02d", nowtime.getMinutes())));
                                    double endtimeCint = Double.parseDouble(endtimeC);
                                    double nowtimeCint = Double.parseDouble(nowtimeC);
                                    double difftimeC = endtimeCint - nowtimeCint;
                                    int difftimeCint = (int) difftimeC;
//
                                    String intervalC = dateStr.substring(0, 2);
                                    int intervalCint = Integer.parseInt(intervalC);
                                    argTask = difftimeCint / intervalCint;
                                } catch (Exception e) {
                                    System.out.println("e");
                                }
                                AsynkSMSsendTask.execute(argTask);
                            }
                        });
                alertDlg.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel ボタンクリック処理
                                btnSmsAutoSend.setTextColor(Color.WHITE);
                                return;
                            }
                        });
                // 表示
                alertDlg.create().show();
            }
        });


        btnStartTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartTimeSet.setTextColor(Color.RED);
                timeSet(1);
            }
        });

        btnEndTimeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEndTimeSet.setTextColor(Color.RED);
                timeSet(2);
            }
        });

        btnIntervalSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIntervalSet.setTextColor(Color.RED);
                timeSet(3);
            }
        });

        //チェックボックスがクリックされた時に呼び出されてるコールバックリスナーを登録
        CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                boolean checked = checkBox.isChecked();
                if (checked == true) {
                    Toast.makeText(SecondActivity.this, "海外SIM利用:ON\nCSV再読込みして下さい", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SecondActivity.this, "海外SIM利用:OFF\nCSV再読込みして下さい", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //セカンド画面にて【EXIT】ボタンを押下した時の処理
    public void exitDialogSecond() {
        // 確認ダイアログの生成
        final Button btnExitSecond = (Button) findViewById(R.id.btnExitSecond);
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("確認");
        alertDlg.setMessage("ログアウトします。よろしいですか？");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        btnExitSecond.setTextColor(Color.WHITE);
                        finish();
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                        btnExitSecond.setTextColor(Color.WHITE);
                    }
                });
        // 表示
        alertDlg.create().show();
    }


    //セカンド画面にて【CSV読込み】ボタンを押下した時の処理
    public void csvRead() {

        // 確認ダイアログの生成
        final Button btnCsvRead = (Button) findViewById(R.id.btnCsvRead);
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("確認");
        alertDlg.setMessage("宛先CSVを選択して下さい。");

        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OKボタンクリック処理
                        btnCsvRead.setTextColor(Color.BLACK);
                        AsyncCSVreadTask.execute(argTask);
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                        btnCsvRead.setTextColor(Color.BLACK);
                    }
                });
        // 表示
        alertDlg.create().show();
    }

    // ファイルが選択されたときに呼び出される関数
    public void onFileSelect(File file) {
        Toast.makeText(this, "File Selected : " + file.getPath(), Toast.LENGTH_SHORT).show();
        m_strInitialDir = file.getParent();
    }

    //時刻設定ダイアログ
    public void timeSet(final int n) {
        final TextView txtStartTime = (TextView) findViewById(R.id.txtStartTime);
        final TextView txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        final TextView txtIntervalTime = (TextView) findViewById(R.id.txtIntervalTime);
        final Button btnStartTimeSet = (Button) findViewById(R.id.btnStartTimeSet);
        final Button btnEndTimeSet = (Button) findViewById(R.id.btnEndTimeSet);
        final Button btnIntervalSet = (Button) findViewById(R.id.btnIntervalSet);

        //カレンダーインスタンスを取得
        Calendar date = Calendar.getInstance();
        //TimePickerDialogインスタンスを取得
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        SecondActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                switch (n) {
                                    case 1:
                                        //引数n:開始時刻
                                        txtStartTime.setText(String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute));
                                        break;
                                    case 2:
                                        //引数n:終了時刻
                                        txtEndTime.setText(String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute));
                                        break;
                                    case 3:
                                        //引数n:配信間隔
                                        txtIntervalTime.setText(String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute));
                                        txtIntervalTime.setText(String.format("%1$02d", hourOfDay) + ":00");

                                        String chkStr=txtIntervalTime.getText().toString().substring(0,2);
                                        if (chkStr.equals("00")){
                                            Toast.makeText(SecondActivity.this,"1時間未満は設定できません",Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                        },
                        date.get(Calendar.HOUR_OF_DAY),
                        date.get(Calendar.MINUTE),
                        true
                );
        btnStartTimeSet.setTextColor(Color.BLACK);
        btnEndTimeSet.setTextColor(Color.BLACK);
        btnIntervalSet.setTextColor(Color.BLACK);
        //タイトルをセット
        timePickerDialog.setTitle("タイムセット");

        //ダイアログを表示
        timePickerDialog.show();
    }
}
