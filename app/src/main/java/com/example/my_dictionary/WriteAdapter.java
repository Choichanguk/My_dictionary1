package com.example.my_dictionary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WriteAdapter extends RecyclerView.Adapter<WriteAdapter.ViewHolder>{


    private ArrayList<search_item> mdata= null;
    private Context mContext;
    private WriteAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    public WriteAdapter(Context context, ArrayList<search_item> mdata) {
        this.mdata = mdata;
        this.mContext = context;
    }


    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemLongClickListener(WriteAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public WriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_write, parent, false);
        WriteAdapter.ViewHolder vh = new WriteAdapter.ViewHolder(view);
        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull WriteAdapter.ViewHolder holder, int position) {
        search_item item = mdata.get(position);

        holder.definition.setText(item.getDefinition());
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView definition;
        ImageButton btn_option;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            definition = itemView.findViewById(R.id.result_definition);
            btn_option = itemView.findViewById(R.id.btn_option);

            btn_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position);
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position);
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });
        }
    }

}
