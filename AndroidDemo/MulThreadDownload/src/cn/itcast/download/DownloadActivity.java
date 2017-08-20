package cn.itcast.download;

import java.io.File;

import cn.itcast.net.download.DownloadProgressListener;
import cn.itcast.net.download.FileDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends Activity {
    private EditText downloadpathText;
    private TextView resultView;
    private ProgressBar progressBar;
    //��Handler��������������������ĵ�ǰ�̵߳���Ϣ���У�������������Ϣ���з�����Ϣ
    //��Ϣ�����е���Ϣ�ɵ�ǰ�߳��ڲ����д���
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {			
			switch (msg.what) {
			case 1:				
				progressBar.setProgress(msg.getData().getInt("size"));
				float num = (float)progressBar.getProgress()/(float)progressBar.getMax();
				int result = (int)(num*100);
				resultView.setText(result+ "%");
				if(progressBar.getProgress()==progressBar.getMax()){
					Toast.makeText(DownloadActivity.this, R.string.success, 1).show();
				}
				break;

			case -1:
				Toast.makeText(DownloadActivity.this, R.string.error, 1).show();
				break;
			}
		}
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        downloadpathText = (EditText) this.findViewById(R.id.downloadpath);
        progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
        resultView = (TextView) this.findViewById(R.id.result);
        Button button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String path = downloadpathText.getText().toString();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					download(path, Environment.getExternalStorageDirectory());
				}else{
					Toast.makeText(DownloadActivity.this, R.string.sdcarderror, 1).show();
				}
				
			}
		});
    }
    //���߳�(UI�߳�)
    //ҵ���߼���ȷ�����Ǹó������е�ʱ��������
    //������ʾ�ؼ��Ľ������ֻ����UI�̸߳���������ڷ�UI�̸߳��¿ؼ�������ֵ�����º����ʾ���治�ᷴӳ����Ļ��
    private void download(final String path, final File savedir) {
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				FileDownloader loader = new FileDownloader(DownloadActivity.this, path, savedir, 3);
		    	progressBar.setMax(loader.getFileSize());//���ý����������̶�Ϊ�ļ��ĳ���
				try {
					loader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {//ʵʱ��֪�ļ��Ѿ����ص����ݳ���
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);
							handler.sendMessage(msg);//������Ϣ
						}
					});
				} catch (Exception e) {
					handler.obtainMessage(-1).sendToTarget();
				}
			}
		}).start();
	}
}