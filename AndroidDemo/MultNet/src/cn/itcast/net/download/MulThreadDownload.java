package cn.itcast.net.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MulThreadDownload {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "http://net.itcast.cn/QQWubiSetup.exe";
		try {
			new MulThreadDownload().download(path, 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��·���л�ȡ�ļ�����
	 * @param path ����·��
	 * @return
	 */
	public static String getFilename(String path){
		return path.substring(path.lastIndexOf('/')+1);
	}
	/**
	 * �����ļ�
	 * @param path ����·��
	 * @param threadsize �߳���
	 */
	public void download(String path, int threadsize) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		int filelength = conn.getContentLength();//��ȡҪ���ص��ļ��ĳ���
		String filename = getFilename(path);//��·���л�ȡ�ļ�����
		File saveFile = new File(filename);
		RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
		accessFile.setLength(filelength);//���ñ����ļ��ĳ��Ⱥ������ļ���ͬ
		accessFile.close();
		//����ÿ���߳����ص����ݳ���
		int block = filelength%threadsize==0? filelength/threadsize : filelength/threadsize+1;
		for(int threadid=0 ; threadid < threadsize ; threadid++){
			new DownloadThread(url, saveFile, block, threadid).start();
		}
	}
	
	private final class DownloadThread extends Thread{
		private URL url;
		private File saveFile;
		private int block;//ÿ���߳����ص����ݳ���
		private int threadid;//�߳�id

		public DownloadThread(URL url, File saveFile, int block, int threadid) {
			this.url = url;
			this.saveFile = saveFile;
			this.block = block;
			this.threadid = threadid;
		}

		@Override
		public void run() {
			//���㿪ʼλ�ù�ʽ���߳�id*ÿ���߳����ص����ݳ���= ��
		    //�������λ�ù�ʽ�����߳�id +1��*ÿ���߳����ص����ݳ���-1 =?
			int startposition = threadid * block;
			int endposition = (threadid + 1 ) * block - 1;
			try {
				RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
				accessFile.seek(startposition);//���ô�ʲôλ�ÿ�ʼд������
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestProperty("Range", "bytes="+ startposition+ "-"+ endposition);
				InputStream inStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while( (len=inStream.read(buffer)) != -1 ){
					accessFile.write(buffer, 0, len);
				}
				inStream.close();
				accessFile.close();
				System.out.println("�߳�id:"+ threadid+ "�������");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

}
