package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
public class AsyncSMSsend extends AsyncTask<Integer,Integer,Integer> {

    private ViewGroup vg;
    private TextView text;
    private TextView txtEndTime;
    private TextView txtIntervalTime;
    private TextView txtStartTime;

    final String TAG = "AsyncTaskActivity";
    Context context;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliverdReceiver;

    /*
     *コンストラクタ
     */
    public AsyncSMSsend(ViewGroup vg,TextView text,TextView txtEndTime,TextView txtIntervalTime,TextView txtStartTime) {
        super();
        this.vg=vg;
        this.text=text;
        this.txtEndTime=txtEndTime;
        this.txtIntervalTime=txtIntervalTime;
        this.txtStartTime=txtStartTime;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        Log.d(TAG, "doInBackground-" + params[0]);
        //メッセージが成功したかどうかをチェックする
//        sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        //メッセージの配達が成功したかどうかをチェックする
//        deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        try {
            SMSsend();
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException in doInBackground");
        }
        return null;
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result) {
        text.setText("helloworld");
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
    }

    public void SMSsend(){

        SmsManager smsManager = SmsManager.getDefault();
        ViewGroup vg = this.vg;
        TextView text = this.text;
        TextView txtEndTime = this.txtEndTime;
        TextView txtIntervalTime = this.txtIntervalTime;
        TextView txtStartTime = this.txtStartTime;
        String txt = text.getText().toString();
        int trcnt = vg.getChildCount();

        // Date型変換
        try {
            for (int i=0;i<1;i++) {

                String dateStr = txtIntervalTime.getText().toString();
                String endtimeStr = txtEndTime.getText().toString();
                String startTimeStr = txtStartTime.getText().toString();

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
                                    //((TextView) (tr.getChildAt(2))).setText("送信成功");
                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                    //((TextView) (tr.getChildAt(2))).setText("送信失敗");
                                    //((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    //((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                    //((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    //((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                    //((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    //((TextView) (tr.getChildAt(2))).setText("配達失敗");
                                    //((TextView) (tr.getChildAt(2))).setTextColor(Color.RED);
                                    break;
                            }
                        }
                    };

                    String destinationAddress = ((TextView) (tr.getChildAt(0))).getText().toString();
                    smsManager.sendTextMessage(
                            destinationAddress,
                            null, txt, sentPI, deliveredPI);

                    try {
                        Thread.sleep(10 * 1000);
                    }catch (Exception e){
                    }

                    Date date = new Date();

                    //開始時間チェック
                    Date starttime = sdf2.parse(startTimeStr.toString());
                    Calendar cal2 = Calendar.getInstance();
                    Date nowtime = cal2.getTime();
                    String nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));
                    Date nowtimestr2 = sdf2.parse(nowtimestr);

                    int diff = nowtimestr2.compareTo(starttime);
                    if (diff < 0) {
//                        ((TextView) (tr.getChildAt(1))).setText("WAIT");
                    } else {
//                        ((TextView) (tr.getChildAt(1))).setText(sdf1.format(date));
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
//                            ((TextView) (tr.getChildAt(3))).setText("END");
                        } else {
//                            ((TextView) (tr.getChildAt(3))).setText(sdf1.format(cal.getTime()));
                        }
                    } else {
//                        ((TextView) (tr.getChildAt(3))).setText("END");
                    }
                }
            }
        } catch (ParseException e) {

        }
    }
}
