package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by takamasa on 2016/03/20.
 */
public class AsyncCSVread extends AsyncTask<Integer, Integer, Integer> {

    Activity uiActivity;

    final String TAG = "AsyncCSVread";
    ProgressDialog progressDialog;

    /**
     *コンストラクタ
     */
    public AsyncCSVread(Activity ac) {
        super();
        this.uiActivity=ac;
    }

    /**
    *事前準備の処理を行うメソッド
    */
    @Override
    protected void onPreExecute() {

        progressDialog=new ProgressDialog(uiActivity);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("CSV読込み中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    /**
     *バックグラウンドで行う処理
     */
    @Override
    protected Integer doInBackground(Integer...params){
        Log.d(TAG, "doInBackground");

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
        return i;
    }


    /**
     *進捗状況をUIスレッドで表示する
    */
    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result){
        Log.d(TAG, "onPostExecute");

        for (i = 0; i < result; i++) {
            tr = (TableRow) vg.getChildAt(j);
            ((TextView) (tr.getChildAt(0))).setText(rowStrList[i][0]);
        }
        Log.d(TAG, "progressDialog.dismiss");
        progressDialog.dismiss();
    }
}
