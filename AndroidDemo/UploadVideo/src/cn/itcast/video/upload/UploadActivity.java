package cn.itcast.video.upload;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import cn.itcast.service.UploadLogService;
import cn.itcast.utils.StreamTool;

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

public class UploadActivity extends Activity {
    private EditText filenameText;
    private TextView resulView;
    private ProgressBar uploadbar;
    private UploadLogService logService;
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int length = msg.getData().getInt("size");
			uploadbar.setProgress(length);
			float num = (float)uploadbar.getProgress()/(float)uploadbar.getMax();
			int result = (int)(num * 100);
			resulView.setText(result+ "%");
			if(uploadbar.getProgress()==uploadbar.getMax()){
				Toast.makeText(UploadActivity.this, R.string.success, 1).show();
			}
		}
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        logService = new UploadLogService(this);
        filenameText = (EditText)this.findViewById(R.id.filename);
        uploadbar = (ProgressBar) this.findViewById(R.id.uploadbar);
        resulView = (TextView)this.findViewById(R.id.result);
        Button button =(Button)this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String filename = filenameText.getText().toString();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File uploadFile = new File(Environment.getExternalStorageDirectory(), filename);
					if(uploadFile.exists()){
						uploadFile(uploadFile);
					}else{
						Toast.makeText(UploadActivity.this, R.string.filenotexsit, 1).show();
					}
				}else{
					Toast.makeText(UploadActivity.this, R.string.sdcarderror, 1).show();
				}
			}
		});
    }
    /**
     * �ϴ��ļ�
     * @param uploadFile
     */
	private void uploadFile(final File uploadFile) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					uploadbar.setMax((int)uploadFile.length());
					String souceid = logService.getBindId(uploadFile);
					String head = "Content-Length="+ uploadFile.length() + ";filename="+ uploadFile.getName() + ";sourceid="+
						(souceid==null? "" : souceid)+"\r\n";
					Socket socket = new Socket("192.168.1.100", 7878);
					OutputStream outStream = socket.getOutputStream();
					outStream.write(head.getBytes());
					
					PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());	
					String response = StreamTool.readLine(inStream);
			        String[] items = response.split(";");
					String responseid = items[0].substring(items[0].indexOf("=")+1);
					String position = items[1].substring(items[1].indexOf("=")+1);
					if(souceid==null){//����ԭ��û���ϴ������ļ��������ݿ����һ���󶨼�¼
						logService.save(responseid, uploadFile);
					}
					RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");
					fileOutStream.seek(Integer.valueOf(position));
					byte[] buffer = new byte[1024];
					int len = -1;
					int length = Integer.valueOf(position);
					while( (len = fileOutStream.read(buffer)) != -1){
						outStream.write(buffer, 0, len);
						length += len;
						Message msg = new Message();
						msg.getData().putInt("size", length);
						handler.sendMessage(msg);
					}
					fileOutStream.close();
					outStream.close();
		            inStream.close();
		            socket.close();
		            if(length==uploadFile.length()) logService.delete(uploadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}