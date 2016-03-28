package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by takamasa on 2016/03/20.
 */
public class AsyncSMSsend extends AsyncTask<Integer, Integer, Integer> {

    ViewGroup vg;
    TextView text;
    TextView txtEndTime;
    TextView txtIntervalTime;
    TextView txtStartTime;
    Activity uiActivity;

    final String TAG = "AsyncTaskActivity";
    PendingIntent sentPI, deliveredPI;
    ProgressDialog progressDialog;

    String txt;
    String dateStr;
    String nowtimestr;
    String nexttimestr;
    String endtimeStr;
    int trcnt;
    int j;
    int i;
    ArrayList<TableRow> trRow=new ArrayList<TableRow>();
    ArrayList<String>trStr=new ArrayList<String >();
    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

    SmsManager smsManager = SmsManager.getDefault();
    Calendar cal = Calendar.getInstance();

    Date date = new Date();
    Date nowtime;
    Date nexttime;
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

        dateStr = txtIntervalTime.getText().toString();

        //現在時間定義
        Calendar cal2 = Calendar.getInstance();
        nowtime = cal2.getTime();
        nowtimestr = String.format(String.format("%1$02d", nowtime.getHours()) + ":" + String.format("%1$02d", nowtime.getMinutes()));

        for (j = 0; j < trcnt; j++) {
            trRow.add((TableRow) vg.getChildAt(j));
            trStr.add(trRow.get(j).toString());
        }

        nexttime = cal.getTime();
        nexttimestr = String.format(String.format("%1$02d", nexttime.getHours()) + ":" + String.format("%1$02d", nexttime.getMinutes()));
        endtimeStr = txtEndTime.getText().toString();

        try {
            formatDate = sdf2.parse(dateStr);
            nowtimestr2 = sdf2.parse(nowtimestr);
            nexttimestr2 = sdf2.parse(nexttimestr);
            endtime = sdf2.parse(endtimeStr.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //次回送信時間定義
        cal.add(Calendar.HOUR_OF_DAY, formatDate.getHours());
        cal.add(Calendar.MINUTE, formatDate.getMinutes());

        txt = text.getText().toString();
        trcnt = vg.getChildCount();
    }

    /**
    *事前準備の処理を行うメソッド
    */
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");

        progressDialog=new ProgressDialog(uiActivity);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("SMS自動送信中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    /**
     *バックグラウンドで行う処理
     */
    @Override
    protected Integer doInBackground(Integer...params){

        Log.d(TAG, "doInBackground");

        progressDialog.setMax(params[0]);
        // Date型変換
        for (i=0;i<params[0]+1;i++) {
            for (j = 0; j < trcnt; j++) {
                try {
                    int diff3 = nexttimestr2.compareTo(date);
                    while (diff3 < 0) {
                        if (((trStr.get(j))).equals("WAIT")) {
                        } else {
                            if (diff3 > 0) {
                                String destinationAddress = (trStr.get(j));
                                smsManager.sendTextMessage(
                                        destinationAddress,
                                        null, txt, sentPI, deliveredPI);
                            }
                        }
                    }
                }catch (Exception e){
                }
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
            }
            publishProgress(i);
        }
        return params[0];
    }

    /**
     *進捗状況をUIスレッドで表示する
    */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate");
        progressDialog.incrementProgressBy(progress[0]);
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result){
        Log.d(TAG, "onPostExecute");

        for (j = 0; j < trcnt; j++) {
            TableRow tr = (TableRow) vg.getChildAt(j);
            try {
                int diff2 = nexttimestr2.compareTo(endtime);
                ((TextView) (tr.getChildAt(1))).setText(sdf1.format(date));
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
            } catch (Exception e) {
            }
        }
        progressDialog.dismiss();

    }
}
