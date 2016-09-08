/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.base.module.pack.R;
import com.base.module.pack.bean.ApkInfo;
import com.base.module.pack.bean.AppSoftwareInfo;
import com.base.module.pack.bean.Pack;
import com.base.module.pack.common.ImageLoader;
import com.base.module.pack.common.Utils;
import com.base.module.pack.dao.PackDao;
import com.base.module.pack.inter.PackDaoInter;
import com.base.module.pack.listener.CustomWebViewDownLoadListener;
import com.base.module.pack.method.PackMethod;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


public class AppDetialActivity extends Activity implements OnClickListener{
    // private int COMMMENT_LIMIT = 10;
    //"http://market.ipvideotalk.com:9023/marketinterface/getGradeList?appid={app对应主键id}&pageNo=第几页评论&pageSize=每页大小，接口代码文档见附件";
    // private String COMMENT_HTTP= "http://market.ipvideotalk.com:9023/marketinterface/getGradeList?pageSize="+COMMMENT_LIMIT;
    private ImageView mAppImageView,detailDownBtnimage;//moreDescroption
    private TextView appname,appVersion,appSize,downTimes,publishTime;//previous_version
    private TextView description;
    private TextView downButton;
    private RatingBar appRating;//setCommentRating

    /*private EditText commentEdit;
    private TextView lineView1;
    private Button submitButton,loadMoreComment;
    private ListView commentListView;
    private ListViewForScrollView commentListView;
    private GradeInfo commentInfo;
    private boolean isUp = false;
    private ProgressDialog progressDialog;
    private LayoutParams params;
    private float mRating;
    private CommentAdapter mAdapter;*/
    private LinearLayout downLayout;
    private AppSoftwareInfo mSoftwareInfo;
    private CustomWebViewDownLoadListener mDownListener;
    private PackDaoInter mPackDao;
    private Map<String, Pack> mPacks = new HashMap<String, Pack>();
    // private List<GradeInfo> mGradeList = new ArrayList<GradeInfo>();
    private String packageName;
    private boolean isHasNew =false;
    /*private int mAppId;
    private int mCommentPage=1;
    private String PARAMETER = "&appid="+mAppId+"&pageNo="+mCommentPage;*/
    private boolean isExit=false;
    public static int POST_SUCCESS_FLAG = 2;
    public static int POST_FAILD_FLAG = -1;
    public final static int GET_COMMENT_FAILED = -2;
    public static int GET_SUCCESS_FLAG = 0;

    private Context mContext;
    // private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_detail);
        mContext = this;
        mPackDao = PackDao.getInstance(this);
        mSoftwareInfo = (AppSoftwareInfo) getIntent().getSerializableExtra("softwareinfo");
        if(mSoftwareInfo == null){
            finish();
        }

        if(mSoftwareInfo.getApkInfo() == null || mSoftwareInfo.getApkInfo().size()==0){
            finish();
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(PackMethod.DOWN_ACTION);
        filter.addAction(PackMethod.REFRESH_ACTION);
        registerReceiver(mBroadcastReceiver, filter);

        mDownListener = new CustomWebViewDownLoadListener(this);

        findView();
        freshAdapterForThis();
    }
    private void findView(){
        ImageLoader mImageLoader = new ImageLoader(getApplicationContext());
        ApkInfo info = mSoftwareInfo.getApkInfo().get(0);
        // mAppId = info.getAppid();
        // PARAMETER = "&appid="+mAppId+"&pageNo="+mCommentPage;

        //mScrollView = (ScrollView) findViewById(R.id.appdatil_scroll);
        //  lineView1 = (TextView) findViewById(R.id.line1);
        downLayout = (LinearLayout)findViewById(R.id.detail_downLayout);
        downLayout.setOnClickListener(this);
        mAppImageView = (ImageView) findViewById(R.id.app_image);
        detailDownBtnimage = (ImageView) findViewById(R.id.detail_downBtnimage);
        appname = (TextView) findViewById(R.id.appname);
        appVersion = (TextView) findViewById(R.id.version);
        appSize = (TextView) findViewById(R.id.appSize);
        downTimes = (TextView) findViewById(R.id.downTimes);
        publishTime = (TextView) findViewById(R.id.publishTime);
        downButton = (TextView) findViewById(R.id.detail_downButton);
       // previous_version = (TextView) findViewById(R.id.previous_version);
        description = (TextView) findViewById(R.id.description_content);
        appRating = (RatingBar) findViewById(R.id.detial_rating);
        /*setCommentRating = (RatingBar) findViewById(R.id.set_rating);
        moreDescroption = (ImageView) findViewById(R.id.more_descroption);
        moreDescroption.setOnClickListener(this);
        submitButton = (Button) findViewById(R.id.subment);
        loadMoreComment = (Button) findViewById(R.id.load_more_comment);
        commentListView = (ListView) findViewById(R.id.comment_list);
        commentListView = (ListViewForScrollView) findViewById(R.id.comment_list);
        commentEdit = (EditText) findViewById(R.id.comment_edit);
        mRating = setCommentRating.getRating();
        mAdapter = new CommentAdapter(this, mGradeList);
        commentListView.setAdapter(mAdapter);*/
        downButton.setOnClickListener(this);
        packageName = info.getPackageName();
        isExit = Utils.isPackageExit(this, packageName);
        setDownButtonState();
        mImageLoader.displayImage(mSoftwareInfo.getIconurl(), mAppImageView);
        appname.setText(mSoftwareInfo.getAppname());
        /*if(mSoftwareInfo.getApkInfo()!=null && mSoftwareInfo.getApkInfo().size()>1){
            ApkInfo info1 = mSoftwareInfo.getApkInfo().get(1);
            previous_version.setText(info1.getVersion());
        }*/
        appVersion.setText(getResources().getString(R.string.version)+": "+info.getVersion());
        appSize.setText(info.getSize()+"M");
        downTimes.setText(getResources().getString(R.string.to_download)+": "+mSoftwareInfo.getDownloadCount());
        publishTime.setText(getResources().getString(R.string.publishtime)+": "+info.getPublishdata());
        description.setText(mSoftwareInfo.getDescription());

        // params = (LayoutParams) description.getLayoutParams();

        appRating.setRating(mSoftwareInfo.getGradenum());

        /*submitButton.setOnClickListener(this);
        loadMoreComment.setOnClickListener(this);
         setCommentRating.setOnRatingBarChangeListener(this);
         GetCommentTask task = new GetCommentTask(mHandler);
        task.execute(COMMENT_HTTP+PARAMETER);*/
    }

    /*private void descriptionViewState(boolean isOnclick){
        if(isOnclick && !isUp){
            isUp = true;
            moreDescroption.setImageResource(R.drawable.descroption_more_up);
            params.height = LayoutParams.WRAP_CONTENT;
            description.setLayoutParams(params);
        }else if(isOnclick && isUp){
            isUp = false;
            moreDescroption.setImageResource(R.drawable.descroption_more_down);
            params.height = 200;
            description.setLayoutParams(params);
        }else{
            isUp = false;
            if(description.getLineCount()>5){
                moreDescroption.setVisibility(View.VISIBLE);
                params.height = 200;
                description.setLayoutParams(params);
            }else{
                moreDescroption.setVisibility(View.GONE);
                params.height = LayoutParams.WRAP_CONTENT;
                description.setLayoutParams(params);
            }
        }

    }*/

    @Override
    protected void onResume() {
        //mScrollView.scrollTo(0, 0);
        super.onResume();
    }
    private void setDownButtonState(){
        detailDownBtnimage.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.btn_icon_download);
        downLayout.setEnabled(true);
        isExit = Utils.isPackageExit(this, packageName);
        Pack pck = mPacks.get(packageName);
        int state =0;
        if(pck!=null && pck.getPackName().equals(mSoftwareInfo.getAppname())){
            state = pck.getPackState();
        }
        String downBtnText= State(state);
        if(isExit){
            downLayout.setBackgroundResource(R.drawable.app_down_button_green);
            downButton.setText(R.string.to_open);
            drawable = getResources().getDrawable(R.drawable.btn_icon_open);
            isHasNew = Utils.isHasNewVesion(this, packageName,mSoftwareInfo.getLatestVersion());
            if(isHasNew){
                downButton.setText(R.string.to_update);
                drawable = getResources().getDrawable(R.drawable.btn_icon_update);
            }
        }else{
            //downButton.setText(R.string.to_download);
            downButton.setText(downBtnText);
            downLayout.setBackgroundResource(R.drawable.app_down_button_blue);
        }
        if (state == Pack.STATE_IN_DOWN || Pack.STATE_WAIT == state){
            detailDownBtnimage.setVisibility(View.GONE);
            downButton.setText(State(state));
            downLayout.setBackgroundResource(R.drawable.app_down_button_blue);
            // downButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            //  downButton.setGravity(Gravity.CENTER);
        }else{
            // downButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            detailDownBtnimage.setImageDrawable(drawable);
        }
    }

    private String State(int state) {
        switch (state) {
        case 0:
            return getResources().getString(R.string.to_install);
        case Pack.STATE_INSTALL:
            return getResources().getString(R.string.to_open);
        case Pack.STATE_IN_DOWN:
            return getResources().getString(R.string.downloading);
        case Pack.STATE_IN_INSTALL:
            return getResources().getString(R.string.installing);
        case Pack.STATE_WAIT:
            return mContext.getResources().getString(R.string.wating);
        default:
            return getResources().getString(R.string.to_install);
        }
    }


    /*@Override
    public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
        mRating = arg1;
    }*/

    @Override
    protected void onDestroy() {
        // dismissProgressDialog();
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
    private void freshAdapterForThis() {
        new Thread(){
            @Override
            public void run() {
                try {
                    if (!mPackDao.isOpen()) {
                        mPackDao.open();
                    }
                    mPacks = mPackDao.findDownPackMaps();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    mHandler.sendEmptyMessage(1);
                }
            }

        }.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // dismissProgressDialog();
            switch (msg.what){
            /* case GET_COMMENT_FAILED:
                Toast.makeText(AppDetialActivity.this, R.string.get_comment_failed, Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(AppDetialActivity.this, R.string.comment_failed, Toast.LENGTH_SHORT).show();
                break;
            case 0:
                mCommentPage++;
                List<GradeInfo> list = (List<GradeInfo>) msg.obj;
                if(list!=null){
                    mGradeList.addAll(list);
                }
                if(mGradeList!=null && mGradeList.size()>0){
                    mAdapter.notifyDataSetChanged();
                    lineView1.setVisibility(View.VISIBLE);
                    loadMoreComment.setVisibility(View.VISIBLE);
                }
                descriptionViewState(false);
                mScrollView.scrollTo(0, 0);
                break;*/
            case 1:
                setDownButtonState();
                break;
                /* case 2:
                commentEdit.setText("");
                commentInfo.setGradetime(DateTimeUtils.timeShow(AppDetialActivity.this, System.currentTimeMillis(), false));
                mGradeList.add(0, commentInfo);
                lineView1.setVisibility(View.VISIBLE);
                loadMoreComment.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(AppDetialActivity.this, R.string.comment_success, Toast.LENGTH_SHORT).show();
                break;*/
            }
        }
    };


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.detail_downLayout:
            if(isExit && !isHasNew){
                Utils.openApplication(getApplicationContext(), packageName);
            }else{
                detailDownBtnimage.setVisibility(View.GONE);
                downLayout.setEnabled(false);
                downButton.setText(R.string.downloading);
                ApkInfo info = mSoftwareInfo.getApkInfo().get(0);
                if(info!=null){
                    try {
                        long fileSize =(long) (info.getSize()!=null?Float.parseFloat(info.getSize())*1024.0f*1024.0f:0);
                        mDownListener.downApp(info.getDownloadUrl(),mSoftwareInfo.getIconurl(), this,fileSize);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            /*case R.id.subment:
            String descripation = commentEdit.getText().toString();
            if(!TextUtils.isEmpty(descripation.trim())){
                showProgressDialog(getResources().getString(R.string.commenting));
                commentInfo = new GradeInfo();
                commentInfo.setAppid(mSoftwareInfo.getApkInfo().get(0).getAppid());
                commentInfo.setComment(descripation);
                commentInfo.setGradenum(mRating);
                PostCommentTask task = new PostCommentTask(mHandler,commentInfo);
                task.execute();
            }else{
                Toast.makeText(this, R.string.empty_waring, Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.load_more_comment:
            PARAMETER = "&appid="+mAppId+"&pageNo="+mCommentPage;
            GetCommentTask task = new GetCommentTask(mHandler);
            task.execute(COMMENT_HTTP+PARAMETER);
            break;
        case R.id.more_descroption:
            descriptionViewState(true);
            break;*/
        default:
            break;
        }
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context contex, Intent intent) {
            if(intent.getAction().equals(PackMethod.REFRESH_ACTION)){
                freshAdapterForThis();
            }else{
                String packageName = intent.getStringExtra(PackMethod.PACKAGENAME);
                List<ApkInfo> info  = mSoftwareInfo.getApkInfo();
                if(info != null && info.size()>0){
                    if(!TextUtils.isEmpty(packageName) && packageName.equals(info.get(0).getPackageName())){

                    }
                }
            }
        }
    };

    /* private void showProgressDialog(String message){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(mContext);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    private void dismissProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }*/
}
