/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.common;

import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.base.module.pack.bean.GradeInfo;
import com.base.module.pack.main.AppDetialActivity;


public class GetCommentTask extends AsyncTask<String, Integer, String>{
    private Handler mHandler;
    public GetCommentTask(Handler handler){
        mHandler = handler;
    }
    @Override
    protected String doInBackground(String... arg0) {
        String json = JsonHelpUtil.doDownloadFromHttpServer(arg0[0]);
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        if(!TextUtils.isEmpty(result) && !"error".equals(result)){
            List<GradeInfo> list = JsonHelpUtil.JsonCommentToJava(result);
            Message message = new Message();
            message.obj=list;
            message.what=AppDetialActivity.GET_SUCCESS_FLAG;
            mHandler.sendMessage(message);
        }else if("error".equals(result)){
            mHandler.sendEmptyMessage(AppDetialActivity.GET_COMMENT_FAILED);
        }
        super.onPostExecute(result);
    }
}
