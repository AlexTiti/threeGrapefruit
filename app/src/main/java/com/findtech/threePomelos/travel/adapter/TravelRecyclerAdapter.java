package com.findtech.threePomelos.travel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.travel.bean.KilometerBean;
import com.findtech.threePomelos.travel.view.TravelView;

import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/28
 */

public class TravelRecyclerAdapter extends RecyclerView.Adapter<TravelRecyclerAdapter.ViewHolder> {

    private ArrayList<KilometerBean> travelBeans;

    public void setTravelBeans(ArrayList<KilometerBean> travelBeans) {
        this.travelBeans = travelBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KilometerBean bean = travelBeans.get(position);
        holder.tv_time_begin.setText(bean.getStartTime());
        holder.tv_time_end.setText(bean.getEndTime());
        holder.tv_calorie.setText(String.valueOf(bean.getCalorie()));
        holder.tv_kilometer.setText(String.valueOf(bean.getDistance()));

        if (position == travelBeans.size() - 1) {
            holder.travelView.setShowLine(false);
            holder.tv_bac_line.setVisibility(View.INVISIBLE);
        }else {
            holder.travelView.setShowLine(true);
            holder.tv_bac_line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return travelBeans == null ? 0 : travelBeans.size();
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TravelView travelView;
        TextView tv_time_end;
        TextView tv_time_begin;
        TextView tv_kilometer;
        TextView tv_bac_line;
        TextView tv_calorie;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_time_end = itemView.findViewById(R.id.tv_time_end);
            tv_time_begin = itemView.findViewById(R.id.tv_time_begin);
            tv_kilometer = itemView.findViewById(R.id.tv_kilometer);
            tv_calorie = itemView.findViewById(R.id.tv_calorie);
            tv_bac_line = itemView.findViewById(R.id.tv_bac_line);
            travelView = itemView.findViewById(R.id.travelView);
        }
    }
}
