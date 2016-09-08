package com.eeontheway.android.applocker.main;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.eeontheway.android.applocker.R;
import com.eeontheway.android.applocker.locate.LocationService;
import com.eeontheway.android.applocker.locate.Position;
import com.eeontheway.android.applocker.lock.PositionLockCondition;
import com.eeontheway.android.applocker.ui.MapMakerWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 百度地图的位置选择点
 * 用于在地图上选择一个位置，然后将位置坐标保存起来，用于设置位置锁
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class LocationConditionEditActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final String PARAM_EDIT_MODE = "add_mode";
    private static final String PARAM_POS_CONFIG = "position";

    private PositionLockCondition positionLockCondition;
    private Position position;

    private MapView mapView;
    private View bt_show_my_location;
    private CheckBox iv_layers;
    private AutoCompleteTextView act_search;
    private Button bt_search;
    private ArrayAdapter<String> act_adapter;

    private BaiduMap baiduMap;
    private LocationService locationService;
    private LocationService.PositionChangeListener positionChangeListener;
    private GeoCoder geocoderSearch;
    private PoiSearch mPoiSearch;
    private SuggestionSearch mSuggestionSearch;
    private BitmapDescriptor markerIcon;
    private MapMakerWindow mapMakerWindow;

    private boolean editMode;
    private int maxDistance;

    /**
     * 以编辑模式启动Activity
     * @param fragment
     * @param condition 编辑条件
     * @param requestCode 请求码
     */
    public static void start (Fragment fragment, PositionLockCondition condition, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LocationConditionEditActivity.class);
        intent.putExtra(PARAM_EDIT_MODE, true);
        intent.putExtra(PARAM_POS_CONFIG, condition);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 启动界面，用于创建新项
     * @param fragment 上下文
     */
    public static void start (Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LocationConditionEditActivity.class);
        intent.putExtra(PARAM_EDIT_MODE, false);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取结果位置条件
     * @param intent
     * @return 时间条件
     */
    public static PositionLockCondition getCondition (Intent intent) {
        return (PositionLockCondition)intent.getSerializableExtra(PARAM_POS_CONFIG);
    }

    /**
     * 判断是否是编辑模式
     * @param intent
     * @return true/false
     */
    public static boolean isEditMode (Intent intent) {
        return intent.getBooleanExtra(PARAM_EDIT_MODE, false);
    }

    /**
     * Activity的onCreate回调
     * @param savedInstanceState 之前保存的数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);

        setTitle(R.string.select_time);

        readDefaultMaxDistance();
        initView ();
        initMap();
        initLocation();

        Toast.makeText(this, R.string.select_location, Toast.LENGTH_LONG).show();
    }

    /**
     * Activity的onDestroy回调
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 退出后，恢复监听间隔
        locationService.setLocateInterval(-1);
        locationService.removePositionListener(positionChangeListener);
        mapView.onDestroy();
    }

    /**
     * Activity的onResume回调
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Activity的onPause回调
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 按返回键时的处理
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    /**
     * 按键处理
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            String keyWords = act_search.getText().toString();
            if (!keyWords.isEmpty()) {
                startSearch(keyWords);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 从配置文件中读取缺省的最大距离间隔
     */
    private void readDefaultMaxDistance() {
        SettingsManager manager = SettingsManager.getInstance(this);
        maxDistance = manager.getLocateDefaultDistance();
        manager.freeInstance();
    }

    /**
     * 初始化View
     */
    private void initView () {
        initSearchEdit();

        // 初始化标记窗口
        mapMakerWindow = new MapMakerWindow(this);
        mapMakerWindow.initViews();
        mapMakerWindow.setStyle(MapMakerWindow.STYLE_NORMAL);
        mapMakerWindow.SetUsePostionListener(new MapMakerWindow.OnSetUsePostionListener() {
            @Override
            public void useMarkerPostion() {
                // 退出前，保存radius
                position.setRadius(maxDistance);

                // 结束自己
                Intent intent = getIntent();
                intent.putExtra(PARAM_POS_CONFIG, positionLockCondition);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 配置自我定位按钮
        bt_show_my_location = findViewById(R.id.bt_show_my_location);
        bt_show_my_location.setOnClickListener(this);

        // 获取地图对像
        mapView = (MapView) findViewById(R.id.map_baidu);
        baiduMap = mapView.getMap();

        // 初始化图层选择监听器，进入卫星模式
        iv_layers = (CheckBox) findViewById(R.id.iv_layers);
        iv_layers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    mapMakerWindow.setStyle(MapMakerWindow.STYLE_STATELLITE);
                } else {
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    mapMakerWindow.setStyle(MapMakerWindow.STYLE_NORMAL);
                }

                // 重刷Marker点
                baiduMap.clear();
                if (position != null) {
                    LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
                    showMarker(latLng, position.getAddress());
                }
            }
        });
        iv_layers.setChecked(true);
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_show_my_location:
                Position myPosition = locationService.getLastPosition();
                if (myPosition != null) {
                    showLocation(myPosition);
                }
                break;
        }
    }

    /**
     * 开始搜索关键字
     */
    private void startSearch (String keywords) {
        // 请求查询，以获取查询列表
        SuggestionSearchOption option = new SuggestionSearchOption();
        option.city("广州");
        option.keyword(keywords);
        mSuggestionSearch.requestSuggestion(option);
    }

    /**
     * 初始化搜索编辑框
     */
    private void initSearchEdit() {
        // 配置自动完成输入框
        act_search = (AutoCompleteTextView) findViewById(R.id.act_search);
        act_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        act_search.setAdapter(act_adapter);
        act_search.setThreshold(1);
        act_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    return;
                }

                startSearch(s.toString());
            }
        });

        // 获取搜索建议
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                // 获取所有查询结果
                List<String> suggestList = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.key != null) {
                        suggestList.add(info.key);
                    }
                }

                Log.d("Count", "" + suggestList.size());

                // 添加到适配器中，通知数据发生变化，显示出来
                act_adapter.clear();
                act_adapter.notifyDataSetChanged();
                act_adapter.addAll(suggestList);
                act_adapter.notifyDataSetChanged();
            }
        });

        // 配置搜索按钮
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWords = act_search.getText().toString();
                if (keyWords.isEmpty()) {
                    Toast.makeText(LocationConditionEditActivity.this,
                                            R.string.input_empty, Toast.LENGTH_SHORT).show();
                } else {
                    // 开始搜索
                    PoiCitySearchOption option = new PoiCitySearchOption();
                    String city = locationService.getCity();
                    if (city == null) {
                        city = "北京";
                    }
                    option.city(city);
                    option.keyword(act_search.getText().toString());
                    mPoiSearch.searchInCity(option);
                }
            }
        });
    }

    /**
     * 初始化地图
     */
    private void initMap () {
        // 初始化百度地图
        initBaiduMap();

        // 初始化PIO Search
        initPIOSearch();

        // 初始化反向地址编码
        initGeocoderSearch();
    }

    /**
     * 初始化地图区域
     */
    private void initBaiduMap () {
        // 初始化Marker
        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

        // 初始化地图对像
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // 点击事件，重新选点
                baiduMap.clear();

                // 添加标记
                showMarker(latLng, null);

                // 同时保存选中的位置, 保存当前位置
                position.setLatitude(latLng.latitude);
                position.setLongitude(latLng.longitude);

                // 搜索找到其地址
                geocoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                // 点击事件，重新选点
                baiduMap.clear();

                LatLng pos = mapPoi.getPosition();

                // 添加标记
                showMarker(mapPoi.getPosition(), mapPoi.getName());

                // 同时保存选中的位置, 保存当前位置
                position.setLatitude(pos.latitude);
                position.setLongitude(pos.longitude);
                position.setAddress(mapPoi.getName());
                return true;
            }
        });
    }

    /**
     * 在地图上显示标记和信息窗
     * @param latLng 显示的位置
     */
    private void showMarker (LatLng latLng, String address) {
        // 添加图标及Info窗口
        MarkerOptions option = new MarkerOptions().title("hello").icon(markerIcon).position(latLng);
        baiduMap.addOverlay(option);
        if (address != null) {
            mapMakerWindow.setTitle(address);
            InfoWindow win = new InfoWindow(mapMakerWindow.getView(), latLng, -47);
            baiduMap.showInfoWindow(win);
        }

        // 再添加一个距离圆，表示范围偏差
        int strokeFillColor = getResources().getColor(R.color.map_distance_stroke_fill_color);
        int strokeLineColor = getResources().getColor(R.color.map_distance_stroke_line_color);
        OverlayOptions ooCircle = new CircleOptions().fillColor(strokeFillColor)
                .center(latLng).stroke(new Stroke(5, strokeLineColor))
                .radius(maxDistance);
        baiduMap.addOverlay(ooCircle);
    }

    /**
     * 初始化PIO搜索
     */
    private void initPIOSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if ((poiResult == null) ||
                        (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)) {
                    Toast.makeText(LocationConditionEditActivity.this,
                                        R.string.no_result, Toast.LENGTH_SHORT).show();
                    return;
                } else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    baiduMap.clear();

                    // 添加标记，显示在地图上
                    PoiOverlay overlay = new MyPoiOverlay(baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(LocationConditionEditActivity.this,
                                R.string.no_result, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocationConditionEditActivity.this,
                            poiDetailResult.getName() + ": " + poiDetailResult.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                    Toast.makeText(LocationConditionEditActivity.this,
                            R.string.unknwon_location,Toast.LENGTH_SHORT).show();
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
                position.setAddress(address);

                // 在地图上显示标记
                showMarker(myLocation, address);
            }
        });
    }

    /**
     * 开始定位模式
     * 地图模式定位在当前位置
     */
    private void initLocation () {
        // 配置定位监听
        baiduMap.setMyLocationEnabled(true);
        locationService = ((StartupApplcation)getApplication()).locationService;
        if (locationService.getLastErrorCode() != 0) {
            // 在启动界面的时候也打印一下，用于再次提醒用户
            Toast.makeText(this, R.string.locate_error, Toast.LENGTH_SHORT).show();
        }

        // 配置位置变化监听器
        positionChangeListener = new LocationService.PositionChangeListener() {
            @Override
            public void onPositionChanged(Position oldPosition, Position newPosition) {
                updateMyLocation(newPosition);
            }

            @Override
            public void onLocateResult(int errorCode, String errorMsg) {
            }
        };
        locationService.addPositionListener(positionChangeListener);

        // 打开该页面后，每隔5s立刻刷新
        locationService.setLocateInterval(5000);

        // 读取配置参数，根据模式来加载当前位置
        Intent intent = getIntent();
        editMode = intent.getBooleanExtra(PARAM_EDIT_MODE, false);
        if (editMode) {
            positionLockCondition = (PositionLockCondition) intent.getSerializableExtra(PARAM_POS_CONFIG);
            position = positionLockCondition.getPosition();

            // 添加标记该位置
            LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
            showMarker(latLng, position.getAddress());
        } else {
            // 创建一个缺省对像,用于新建
            positionLockCondition = new PositionLockCondition();
            position = locationService.getLastPosition();
            if (position == null) {
                position = new Position();
            }

            positionLockCondition.setPosition(position);
            positionLockCondition.setEnable(true);
        }

        // 刷新下自己的位置，显示定位
        updateMyLocation(locationService.getLastPosition());
        showLocation(position);
    }

    /**
     * 在地图上更新我的当前位置
     * @param position 当前位置
     */
    private void updateMyLocation (Position position) {
        if (position != null) {
            // 在地图上显示我的位置
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(position.getRadius())
                    .direction(100).latitude(position.getLatitude())
                    .longitude(position.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
        }
    }

    /**
     * 显示指定位置
     * @param position 指定位置
     */
    private void showLocation (Position position) {
        if (position != null) {
            LatLng ll = new LatLng(position.getLatitude(), position.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    /**
     * Marker图层
     */
    private class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);


            // 添加图标及Info窗口
            mapMakerWindow.setTitle(poi.address + " " + poi.name);
            InfoWindow win = new InfoWindow(mapMakerWindow.getView(), poi.location, -47);
            baiduMap.showInfoWindow(win);
            return true;
        }
    }
}
