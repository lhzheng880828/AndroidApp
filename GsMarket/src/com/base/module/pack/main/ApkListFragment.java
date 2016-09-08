package com.base.module.pack.main;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.base.module.pack.R;
import com.base.module.pack.adapter.AppGridviewAdapter;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.AppSoftwareInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.HttpUtils;
import com.base.module.pack.common.ImageLoader;
import com.base.module.pack.common.JsonHelpUtil;
import com.base.module.pack.common.OnDisplayKeyEventListenner;
import com.base.module.pack.common.ThreadManager;
import com.base.module.pack.common.Utils;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.listener.CustomWebViewDownLoadListener;
import com.base.module.pack.method.PackMethod;
import com.base.module.pack.task.TaskPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApkListFragment extends Fragment implements OnItemClickListener{

    private final static int LOAD_APPLIST_FINISH = 1989;

    public static String MARKET_SERVERURL="http://market.ipvideotalk.com:9023/marketinterface/";
    // http://market.ipvideotalk.com:9023/marketinterface/showDetail?sysid=
    //public static String MARKET_SERVERURL="http://192.168.127.110/marketinterface";
    public static String REQUEST_LIST_ACTION = Utils.getDeviceType().getRequest_List_Action();
    public static String REQUEST_DETIAL_ACTION = MARKET_SERVERURL+"showDetail?sysid=";
    public static final String CLICKURLITENTEXTRANAME = "clickurl";
    public static ProgressDialog mProgressDialog;

    private String mWebUrl;
    private View mView;

    // private EditText mSearchInputView;
    // private ImageView mSearchBtn;
    private GridView mGridView;
    private TextView mEmptyView;
    private AppGridviewAdapter mApplistAdapter;
    private CustomWebViewDownLoadListener mDownListener;
    private static boolean isFristLoad = true;
    private static boolean isShowToast = true;
    private Toast mToast;
    private Context mContext;
    private ImageLoader mImageLoader;
    public boolean isSearch = false;
    private int mLeftFocusId;

    private void showToast(int resId){
        mToast.setText(resId);
        mToast.show();
    }

    public ApkListFragment(){
        isFristLoad = true;
    }
    
    public static ApkListFragment instance(String tag, String webUrl){
        ApkListFragment fragment = new ApkListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putString("weburl", webUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebUrl = getArguments().getString("weburl");

        if(TextUtils.isEmpty(mWebUrl)){
            mWebUrl = MARKET_SERVERURL+REQUEST_LIST_ACTION+"&apptypecode=hot";
        }
        mContext = getActivity();
        if(mPackDao==null){
            mPackDao = PackDao.getInstance(mContext);
        }
        mImageLoader=new ImageLoader(mContext);
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        mDownListener = new CustomWebViewDownLoadListener(getActivity());
        IntentFilter filter = new IntentFilter(TaskPool.DOWNLOAD_TASK_CHANGE_ACTION);
        filter.addAction(PackMethod.REFRESH_ACTION);

        //  filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        //  filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mContext.registerReceiver(mBroadcastReceiver, filter);
        mApplistAdapter = new AppGridviewAdapter(mContext,mDownListener,mImageLoader);
        freshAdapterForThis();
        initApplistData(mWebUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.applist, null);
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mEmptyView =(TextView)  mView.findViewById(R.id.empty);
        mGridView.setOnItemClickListener(this);
        // mGridView.setOnItemSelectedListener(this);
        mGridView.setAdapter(mApplistAdapter);
        mGridView.setNextFocusLeftId(mLeftFocusId);
        return mView;
    }
    public void search(String searchKey){
        isSearch = true;
        if(!TextUtils.isEmpty(searchKey)){
            initApplistData(getUrl(searchKey));
        }
    }

    /**
     * view adapter
     */
    PackDaoInter mPackDao;
    private Map<String, Pack> mPacks = new HashMap<String, Pack>();
    private void freshAdapterForThis() {
        new Thread(){
            @Override
            public void run() {
                try {
                    if (mPackDao!=null) {
                        if(!mPackDao.isOpen()){
                            mPackDao.open();
                        }
                        mPacks = mPackDao.findDownPackMaps();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    mHandler.sendEmptyMessage(1);
                }
            }

        }.start();

    }
    private void initGridViewNextFocusId(String apptypecode){
        if(AppActivity.HOT.equals(apptypecode)){
            mLeftFocusId = 100;
        }else  if(AppActivity.APP.equals(apptypecode)){
            mLeftFocusId = 101;
        }else  if(AppActivity.GAME.equals(apptypecode)){
            mLeftFocusId = 102;
        }else  if(AppActivity.TOOLS.equals(apptypecode)){
            mLeftFocusId = 103;
        }
    }

    private List<AppSoftwareInfo> mApplist = new ArrayList<AppSoftwareInfo>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
            case LOAD_APPLIST_FINISH:{
                dimissProgressDialog();
                List<AppSoftwareInfo> applist = (List<AppSoftwareInfo>) msg.obj;
                if(mApplistAdapter != null){
                    mApplistAdapter.notifyDataSetChanged(applist);
                }
                if(!isSearch){
                    mApplist.clear();
                    if(applist!=null){
                        mApplist.addAll(applist);
                    }
                }
                mGridView.setEmptyView(mEmptyView);
                if(msg.arg1 == 0){//&& isShowToast
                    showToast(R.string.getapplistfail);
                    //isShowToast = false;
                }
                break;
            }
            case 1:{
                if(mApplistAdapter != null){
                    mApplistAdapter.setDataPackSetChanged(mPacks);
                }
                break;
            }
            }
        }
    };

    public void refershGridVIew(){
        if(mApplistAdapter != null && isSearch){
            mApplistAdapter.notifyDataSetChanged(mApplist);
            if(mPacks!=null){
                mApplistAdapter.setDataPackSetChanged(mPacks);
            }
            isSearch = false;
        }

    }
    public void initApplistData(final String url){
       // if(url.contains(AppActivity.HOT) && isFristLoad){
            //isShowToast = true;
            showProgressDialog(R.string.loading);
       // }
        ThreadManager.execute(new Runnable() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = LOAD_APPLIST_FINISH;
                msg.arg1 = 1;
                List<AppSoftwareInfo> result = null;
                try {
                    //  String jsonstr = "[{\"description\":\"AnyShare\",\"status\":\"1\",\"searchWord\":null,\"iconurl\":\"http://192.168.120.241:9023/filestorage//resources/software/AnyShare/res/drawable-mdpi/ic_launcher.png\",\"appname\":\"AnyShare\",\"gradenum\":2.5,\"appcode\":\"AnyShare\",\"sysid\":160,\"biz_gradeInfo\":null,\"apptypecode\":\"app\",\"developerid\":1,\"downloadCount\":0,\"ischarge\":2,\"createtime\":\"2013-10-21 10:14:46\",\"creator\":\"osadmin\",\"updatetime\":null,\"updator\":null,\"thumbnailurl\":\"\",\"biz_developerInfo\":null,\"biz_appType\":null,\"ApkInfo\":null,\"biz_appDownloadInfo\":null}]";
                    String json = JsonHelpUtil.doDownloadFromHttpServer(url);
                    if("error".equals(json)){
                        msg.arg1 = 0;
                    }else{
                        result = JsonHelpUtil.JsonToJavaObj(json);//JsonToJavaObj(new URL(url), AppSoftwareInfo.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.arg1 = 0;
                }finally{
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private String getUrl(String searchWord){
        if(TextUtils.isEmpty(searchWord)){
            return mWebUrl;
        }else{

            try {
                return mWebUrl + "&searchWord=" + URLEncoder.encode(searchWord ,"utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return mWebUrl + "&searchWord=" +searchWord ;
        }
    }

    private void dimissProgressDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void showProgressDialog(int messageid){
        dimissProgressDialog();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(getString(messageid));
        mProgressDialog.show();
        isFristLoad = false;
    }

    @Override
    public void onDestroy() {
        dimissProgressDialog();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();    
    }

    private BroadcastReceiver mBroadcastReceiver  =new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mApplistAdapter != null){
                freshAdapterForThis();
                /*if(PackMethod.INSTALL_ACTION.equals(intent.getAction())){
                    mApplistAdapter.setInstallPack(intent.getStringExtra("packageName"));
                }*/
                mApplistAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext,AppDetialActivity.class);
        intent.putExtra("softwareinfo", mApplistAdapter.getItem(position));
        //intent.putExtra(CLICKURLITENTEXTRANAME,REQUEST_DETIAL_ACTION+mApplistAdapter.getItem(position).getSysid());
        startActivity(intent);
    }
}