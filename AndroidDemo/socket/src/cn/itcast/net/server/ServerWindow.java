package cn.itcast.net.server;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ServerWindow extends Frame{
	private FileServer s = new FileServer(7878);
	private Label label;
	
	public ServerWindow(String title){
		super(title);
		label = new Label();
		add(label, BorderLayout.PAGE_START);
		label.setText("服务器已经启动");
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				new Thread(new Runnable() {			
					@Override
					public void run() {
						try {
							s.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				 s.quit();
				 System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerWindow window = new ServerWindow("文件上传服务端"); 
		window.setSize(300, 300); 
		window.setVisible(true);
	}

}
