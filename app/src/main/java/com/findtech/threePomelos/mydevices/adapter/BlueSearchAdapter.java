package com.findtech.threePomelos.mydevices.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/26
 *   desc     :
 *   version  :   V 1.0.5
 * @author Administrator
 */
public class BlueSearchAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<DeviceCarBean> arrayList ;
    private ItemClickListtener itemCliclListener;
    private Context context;
    boolean isLinking = false;

    public BlueSearchAdapter(Context context) {
        this.context = context;
    }

    public void setItemCliclListener(ItemClickListtener itemCliclListener) {
        this.itemCliclListener = itemCliclListener;
    }

    public void setArrayList(ArrayList<DeviceCarBean> arrayList) {
        this.arrayList = arrayList;
    }

    public void setLinking(boolean linking) {
        isLinking = linking;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ReViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_link_item, parent, false));


    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReViewHolder ) {
            DeviceCarBean device = arrayList.get(position);
        String name = device.getDeviceName();
            if (isLinking) {
                ((ReViewHolder) holder).text_position.setText(String.valueOf(position + 2));
            } else {
                ((ReViewHolder) holder).text_position.setText(String.valueOf(position + 1));
            }
            ((ReViewHolder) holder).text_name.setText(name);

            if ( device.getDeviceaAddress().equals(IContent.getInstacne().address) ) {
                ((ReViewHolder) holder).text_detail.setVisibility(View.VISIBLE);
                ((ReViewHolder) holder).text_state.setVisibility(View.GONE);
            }
            else {
                ((ReViewHolder) holder).text_detail.setVisibility(View.GONE);
                ((ReViewHolder) holder).text_state.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0:arrayList.size();
    }

    class ReViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_position;
        TextView text_name;
        TextView  text_state;
        TextView text_detail ;
        public ReViewHolder(View itemView) {
            super(itemView);
            text_position = itemView.findViewById(R.id.text_position);
            text_name = itemView.findViewById(R.id.text_name);
            text_state = itemView.findViewById(R.id.text_state);
            text_detail = itemView.findViewById(R.id.text_detail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemCliclListener.click(position);
        }
    }

}
