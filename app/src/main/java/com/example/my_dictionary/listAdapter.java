package com.example.my_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class listAdapter extends BaseAdapter {
    Context mContext = null;
    ArrayList<CategoryItem> mdata;
    private listAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemClickListener(listAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public listAdapter(Context mContext, ArrayList<CategoryItem> mdata) {
        this.mContext = mContext;
        this.mdata = mdata;
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_category2, null, false);
        }

        TextView category_name = (TextView) convertView.findViewById(R.id.category_name);
        category_name.setText(mdata.get(position).getCategory_name());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });
        return convertView;
    }
}
