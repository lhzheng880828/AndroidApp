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
	 * 从路径中获取文件名称
	 * @param path 下载路径
	 * @return
	 */
	public static String getFilename(String path){
		return path.substring(path.lastIndexOf('/')+1);
	}
	/**
	 * 下载文件
	 * @param path 下载路径
	 * @param threadsize 线程数
	 */
	public void download(String path, int threadsize) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		int filelength = conn.getContentLength();//获取要下载的文件的长度
		String filename = getFilename(path);//从路径中获取文件名称
		File saveFile = new File(filename);
		RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
		accessFile.setLength(filelength);//设置本地文件的长度和下载文件相同
		accessFile.close();
		//计算每条线程下载的数据长度
		int block = filelength%threadsize==0? filelength/threadsize : filelength/threadsize+1;
		for(int threadid=0 ; threadid < threadsize ; threadid++){
			new DownloadThread(url, saveFile, block, threadid).start();
		}
	}
	
	private final class DownloadThread extends Thread{
		private URL url;
		private File saveFile;
		private int block;//每条线程下载的数据长度
		private int threadid;//线程id

		public DownloadThread(URL url, File saveFile, int block, int threadid) {
			this.url = url;
			this.saveFile = saveFile;
			this.block = block;
			this.threadid = threadid;
		}

		@Override
		public void run() {
			//计算开始位置公式：线程id*每条线程下载的数据长度= ？
		    //计算结束位置公式：（线程id +1）*每条线程下载的数据长度-1 =?
			int startposition = threadid * block;
			int endposition = (threadid + 1 ) * block - 1;
			try {
				RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
				accessFile.seek(startposition);//设置从什么位置开始写入数据
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
				System.out.println("线程id:"+ threadid+ "下载完成");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

}
