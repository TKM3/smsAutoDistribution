package com.example.takamasa.smsautodistribution;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by takamasa on 2016/03/20.
 */
public class AsyncCSVread extends AsyncTask<Integer, Integer, Integer> {

    Activity uiActivity;
    TextView txtInfo;
    CheckBox cbxSim;
    ViewGroup vg;
    TableRow tr;
    TableLayout table;

    final String TAG = "AsyncCSVread";
    ProgressDialog progressDialog;
    String text = "";
    final String FILE_ADDRESS="address.csv";
    final String FILE_INFO="info.txt";
    int i;
    String empty_str = "-";
    ArrayList<String> strTable;
    BufferedReader reader;


    /**
     *コンストラクタ
     */
    public AsyncCSVread(Activity ac,TextView txtInfo,CheckBox cbxSim,ViewGroup vg,TableLayout table) {
        super();
        this.uiActivity=ac;
        this.txtInfo=txtInfo;
        this.cbxSim=cbxSim;
        this.table=table;
        this.vg=vg;
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

        strTable = new ArrayList<>();
        String path= Environment.getExternalStorageDirectory().getPath()+"/SmsAutoSendFiles/";

        try {
//assetsフォルダから取出し
//            try {
//                //assetsフォルダ内のinfo.txtをオープンする
//                is = uiActivity.getAssets().open(FILE_INFO);
//                br = new BufferedReader(new InputStreamReader(is));
//                //1行ずつ読込、改行を付加する
//                String str;
//                while ((str = br.readLine()) != null) {
//                    text += str + "\n";
//                }
//
//            } finally {
//                if (is != null) is.close();
//                if (br != null) br.close();
//            }

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                        path + FILE_INFO),"Shift-JIS"));

                BufferedReader bufferReader = new BufferedReader(reader);
                //1行ずつ読込、改行を付加する
                String str;
                while ((str = bufferReader.readLine()) != null) {
                    text += str + "\n";
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                        path + FILE_ADDRESS), "Shift-JIS"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            BufferedReader bufferReader = new BufferedReader(reader);

            i = 0;
            //ファイルの件数分ループして配列に格納します。
            while (bufferReader.ready()) {
                String line = bufferReader.readLine();
                strTable.add(line);
                i++;
                Log.d(TAG, "bufferReader:"+i);
            }
            bufferReader.close();
        } catch (IOException e) {
            Log.d(TAG,"bufferReader:err");
        }
        //処理を終了させる
        if (isCancelled()) {
            Log.d(TAG,"キャンセル処理");
            return null;
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
    protected void onPostExecute(Integer result) {
        Log.d(TAG, "onPostExecute");

        table.removeAllViews();
        Log.d(TAG, "removeAllViews");

        //配列の要素分処理を繰り返し表示する。
        for (int j = 0; j < strTable.size() - 1; j++) {
            String telstr = strTable.get(j);
            //行を追加
            uiActivity.getLayoutInflater().inflate(R.layout.table_row, vg);
            //文字設定
            tr = (TableRow) vg.getChildAt(j);
            if (cbxSim.isChecked()== true) {
                StringBuilder sb = new StringBuilder(telstr);
                ((TextView) (tr.getChildAt(0))).setText(sb.replace(0, 1, "+81"));
            } else {
                ((TextView) (tr.getChildAt(0))).setText(telstr);
            }
            ((TextView) (tr.getChildAt(1))).setText(empty_str);
            ((TextView) (tr.getChildAt(2))).setText(empty_str);
            ((TextView) (tr.getChildAt(3))).setText(empty_str);
            ((TextView) (tr.getChildAt(4))).setText(empty_str);
            Log.d(TAG, "inflate:" + j);
        }

        txtInfo.setText(text);

        Log.d(TAG, "progressDialog.dismiss");
        progressDialog.dismiss();
    }

    /**
     * 中止された際の処理
     */
    @Override
    protected void onCancelled(){
        progressDialog.dismiss();
        progressDialog=null;
        Log.d(TAG, "キャンセル処理が行われました");
    }
}
