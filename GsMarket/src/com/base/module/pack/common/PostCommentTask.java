/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.common;

import com.base.module.pack.bean.GradeInfo;
import com.base.module.pack.main.AppDetialActivity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class PostCommentTask extends AsyncTask<String, Integer, Integer>{
    private String GRADGE_POST = "http://market.ipvideotalk.com:9023/marketinterface/saveGrandInfoJson";
    private Handler mHandler;
    private GradeInfo mCommentInfo;
    public PostCommentTask(Handler handler,GradeInfo commentInfo){
        mHandler = handler;
        mCommentInfo = commentInfo;
    }
    @Override
    protected Integer doInBackground(String... arg0) {
        String json = JsonHelpUtil.JavaObjToJson(mCommentInfo);
        HttpUtils.ResposeInfo r = HttpUtils.requestServer(GRADGE_POST, json);
        if(r!=null){
            return r.getResponseCode();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result==200){
            Message message = new Message();
            message.what=AppDetialActivity.POST_SUCCESS_FLAG;
            mHandler.sendMessage(message);
        }else{
            mHandler.sendEmptyMessage(AppDetialActivity.POST_FAILD_FLAG);
        }
        super.onPostExecute(result);
    }
}
