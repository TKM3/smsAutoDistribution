package com.example.takamasa.smsautodistribution;

/**
 * Created by takamasa on 2016/02/02.
 */
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileSelectionDialog
        implements OnItemClickListener
{
    private Context					m_parent;				// 呼び出し元
    private OnFileSelectListener	m_listener;			// 結果受取先
    private AlertDialog				m_dlg;					// ダイアログ
    private FileInfoArrayAdapter	m_fileinfoarrayadapter; // ファイル情報配列アダプタ

    // コンストラクタ
    public FileSelectionDialog( Context context,
                                OnFileSelectListener listener )
    {
        m_parent = context;
        m_listener = (OnFileSelectListener)listener;
    }

    // ダイアログの作成と表示
    public void show( File fileDirectory )
    {
        // タイトル
        String strTitle = fileDirectory.getAbsolutePath();

        // リストビュー
        ListView listview = new ListView( m_parent );
        listview.setScrollingCacheEnabled( false );
        listview.setOnItemClickListener( this );
        // ファイルリスト
        File[] aFile = fileDirectory.listFiles();
        List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
        if( null != aFile )
        {
            for( File fileTemp : aFile )
            {
                listFileInfo.add( new FileInfo( fileTemp.getName(), fileTemp ) );
            }
            Collections.sort( listFileInfo );
        }
        // 親フォルダに戻るパスの追加
        if( null != fileDirectory.getParent() )
        {
            listFileInfo.add( 0, new FileInfo( "..", new File( fileDirectory.getParent() ) ) );
        }
        m_fileinfoarrayadapter = new FileInfoArrayAdapter( m_parent, listFileInfo );
        listview.setAdapter( m_fileinfoarrayadapter );

        Builder builder = new Builder( m_parent );
        builder.setTitle( strTitle );
        builder.setPositiveButton( "Cancel", null );
        builder.setView( listview );
        m_dlg = builder.show();
    }

    // ListView内の項目をクリックしたときの処理
    public void onItemClick(	AdapterView<?> l,
                                View v,
                                int position,
                                long id )
    {
        if( null != m_dlg )
        {
            m_dlg.dismiss();
            m_dlg = null;
        }

        FileInfo fileinfo = m_fileinfoarrayadapter.getItem( position );

        if( true == fileinfo.getFile().isDirectory() )
        {
            show( fileinfo.getFile() );
        }
        else
        {
            // ファイルが選ばれた：リスナーのハンドラを呼び出す
            m_listener.onFileSelect( fileinfo.getFile() );
        }
    }

    // 選択したファイルの情報を取り出すためのリスナーインターフェース
    public interface OnFileSelectListener
    {
        // ファイルが選択されたときに呼び出される関数
        public void onFileSelect(File file);
    }
}
