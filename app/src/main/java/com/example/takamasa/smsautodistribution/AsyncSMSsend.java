package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by takamasa on 2016/03/20.
 */
public class AsyncSMSsend extends AsyncTask<Integer, Integer, Integer> {

    ViewGroup vg;
    TableRow tr;
    TextView text;
    TextView txtEndTime;
    TextView txtIntervalTime;
    TextView txtStartTime;
    Activity uiActivity;

    final String TAG = "AsyncTaskActivity";
    PendingIntent sentPI, deliveredPI;
    ProgressDialog progressDialog;
    BroadcastReceiver smsSentReceiver;
    BroadcastReceiver smsDeliverdReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    String txt;
    String dateStr;
    String starttimestr;
    String nowtimestr;
    String nexttimestr;
    String nexttimeIniProgresstr;
    String endtimeStr;

    int trcnt;
    int j;
    int i;
    int diff2;
    int diff3;

    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

    String[][]rowStrList;
    String[]BroadReceiveStr;


    SmsManager smsManager = SmsManager.getDefault();
    Calendar cal = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    Calendar cal3 = Calendar.getInstance();
    Calendar cal4 = Calendar.getInstance();

    Date date = new Date();
    Date nowtime;
    Date nexttime;
    Date nexttimeIniProgress;
    Date starttime;
    Date endtime;
    Date formatDate;
    Date nowtimestr2;
    Date nexttimestr2;

    /**
     *コンストラクタ
     */
    public AsyncSMSsend(Activity ac,ViewGroup vg,TextView text,TextView txtEndTime,TextView txtIntervalTime,TextView txtStartTime) {
        super();
        this.uiActivity=ac;
        this.vg=vg;
        this.text=text;
        this.txtEndTime=txtEndTime;
        this.txtIntervalTime=txtIntervalTime;
        this.txtStartTime=txtStartTime;
    }

    /**
    *事前準備の処理を行うメソッド
    */
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");

        dateStr = txtIntervalTime.getText().toString();
        starttimestr=txtStartTime.getText().toString();

        nowtime = cal2.getTime();
        nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));

        nexttime = cal.getTime();
        nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));
        endtimeStr = txtEndTime.getText().toString();

        try {
            formatDate = sdf2.parse(dateStr);
            starttime = sdf2.parse(starttimestr);
            nowtimestr2 = sdf2.parse(nowtimestr);
            nexttimestr2 = sdf2.parse(nexttimestr);
            endtime = sdf2.parse(endtimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal3.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
        cal3.add(Calendar.MINUTE, formatDate.getMinutes());
        nexttimeIniProgress = cal3.getTime();
        nexttimeIniProgresstr = String.format(String.format("%1$02d", nexttimeIniProgress.getHours()) + ":" + String.format("%1$02d", nexttimeIniProgress.getMinutes()));

        txt = text.getText().toString();
        trcnt = vg.getChildCount();

        rowStrList=new String[trcnt+1][4];
        BroadReceiveStr=new String[trcnt+1];

        for (int i=0;i<trcnt;i++){
            tr = (TableRow) vg.getChildAt(i);
            rowStrList[i][0]=((TextView) (tr.getChildAt(0))).getText().toString();
            rowStrList[i][1]=((TextView) (tr.getChildAt(1))).getText().toString();
            rowStrList[i][2]=((TextView) (tr.getChildAt(2))).getText().toString();
            rowStrList[i][3]=((TextView) (tr.getChildAt(3))).getText().toString();
            Log.d(TAG, rowStrList[i][0]);
            Log.d(TAG, rowStrList[i][1]);
            Log.d(TAG, rowStrList[i][2]);
            Log.d(TAG, rowStrList[i][3]);
        }

        progressDialog=new ProgressDialog(uiActivity);
        progressDialog.setTitle("Please wait");
        if(rowStrList[i][3]==null) {
            progressDialog.setMessage("SMS自動送信中(NEXT➡︎" + nexttimeIniProgresstr.substring(0, 5) + ")...");
        }

        //メッセージが成功したかどうかをチェックする
        sentPI = PendingIntent.getBroadcast(uiActivity, 0, new Intent(SENT), 0);
        //メッセージの配達が成功したかどうかをチェックする
        deliveredPI = PendingIntent.getBroadcast(uiActivity, 0, new Intent(DELIVERED), 0);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     *バックグラウンドで行う処理
     */
    @Override
    protected Integer doInBackground(Integer...params){
        Log.d(TAG, "doInBackground");

        progressDialog.setMax(params[0]);
        j=0;

        for (i = 0; i < trcnt; i++) {
            if (exeSmsSend(0)==true){
                break;
            }
        }

//        for (j=0;j<params[0];j++) {
//            for (i = 0; i < trcnt; i++) {
//                if (exeSmsSend(1)==true){
//                    break;
//                }
//            }
//            try {
//                Log.d(TAG, "Thread.sleep");
//                Thread.sleep(30 * 1000);
//            } catch (Exception e) {
//                Log.d(TAG, "err(send):" + i);
//            }
//            publishProgress(j);
//            //次回送信時間定義
//            cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
//            cal.add(Calendar.MINUTE, formatDate.getMinutes());
//            Log.d(TAG, "Addtime" + i + ":" + cal.getTime().toString());
//        }
        Log.d(TAG, "sendALLcount:" + j);
        return i;
    }

    public boolean exeSmsSend(int ini) {
        //次回送信時間定義
        if(ini==1) {
            cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
            cal.add(Calendar.MINUTE, formatDate.getMinutes());
        }
        nexttime = cal.getTime();
        nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));


        diff3 = endtime.compareTo(nexttimestr2);
        if(ini==1) {
            if (diff3 < 0) {
                Log.d(TAG, "END" + i);
                return true;
            }
        }

        cal3 = Calendar.getInstance();
        nowtime = cal3.getTime();
        nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));

        diff3 = nowtimestr2.compareTo(starttime);
        if(ini==1) {
            while (diff3 < 0) {
                Log.d(TAG, "STARTwait");
                cal4 = Calendar.getInstance();
                nowtime = cal4.getTime();
                nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));
                try {
                    nowtimestr2 = sdf2.parse(nowtimestr);
                } catch (Exception e) {
                    Log.d(TAG, "err(STARTwait)");
                }

                diff2 = nowtimestr2.compareTo(starttime);
                if (diff2 < 0) {
                    Log.d(TAG, "WAIT:" + i + ":" + nowtimestr2.toString());
                    try {
                        Thread.sleep(180 * 1000);
                    } catch (Exception e) {
                        Log.d(TAG, "err(WAIT):" + i);
                    }
                } else {
                    break;
                }
            }
        }

        try {
            nexttimestr2 = sdf2.parse(nexttimestr);
        } catch (Exception e) {
        }

        diff2 = nowtimestr2.compareTo(nexttimestr2);
        if(ini==1) {
            while (diff2 < 0) {
                Log.d(TAG, "NEXTwait(next):" + nexttimestr2 + ":" + i);
                Log.d(TAG, "NEXTwait(now):" + nowtimestr2 + ":" + i);

                try {
                    Thread.sleep(180 * 1000);
                } catch (Exception e) {
                    Log.d(TAG, "err(NEXT):" + i);
                }

                Calendar cal3 = Calendar.getInstance();
                nowtime = cal3.getTime();
                nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));
                try {
                    nowtimestr2 = sdf2.parse(nowtimestr);
                    diff2 = nowtimestr2.compareTo(nexttimestr2);
                    rowStrList[i][1] = nowtimestr;
                    nexttimeIniProgresstr = nexttimestr;
                    rowStrList[i][3] = nexttimestr;
                } catch (Exception e) {
                    Log.d(TAG, "err(NEXTwait)" + nowtimestr2 + ":" + i);
                }
            }
        }else {
            cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
            cal.add(Calendar.MINUTE, formatDate.getMinutes());
            nexttime = cal.getTime();
            nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));

            try {
                nowtimestr2 = sdf2.parse(nowtimestr);
                diff2 = nowtimestr2.compareTo(nexttimestr2);
                rowStrList[i][1] = nowtimestr;
                nexttimeIniProgresstr = nexttimestr;
                rowStrList[i][3] = nexttimestr;
            } catch (Exception e) {
                Log.d(TAG, "err(NEXTwait)" + nowtimestr2 + ":" + i);
            }
        }

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        BroadReceiveStr[i] = "送信成功";
                        Log.d(TAG, "Sent送信成功:" + i);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        BroadReceiveStr[i] = "送信失敗";
                        Log.d(TAG, "Sent送信失敗:" + i);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        BroadReceiveStr[i] = "配達失敗";
                        Log.d(TAG, "Sent配達失敗:" + i);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        BroadReceiveStr[i] = "配達失敗";
                        Log.d(TAG, "Sent配達失敗:" + i);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        BroadReceiveStr[i] = "配達失敗";
                        Log.d(TAG, "Sent配達失敗:" + i);
                        break;
                }
            }
        };

        //---create the BroadcastReceiver when the SMS is delivered---
        smsDeliverdReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        BroadReceiveStr[i] = "送信成功";
                        Log.d(TAG, "Deliverd送信成功:" + i);
                        break;
                    case Activity.RESULT_CANCELED:
                        BroadReceiveStr[i] = "配達失敗";
                        Log.d(TAG, "Deliverd配達失敗:" + i);
                        break;
                }
            }
        };

        //---ddddregister the two BroadcastReceivers---
        uiActivity.registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        uiActivity.registerReceiver(smsDeliverdReceiver, new IntentFilter(DELIVERED));

        try {
            nowtimestr2 = sdf2.parse(nowtimestr);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        Log.d(TAG, "send:" + i + ":" + rowStrList[i][1] + ":" + rowStrList[i][0]);
        String destinationAddress = rowStrList[i][0];
        smsManager.sendTextMessage(
                destinationAddress,
                null, txt, sentPI, deliveredPI);

        return false;
    }

    /**
     *進捗状況をUIスレッドで表示する
    */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate");
        progressDialog.incrementProgressBy(progress[0]);
        progressDialog.setMessage("SMS自動送信中(NEXT➡︎" + nexttimeIniProgresstr + ")...");
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result){
        Log.d(TAG, "onPostExecute");

        for (i = 0; i < result; i++) {
            tr = (TableRow) vg.getChildAt(i);
            //((TextView) (tr.getChildAt(0))).setText(rowStrList[i][0]);
            ((TextView) (tr.getChildAt(1))).setText(rowStrList[i][1]);
            ((TextView) (tr.getChildAt(2))).setText(BroadReceiveStr[i]);
            ((TextView) (tr.getChildAt(3))).setText(rowStrList[i][3]);
        }

        Log.d(TAG, "progressDialog.dismiss");
        progressDialog.dismiss();
    }

    /**
     * 中止された際の処理
     */
    @Override
    protected void onCancelled(){
        progressDialog.dismiss();
    }
}
