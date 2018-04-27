package com.haddis.example.bluetoothtest.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haddis.example.bluetoothtest.R;

import java.util.List;

/**
 * Created by haddis on 18-4-23.
 */

public class BlueAdapter extends BaseAdapter {
    private Context mContext;
    private List<BluetoothDevice> mData;

    public BlueAdapter(Context mContext, List<BluetoothDevice> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        BleViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ble_adapter, null);
            viewHolder = new BleViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BleViewHolder) convertView.getTag();
        }
        if (mData.get(position).getName() != null) {
            viewHolder.mTextViewName.setText("名称:" + mData.get(position).getName());
        } else {
            viewHolder.mTextViewName.setText("名称:null");
        }
        viewHolder.mTextViewMacAddress.setText("Address:" + mData.get(position).getAddress());

        return convertView;
    }

    class BleViewHolder {
        TextView mTextViewName;
        TextView mTextViewMacAddress;
        TextView mTextViewConnect;

        public BleViewHolder(View convertView) {
            mTextViewName = (TextView) convertView.findViewById(R.id.tv_ble_adapter_name);
            mTextViewMacAddress = (TextView) convertView.findViewById(R.id.tv_ble_adapter_mac);
            mTextViewConnect = (TextView) convertView.findViewById(R.id.tv_ble_adapter_connect);
        }
    }
}
