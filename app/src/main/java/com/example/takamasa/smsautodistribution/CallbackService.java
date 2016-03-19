//package com.example.takamasa.smsautodistribution;
//
//import android.content.Intent;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.RemoteCallbackList;
//import android.os.RemoteException;
//
//import java.util.logging.Handler;
//import java.util.logging.LogRecord;
//
///**
// * Created by takamasa on 2016/03/12.
// */
//public class CallbackService {
////サービスの継続時間設定（秒）
//    private int count=10;
//    //コールバックインターフェイス設定
//    private final RemoteCallbackList<ISampleCallback>list
//            =new RemoteCallbackList<ISampleCallback>();
//
//    //ハンドラ処理
//    public Handler handler=new Handler(){
//        @Override
//        public void close() {
//        }
//        @Override
//        public void flush() {
//        }
//        @Override
//        public void publish(LogRecord record) {
//
//        }
//
//        @Override
//    public void handleMessage(Message msg){
//            if(msg.what==1) {
//                int numListeners = list.beginBroadcast();
//                for (int i = 0; i < numListeners; i++) {
//                    try {
//                        if (count == 0) {
//                            list.getBroadcastItem(i)
//                                    .updateText("サービスが終了しました！");
//                        } else {
//                            list.getBroadcastItem(i)
//                                    .updateText("サービス終了まで" + count + "秒");
//                        }
//                    } catch (RemoteException e) {
//                    }
//                }
//                list.finishBroadcast();
//                //1秒ごとにメッセージを送信
//                handler.sendEmptyMessageDelayed(1, 1 * 100);
//                count--;
//            }else{
//                super.dispatchMessage(msg);
//
//            }
//        }
//    };
//    @Override
//    public void onCreate(){
//        super.onCreate();
//        handler.sendEnptyMessage(1);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent){
//        if()
//    }
//}
