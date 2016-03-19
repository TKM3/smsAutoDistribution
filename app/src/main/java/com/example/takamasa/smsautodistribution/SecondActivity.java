package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SecondActivity extends AppCompatActivity
        implements FileSelectionDialog.OnFileSelectListener {

    // オプションメニューID
    private static final int MENUID_FILE = 0;
    // 初期フォルダ
    private String m_strInitialDir = "/";
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliverdReceiver;
    ProgressDialog progressDialog;


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
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(SecondActivity.this);
                btnSmsAutoSend.setTextColor(Color.RED);

                final Intent intent=new Intent(SecondActivity.this,smsSend2.class);

                alertDlg.setTitle("確認");

                alertDlg.setMessage("SMS一括配信処理を開始します。");
                alertDlg.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //OKボタンクリック処理
                                btnSmsAutoSend.setTextColor(Color.WHITE);
                                //ProgressDialogインスタンスを生成
                                progressDialog = new ProgressDialog(SecondActivity.this);
                                //プログレススタイルを設定
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                //キャンセル可能に設定
                                progressDialog.setCancelable(true);
                                //タイトルを設定
                                progressDialog.setTitle("SMS自動送信中");
                                //メッセージを設定
                                progressDialog.setMessage("操作を行わないでください");
                                //ダイアログを表示
                                progressDialog.show();

                                //サービスの起動
                                //startService(intent);
                                smsSend();
                            }
                        });
                alertDlg.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel ボタンクリック処理
                                btnSmsAutoSend.setTextColor(Color.WHITE);
                                //Toast.makeText(SecondActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
                            }
                        });
                // 表示
                alertDlg.create().show();
                //progressDialog.dismiss();
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
                        //Toast.makeText(SecondActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
                    }
                });
        // 表示
        alertDlg.create().show();
    }

    public void smsSend(){
            //メッセージが成功したかどうかをチェックする
            sentPI = PendingIntent.getBroadcast(SecondActivity.this, 0, new Intent(SENT), 0);
            //メッセージの配達が成功したかどうかをチェックする
            deliveredPI = PendingIntent.getBroadcast(SecondActivity.this, 0, new Intent(DELIVERED), 0);
            System.out.println("test111");

                                try {

                                    Thread.sleep(10 * 1000);
                                    SmsManager smsManager = SmsManager.getDefault();
                                    ViewGroup vg = (ViewGroup) findViewById(R.id.tblLayout);
                                    TextView text = (TextView) findViewById(R.id.txtInfo);
                                    TextView txtEndTime = (TextView) findViewById(R.id.txtEndTime);
                                    TextView txtIntervalTime = (TextView) findViewById(R.id.txtIntervalTime);
                                    TextView txtStartTime = (TextView) findViewById(R.id.txtStartTime);
                                    String txt = text.getText().toString();
                                    int trcnt = vg.getChildCount();

                                    // Date型変換
                                    try {
                                        String dateStr = txtIntervalTime.getText().toString();
                                        String endtimeStr = txtEndTime.getText().toString();
                                        String startTimeStr = txtStartTime.getText().toString();


                                        if (trcnt == 0) {
                                            Toast.makeText(SecondActivity.this, "送信に失敗しました", Toast.LENGTH_SHORT).show();
                                        }


                                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                        // Date型変換
                                        Date formatDate = sdf2.parse(dateStr);

                                        for (int j = 0; j < trcnt; j++) {
                                            final TableRow tr = (TableRow) vg.getChildAt(j);

                                            //---create the BroadcastReceiver when the SMS is sent---

                                            smsSentReceiver = new BroadcastReceiver() {
                                                @Override
                                                public void onReceive(Context arg0, Intent arg1) {
                                                    switch (getResultCode()) {
                                                        case Activity.RESULT_OK:
                                                            ((TextView) (tr.getChildAt(2))).setText("送信成功");
                                                            break;
                                                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                                            ((TextView) (tr.getChildAt(2))).setText("送信失敗");
                                                            ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                                            break;
                                                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                            ((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                                            ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                                            break;
                                                        case SmsManager.RESULT_ERROR_NULL_PDU:
                                                            ((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                                            ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                                            break;
                                                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                                                            ((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                                            ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                                            break;
                                                    }
                                                }
                                            };

                                            //---ddddregister the two BroadcastReceivers---
                                            registerReceiver(smsSentReceiver, new IntentFilter(SENT));
                                            registerReceiver(smsDeliverdReceiver, new IntentFilter(DELIVERED));


                                            String destinationAddress = ((TextView) (tr.getChildAt(0))).getText().toString();
                                            smsManager.sendTextMessage(
                                                    destinationAddress,
                                                    null, txt, sentPI, deliveredPI);
                                            Date date = new Date();

                                            //開始時間チェック
                                            Date starttime = sdf2.parse(startTimeStr.toString());
                                            Calendar cal2 = Calendar.getInstance();
                                            Date nowtime = cal2.getTime();
                                            String nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));
                                            Date nowtimestr2 = sdf2.parse(nowtimestr);

                                            int diff = nowtimestr2.compareTo(starttime);
                                            if (diff < 0) {
                                                ((TextView) (tr.getChildAt(1))).setText("WAIT");
                                            } else {
                                                ((TextView) (tr.getChildAt(1))).setText(sdf1.format(date));
                                            }

                                            //終了時間チェック
                                            Calendar cal = Calendar.getInstance();
                                            cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
                                            cal.add(Calendar.MINUTE, formatDate.getMinutes());

                                            Date endtime = sdf2.parse(endtimeStr.toString());

                                            Date nexttime = cal.getTime();
                                            String nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));
                                            Date nexttimestr2 = sdf2.parse(nexttimestr);

                                            int diff2 = nexttimestr2.compareTo(endtime);
                                            if (diff2 < 0) {
                                                int diff3 = nowtimestr2.compareTo(nexttimestr2);
                                                if (diff3 > 0) {
                                                    ((TextView) (tr.getChildAt(3))).setText("END");
                                                } else {
                                                    ((TextView) (tr.getChildAt(3))).setText(sdf1.format(cal.getTime()));
                                                }
                                            } else {
                                                ((TextView) (tr.getChildAt(3))).setText("END");
                                            }

                                        }
                                    } catch (ParseException e) {
                                        Toast.makeText(SecondActivity.this, "例外エラー発生", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (InterruptedException e) {
                                    Toast.makeText(SecondActivity.this, "例外エラー発生", Toast.LENGTH_SHORT).show();
                                }

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
                        CSVParser csvParser = new CSVParser(SecondActivity.this, "address.csv");
                        csvParser.parse();
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

    public class CSVParser {

        private Context context;
        private String file;
        InputStream is = null;
        BufferedReader br = null;
        String text = "";

        public CSVParser(Context context, String file) {
            this.file = file;
            this.context = context;
        }

        public void parse() {
            // AssetManagerの呼び出し
            AssetManager assetManager = context.getResources().getAssets();
            try {
                try {
                    //assetsフォルダ内のinfo.txtをオープンする
                    is = SecondActivity.this.getAssets().open("info.txt");
                    br = new BufferedReader(new InputStreamReader(is));
                    //1行ずつ読込、改行を付加する
                    String str;
                    while ((str = br.readLine()) != null) {
                        text += str + "\n";
                    }
                } finally {
                    if (is != null) is.close();
                    if (br != null) br.close();
                }
                TextView txtinfo = (TextView) findViewById(R.id.txtInfo);
                txtinfo.setText(text);

                // CSVファイルの読み込み
                String empty_str = "-";
                InputStream is = assetManager.open(file);
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferReader = new BufferedReader(inputStreamReader);

                ArrayList<String> strTable = new ArrayList<String>();
                int i = 0;
                //ファイルの件数分ループして配列に格納します。
                while (bufferReader.ready()) {
                    String line = bufferReader.readLine();
                    strTable.add(line);
                    i++;
                }

                CheckBox CheckBox = (CheckBox) findViewById(R.id.cbxSim);
                ViewGroup vg = (ViewGroup) findViewById(R.id.tblLayout);
                TableLayout table = (TableLayout)
                        findViewById(R.id.tblLayout);
                table.removeAllViews();

                boolean isCheckd = CheckBox.isChecked();

                //配列の要素分処理を繰り返し表示する。
                for (int j = 0; j < strTable.size() - 1; j++) {
                    String telstr = strTable.get(j);
                    //行を追加
                    getLayoutInflater().inflate(R.layout.table_row, vg);
                    //文字設定
                    TableRow tr = (TableRow) vg.getChildAt(j);
                    //((TextView)(tr.getChildAt(0))).setText(String.valueOf(j + 1));
                    if (isCheckd == true) {
                        StringBuilder sb = new StringBuilder(telstr);
                        ((TextView) (tr.getChildAt(0))).setText(sb.replace(0, 1, "+81"));
                    } else {
                        ((TextView) (tr.getChildAt(0))).setText(telstr);
                    }
                    ((TextView) (tr.getChildAt(1))).setText(empty_str);
                    ((TextView) (tr.getChildAt(2))).setText(empty_str);
                    ((TextView) (tr.getChildAt(3))).setText(empty_str);
                }
                bufferReader.close();

            } catch (IOException e) {
                Toast.makeText(SecondActivity.this, "読込に失敗しました", Toast.LENGTH_SHORT);
            }
        }
    }
}
