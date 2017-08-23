package com.example.scy.myapplication;

/**
 * Created by shichenyang on 2017/6/1/0001.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Activity mContext;

    private LayoutInflater inflater;

    private List<Data> mPlanDetails;

    public MyAdapter(Activity context, List<Data> mPlanDetails) {
        mContext = context;

        inflater = LayoutInflater.from(context);
        this.mPlanDetails = mPlanDetails;

    }

    @Override
    public int getCount() {
        return mPlanDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlanDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Data planDetail = this.mPlanDetails.get(position);
        if (convertView == null) {
             holder =  new ViewHolder();

            convertView = inflater.inflate(R.layout.list_item_proj_plan, parent, false);

            holder.img_plan = (ImageView) convertView.findViewById(R.id.img_plan);

            holder.text_day = (TextView) convertView.findViewById(R.id.text_day);

            holder.text_hour = (TextView) convertView.findViewById(R.id.text_hour);

            holder.text_tags = (TextView) convertView.findViewById(R.id.text_tags);

            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();


        if (planDetail != null) {
           //ImageLoaderUtil.getInstance().displayListItemImage(imgUrl, holder.img_plan);
            Calendar time = stampTocal(planDetail.getstime());
            Calendar localtime = Calendar.getInstance();
            if(time.get(Calendar.YEAR) == localtime.get(Calendar.YEAR)&&time.get(Calendar.MONTH) == localtime.get(Calendar.MONTH)){
                if (time.get(Calendar.DAY_OF_MONTH) == localtime.get(Calendar.DAY_OF_MONTH)) {
                    holder.text_day.setText("今天");
                    holder.text_hour.setText(time.get(Calendar.HOUR_OF_DAY)+":"+time.get(Calendar.MINUTE));
                }
                else {if (time.get(Calendar.DAY_OF_MONTH) + 1 == (localtime.get(Calendar.DAY_OF_MONTH))) {
                    holder.text_day.setText("昨天");
                    holder.text_hour.setText(time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                }
                else {
                    holder.text_day.setText(Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH));
                    holder.text_hour.setText(time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                }
                }
            }
            else {
                holder.text_day.setText(Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH));
                holder.text_hour.setText(time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
            }
            holder.text_tags.setText(planDetail.gettags());
            int status = planDetail.getStatus();
            if(status == 0)
            {
                holder.score.setText("审核中");
                holder.score.setTextSize(20);
                holder.score.setTextColor(Color.parseColor("#555555"));
            }
            else if(status == 1)
            {
                holder.score.setText("+10");
                holder.score.setTextSize(30);
                holder.score.setTextColor(Color.parseColor("#228B22"));
            }
            else if (status == 2)
            {
                holder.score.setText("+0");
                holder.score.setTextSize(30);
                holder.score.setTextColor(Color.parseColor("#FF0000"));
            }
            DisplayImageOptions options=new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .build();
            //加载图片
            ImageLoader.getInstance().displayImage(planDetail.geturl(),holder.img_plan,options);
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.proj_plans_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        String headerText = this.mPlanDetails.get(position).getstime();
        Calendar time = stampTocal(headerText);
        Calendar localtime = Calendar.getInstance();
        if(time.get(Calendar.YEAR) == localtime.get(Calendar.YEAR))
            if(time.get(Calendar.MONTH) == localtime.get(Calendar.MONTH))
                holder.text.setText("本月");
            else
                holder.text.setText(Integer.toString(time.get(Calendar.MONTH)+1)+"月");
        else
            holder.text.setText( Integer.toString(time.get(Calendar.YEAR))+"年"+ Integer.toString(time.get(Calendar.MONTH)+1)+"月");
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return stampTocal(this.mPlanDetails.get(position).getstime()).get(Calendar.MONTH)+stampTocal(this.mPlanDetails.get(position).getstime()).get(Calendar.YEAR);
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        ImageView img_plan;

        TextView text_day;

        TextView text_hour;

        TextView text_tags;

        TextView score;
    }

    public static Calendar stampTocal(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
