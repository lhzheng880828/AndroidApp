/**
 * Author:yyzhu
 * Copyright:GrandStream
 */
package com.base.module.pack.adapter;

import java.util.List;

import com.base.module.pack.R;
import com.base.module.pack.bean.GradeInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;


public class CommentAdapter extends ArrayAdapter<GradeInfo>{

    private LayoutInflater inflater;
    public CommentAdapter(Context context, List<GradeInfo> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHelper viewHelper = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.comment_item, null);
            viewHelper = new ViewHelper(convertView);
            convertView.setTag(viewHelper);
        }else{
            viewHelper = (ViewHelper) convertView.getTag();
        }
        GradeInfo info = (GradeInfo) getItem(position);
        viewHelper.commenter.setText(info.getGradetime());
        viewHelper.commentContent.setText(info.getComment());
        viewHelper.commentRate.setRating(info.getGradenum());
        return convertView;
    }
    class ViewHelper{
        private RatingBar commentRate;
        private TextView commenter;
        private TextView commentContent;
        public ViewHelper(View view){
            commenter = (TextView) view.findViewById(R.id.commenter);
            commentRate = (RatingBar) view.findViewById(R.id.comment_rating);
            commentContent = (TextView) view.findViewById(R.id.comment_content);
        }
    }
}
