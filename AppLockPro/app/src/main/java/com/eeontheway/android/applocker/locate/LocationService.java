package com.eeontheway.android.applocker.locate;

import android.content.Context;
import android.location.Location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.main.SettingsManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 后台定位服务
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class LocationService {
    private static LocationService instance;
    private static Object objLock = new Object();
    private Context context;
	private LocationClient client;
	private LocationClientOption mOption;
	private LocationClientOption diyOption;
    private MyLocationListener bdListener;
    private List<PositionChangeListener> positionListenerList = new ArrayList<>();
    private GeoCoder geocoderSearch;
    private LinkedList<Position> locationList = new LinkedList<>();
    private SettingsManager settingsManager;

    private Position currentPosition;
    private Position lastPosition;
    private String city;
    private int lastErrorCode = -1;
    private int maxDistance = 10;

    /***
     * 构造函数
     */
    private LocationService(Context context) {
        this.context = context;


        client = new LocationClient(context);
        bdListener = new MyLocationListener();
        settingsManager = SettingsManager.getInstance(context);
        client.setLocOption(getDefaultLocationClientOption());
        initGeocoderSearch();
    }

    /**
     * 获取实例，单例化
     * @param context 上下文
     * @return
     */

    public static LocationService getInstance(Context context) {
        synchronized (objLock) {
            if (instance == null) {
                instance = new LocationService(context);
            }
        }

        return instance;
    }

    /**
     * 添加位置监听器
     * @param positionListener 位置监听器
     */
    public void addPositionListener(PositionChangeListener positionListener) {
        positionListenerList.add(positionListener);
    }

    public void removePositionListener (PositionChangeListener positionListener) {
        positionListenerList.remove(positionListener);
    }

	/**
	 * 获取上一次定位的地址
	 * @return 上一次位置，如果之前没有定位，返回null
     */
	public Position getLastPosition() {
        if (currentPosition != null) {
            return currentPosition.clone();
        } else {
            return null;
		}
	}

    /**
     * 获取上一次的结果
     * @return 结果码，非0表示定位失败
     */
    public int getLastErrorCode() {
        return lastErrorCode;
    }

    /***
	 * 设置定位选项
	 * @param option 选项
	 * @return isSuccessSetOption 是否设置成功
	 */
	public boolean setLocationOption(LocationClientOption option){
		boolean isSuccess = false;

        if(option != null){
			if(client.isStarted()) {
                client.stop();
            }

			diyOption = option;
			client.setLocOption(option);
		}
		return isSuccess;
	}

    /**
     * 获取所在城市
     * @return
     */
    public String getCity() {
        return city;
    }

    /**
     * 返回当前的定位设置
     * @return 当前的定位设置
     */
	public LocationClientOption getOption(){
		return diyOption;
	}

    /**
     * 初始化反向地址编码
     */
    private void initGeocoderSearch() {
        geocoderSearch = GeoCoder.newInstance();
        geocoderSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if ((reverseGeoCodeResult == null) ||
                        (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR)) {
                    return;
                }

                // 找出最近的一个地址
                List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();

                double minDistance = 10000000;      // 设置一个很大的地址
                LatLng myLocation = reverseGeoCodeResult.getLocation();
                PoiInfo nearestPoi = null;
                for (PoiInfo poiInfo : poiList) {
                    float [] result = new float[1];
                    Location.distanceBetween(poiInfo.location.latitude, poiInfo.location.longitude,
                            myLocation.latitude, myLocation.longitude, result);
                    if (result[0] < minDistance) {
                        nearestPoi = poiInfo;
                        minDistance = result[0];
                    }
                }

                // 保存找到的地址，如果失败，则使用其原始返回的地址
                String address;
                if (nearestPoi != null) {
                    address = nearestPoi.address + " " + nearestPoi.name;
                } else {
                    address = reverseGeoCodeResult.getAddress();
                }
                currentPosition.setAddress(address);

                // 通知外界，地点发生了变化
                for (PositionChangeListener listener : positionListenerList) {
                    listener.onPositionChanged(lastPosition, currentPosition);
                }
            }
        });
    }

    /***
	 * 获取缺省的定位设置
	 * @return DefaultLocationClientOption
	 */
	public LocationClientOption getDefaultLocationClientOption(){
		if(mOption == null){
			mOption = new LocationClientOption();

            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
			mOption.setLocationMode(LocationMode.Hight_Accuracy);

            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
			mOption.setCoorType("bd09ll");

            //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setScanSpan(settingsManager.getLocateInterval() * 1000);

            //可选，设置是否需要地址信息，默认不需要
		    mOption.setIsNeedAddress(true);

            // 需要周边PIO列表
            mOption.setIsNeedLocationPoiList(false);

            //可选，设置是否需要设备方向结果
		    mOption.setNeedDeviceDirect(false);

            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		    mOption.setLocationNotify(false);

            //可选，默认true，设置是否在stop的时候杀死这个进程，默认不杀死
		    mOption.setIgnoreKillProcess(true);

            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到
		    mOption.setIsNeedLocationDescribe(true);

            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		    mOption.setIsNeedLocationPoiList(true);

            //可选，默认false，设置是否收集CRASH信息，默认收集
		    mOption.SetIgnoreCacheException(false);
		}
		return mOption;
	}

    /**
     * 启动定位
     */
	public void start(){
		synchronized (objLock) {
            if (client != null && !client.isStarted()) {
                client.setLocOption(instance.getDefaultLocationClientOption());
                client.registerLocationListener(bdListener);
				client.requestLocation();
				client.start();
			}
		}
	}

    /**
     * 结束定位
     */
    public void stop(){
		synchronized (objLock) {
			if(client != null && client.isStarted()){
                client.unRegisterLocationListener(bdListener);
				client.stop();
			}
		}
	}

    /**
     * 立即请求一次定位操作
     */
    public void requestLocation() {
        client.requestLocation();
    }

    /**
     * 设置定位的时间间隔，以ms为单位
     *
     * @param interval 时间间隔，如果值为0或-1，则使用缺省值
     */
    public void setLocateInterval(int interval) {
        if (interval <= 0) {
            mOption.setScanSpan(settingsManager.getLocateInterval() * 1000);
        } else {
            mOption.setScanSpan(interval);
        }
        client.setLocOption(mOption);
    }

    /**
     * 显示定位结果
     * @param errorCode 错误码，非0表示有错误发生
     * @param errorMsg 定位结果消息
     */
    private void showLocateResult (int errorCode, String errorMsg) {
        // 状态未改变时，只需显示一次结果
        if (errorCode != lastErrorCode) {
            // 调用回调
            for (PositionChangeListener listener : positionListenerList) {
                listener.onLocateResult(errorCode, errorMsg);
            }
        }
    }

    /**
     * 位置状态监听器
     */
    public interface PositionChangeListener {
        void onLocateResult(int errorCode, String errorMsg);

        void onPositionChanged(Position oldPosition, Position newPosition);
    }

    /**
     * 定位SDK监听器
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            // 检查错误码
            String errorMsg;
            switch (location.getLocType()) {
                case BDLocation.TypeNetWorkLocation:    // 定位成功
                case BDLocation.TypeGpsLocation:
                    errorMsg = context.getString(R.string.locate_ok);
                    showLocateResult(0, errorMsg);
                    lastErrorCode = 0;
                    break;
                default:
                    errorMsg = context.getString(R.string.locate_error);
                    showLocateResult(location.getLocType(), errorMsg);
                    lastErrorCode = location.getLocType();
                    return;
            }

            // 定位成功时的处理
            if (currentPosition == null) {
                // 保存当前位置
                currentPosition = new Position();
                currentPosition.setRadius(location.getRadius());
                currentPosition.setLatitude(location.getLatitude());
                currentPosition.setLongitude(location.getLongitude());
                currentPosition.setAddress(location.getAddrStr());
                currentPosition.setTime(System.currentTimeMillis());
                city = location.getCity();
            } else {
                // 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
                // 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
                // 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理 ╭(●｀∀´●)╯
                Position newPosition = new Position();
                if (locationList.isEmpty() || locationList.size() < 2) {
                    newPosition.setRadius(location.getRadius());
                    newPosition.setLatitude(location.getLatitude());
                    newPosition.setLongitude(location.getLongitude());
                    newPosition.setAddress(location.getAddrStr());
                    newPosition.setTime(System.currentTimeMillis());

                    locationList.add(newPosition);
                } else {
                    // 只保留5个值
                    if (locationList.size() > 5) {
                        locationList.removeFirst();
                    }

                    // 计算某种速度？
                    double score = 0;
                    for (int i = 0; i < locationList.size(); ++i) {
                        LatLng lastPoint = new LatLng(locationList.get(i).getLatitude(),
                                locationList.get(i).getLongitude());
                        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

                        float[] dis = new float[1];
                        Location.distanceBetween(lastPoint.latitude, lastPoint.longitude,
                                curPoint.latitude, curPoint.longitude, dis);
                        double distance = dis[0];
                        double curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).getTime()) / 1000;
                        score += curSpeed * Utils.EARTH_WEIGHT[i];
                    }

                    // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                    if (score > 0.00000999 && score < 0.00005) {
                        location.setLongitude((locationList.get(locationList.size() - 1).getLongitude() + location.getLongitude()) / 2);
                        location.setLatitude((locationList.get(locationList.size() - 1).getLatitude() + location.getLatitude()) / 2);
                    }

                    // 保存起来
                    newPosition.setRadius(location.getRadius());
                    newPosition.setLatitude(location.getLatitude());
                    newPosition.setLongitude(location.getLongitude());
                    newPosition.setAddress(location.getAddrStr());
                    newPosition.setTime(System.currentTimeMillis());
                    locationList.add(newPosition);
                }

                // 计算之前定位的位置与当前定位间的偏差
                float[] distance = new float[1];
                Location.distanceBetween(currentPosition.getLatitude(), currentPosition.getLongitude(),
                                        location.getLatitude(), location.getLongitude(), distance);

                // 如果偏差较大，则说明切换了地点(考虑到定位本身存在偏差)
                if(distance[0] >= maxDistance){
					newPosition.setRadius(location.getRadius());
					newPosition.setLatitude(location.getLatitude());
					newPosition.setLongitude(location.getLongitude());
					newPosition.setAddress(location.getAddrStr());
                    city = location.getCity();

                    currentPosition.update(newPosition);
                } else {
                    return;
                }
            }

            // 反向查找poi地址
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            geocoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        }
    }
}
