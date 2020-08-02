package com.example.my_dictionary;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>{

    private ArrayList<search_item> listData;
    private ViewPagerAdapter.OnItemClickListener mListener = null;

    public interface OnItemClickListener{
        void onItemClick(View v, int position, search_item item);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemClickListener(ViewPagerAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    ViewPagerAdapter(ArrayList<search_item> data) {
        this.listData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.onBind(listData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView word, btn_bookmark;
        private TextView meaning;
        ImageButton btn_tts;


        search_item data;

        ViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.word);
            meaning = itemView.findViewById(R.id.meaning);
            btn_tts = itemView.findViewById(R.id.btn_tts);
            btn_bookmark = itemView.findViewById(R.id.btn_bookmark);

            btn_tts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, listData.get(position));
                    }
                }
            });

            btn_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, listData.get(position));
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });

        }

        public void onBind(search_item data){
            this.data = data;

            word.setText(data.getSearch_word());
            meaning.setText(data.getDefinition());

            if(data.isCheck()){
                btn_bookmark.setTextColor(Color.parseColor("#FFD600"));
            }
            else{
                btn_bookmark.setTextColor(Color.parseColor("#9E9E9E"));
            }

        }
    }

}
