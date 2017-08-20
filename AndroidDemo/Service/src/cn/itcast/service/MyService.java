package cn.itcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
	private Binder binder = new MyBinder();
	
	public String getName(int id){
		return "´«ÖÇ²¥¿Í";
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	private final class MyBinder extends Binder implements IService{
		public String getName(int id){
			return MyService.this.getName(23);
		}
	}

}
