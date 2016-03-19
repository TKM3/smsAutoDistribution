package com.example.takamasa.smsautodistribution;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by takamasa on 2016/03/13.
 */
public class smsSend2 extends Service {
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
    public IBinder onBind(Intent intent) {
        System.out.println("test111");
        return null;
    }

    public void onStart(Intent intent,int startID){
        //開始時にトーストを表示
        Toast.makeText(this, "サービスを開始しました！", Toast.LENGTH_LONG).show();
        //メッセージが成功したかどうかをチェックする
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        //メッセージの配達が成功したかどうかをチェックする
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        //OKボタンクリック処理

        //ProgressDialogインスタンスを生成
        progressDialog = new ProgressDialog(this);
        //プログレススタイルを設定
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //キャンセル可能に設定
        progressDialog.setCancelable(true);
        //タイトルを設定
        progressDialog.setTitle("SMS自動送信中");
        //メッセージを設定
        progressDialog.setMessage("操作を行わないでください");
        //ダイアログを表示
        //progressDialog.show();

        for (int i = 0; i < 3; i++) {
            try {
                //Thread.sleep(3 * 1000);
                SmsManager smsManager = SmsManager.getDefault();
                String destinationAddress = "+19178922262";
                String txt = "hello";
                smsManager.sendTextMessage(
                        destinationAddress,
                        null, txt, sentPI, deliveredPI);

            } catch (Exception e) {
                Toast.makeText(this, "例外エラー発生", Toast.LENGTH_SHORT).show();
            }
        }

        //do {
//        try {
//
//            Thread.sleep(10 * 1000);
//            SmsManager smsManager = SmsManager.getDefault();
//            ViewGroup vg = (ViewGroup) findViewById(R.id.tblLayout);
//            TextView text = (TextView) findViewById(R.id.txtInfo);
//            TextView txtEndTime = (TextView) findViewById(R.id.txtEndTime);
//            TextView txtIntervalTime = (TextView) findViewById(R.id.txtIntervalTime);
//            TextView txtStartTime = (TextView) findViewById(R.id.txtStartTime);
//            String txt = text.getText().toString();
//            int trcnt = vg.getChildCount();
//
//            // Date型変換
//            try {
//                String dateStr = txtIntervalTime.getText().toString();
//                String endtimeStr = txtEndTime.getText().toString();
//                String startTimeStr = txtStartTime.getText().toString();
//
//
//                if (trcnt == 0) {
//                    Toast.makeText(this, "送信に失敗しました", Toast.LENGTH_SHORT).show();
//                }
//
//
//                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
//                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
//                // Date型変換
//                Date formatDate = sdf2.parse(dateStr);
//
//                for (int j = 0; j < trcnt; j++) {
//                    final TableRow tr = (TableRow) vg.getChildAt(j);
//
//                    //---create the BroadcastReceiver when the SMS is sent---
//
//                    smsSentReceiver = new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context arg0, Intent arg1) {
//                            switch (getResultCode()) {
//                                case Activity.RESULT_OK:
//                                    ((TextView) (tr.getChildAt(2))).setText("送信成功");
//                                    break;
//                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                                    ((TextView) (tr.getChildAt(2))).setText("送信失敗");
//                                    ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
//                                    break;
//                                case SmsManager.RESULT_ERROR_NO_SERVICE:
//                                    ((TextView) (tr.getChildAt(2))).setText("配達失敗");
//                                    ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
//                                    break;
//                                case SmsManager.RESULT_ERROR_NULL_PDU:
//                                    ((TextView) (tr.getChildAt(2))).setText("配達失敗");
//                                    ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
//                                    break;
//                                case SmsManager.RESULT_ERROR_RADIO_OFF:
//                                    ((TextView) (tr.getChildAt(2))).setText("配達失敗");
//                                    ((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
//                                    break;
//                            }
//                        }
//                    };
//
//                    //---ddddregister the two BroadcastReceivers---
//                    registerReceiver(smsSentReceiver, new IntentFilter(SENT));
//                    registerReceiver(smsDeliverdReceiver, new IntentFilter(DELIVERED));
//
//
//                    String destinationAddress = "+819057324171";
//                    smsManager.sendTextMessage(
//                            destinationAddress,
//                            null, txt, sentPI, deliveredPI);
//                    Date date = new Date();
//
//                    //開始時間チェック
//                    Date starttime = sdf2.parse(startTimeStr.toString());
//                    Calendar cal2 = Calendar.getInstance();
//                    Date nowtime = cal2.getTime();
//                    String nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));
//                    Date nowtimestr2 = sdf2.parse(nowtimestr);
//
//                    int diff = nowtimestr2.compareTo(starttime);
//                    if (diff < 0) {
//                        ((TextView) (tr.getChildAt(1))).setText("WAIT");
//                    } else {
//                        ((TextView) (tr.getChildAt(1))).setText(sdf1.format(date));
//                    }
//
//                    //終了時間チェック
//                    Calendar cal = Calendar.getInstance();
//                    cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
//                    cal.add(Calendar.MINUTE, formatDate.getMinutes());
//
//                    Date endtime = sdf2.parse(endtimeStr.toString());
//
//                    Date nexttime = cal.getTime();
//                    String nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));
//                    Date nexttimestr2 = sdf2.parse(nexttimestr);
//
//                    int diff2 = nexttimestr2.compareTo(endtime);
//                    if (diff2 < 0) {
//                        int diff3 = nowtimestr2.compareTo(nexttimestr2);
//                        if (diff3 > 0) {
//                            ((TextView) (tr.getChildAt(3))).setText("END");
//                        } else {
//                            ((TextView) (tr.getChildAt(3))).setText(sdf1.format(cal.getTime()));
//                        }
//                    } else {
//                        ((TextView) (tr.getChildAt(3))).setText("END");
//                    }
//
//                }
//            } catch (ParseException e) {
//                Toast.makeText(SecondActivity.this, "例外エラー発生", Toast.LENGTH_SHORT).show();
//            }
//        } catch (InterruptedException e) {
//            Toast.makeText(SecondActivity.this, "例外エラー発生", Toast.LENGTH_SHORT).show();
//        }



        Thread t=new Thread(){
            public void run() {
                    try {
                        //10秒間スレッドをスリープ
                        Thread.sleep(10 * 1000);
                        //自分自身を止めてonDestroy()メソッドへ

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                stopSelf();
            }
        };
        t.start();
    }

    //終了時「onDestroy」メソッド
    public void onDestroy(){
        Toast.makeText(this,"サービスを終了しました！",Toast.LENGTH_LONG).show();
    }
}