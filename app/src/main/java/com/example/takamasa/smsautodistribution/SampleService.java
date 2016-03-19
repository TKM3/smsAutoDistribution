package com.example.takamasa.smsautodistribution;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by takamasa on 2016/03/12.
 */
public class SampleService extends Service {
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    //開始時にトーストを表示
    public void onStart(Intent intent,int startID){
        //開始時にトーストを表示
        Toast.makeText(this,"サービスを開始しました！",Toast.LENGTH_LONG).show();


        Thread t=new Thread(){
            public void run(){
                try {
                    //10秒間スレッドをスリープ
                    Thread.sleep(10 * 1000);
                    //自分自身を止めてonDestroy()メソッドへ
                    stopSelf();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    //終了時「onDestroy」メソッド
    public void onDestroy(){
        Toast.makeText(this,"サービスを終了しました！",Toast.LENGTH_LONG).show();
    }
}
