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

public class SearchAdapter2 extends RecyclerView.Adapter<SearchAdapter2.ViewHolder> {
    private ArrayList<search_item> mdata= null;
    private Context mContext;
    private SearchAdapter2.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    public SearchAdapter2(Context context, ArrayList<search_item> mdata) {
        this.mdata = mdata;
        this.mContext = context;
    }

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemLongClickListener(SearchAdapter2.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SearchAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_search2, parent, false);
        SearchAdapter2.ViewHolder vh = new SearchAdapter2.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter2.ViewHolder holder, int position) {
        search_item item = mdata.get(position);

        if(item.getSearch_word().equals("")){
            holder.word.setVisibility(View.GONE);
            holder.definition.setText(item.getDefinition());
        }
        else{
            holder.word.setText(item.getSearch_word());
            holder.definition.setText(item.getDefinition());
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView word, definition;
        ImageButton btn_plus;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.result_word);
            definition = itemView.findViewById(R.id.result_definition);
            btn_plus = itemView.findViewById(R.id.btn_plus);

            btn_plus.setOnClickListener(new View.OnClickListener() {
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
