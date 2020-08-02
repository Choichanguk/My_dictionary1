package com.example.my_dictionary;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private ArrayList<search_item> mdata_unfiltered= null;
    private ArrayList<search_item> madta_filtered = null;
    private Context mContext;
    private SearchAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    public SearchAdapter(Context context, ArrayList<search_item> mdata) {
        this.mdata_unfiltered = mdata;
        this.madta_filtered = mdata;
        this.mContext = context;
    }


    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position, search_item item);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemLongClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_search, parent, false);
        SearchAdapter.ViewHolder vh = new SearchAdapter.ViewHolder(view);
        return vh;
    }



    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        search_item item = madta_filtered.get(position);



//        holder.word.setText(item.getSearch_word());
        String word = item.getSearch_word();
        word = word.replaceAll(System.getProperty("line.separator"), "");

        holder.word.setText(word);
        holder.definition.setText(item.getDefinition());

        if(item.isCheck()){
            holder.btn_bookmark.setTextColor(Color.parseColor("#FFD600"));
        }
        else{
            holder.btn_bookmark.setTextColor(Color.parseColor("#9E9E9E"));
        }

    }

    @Override
    public int getItemCount() {
        return madta_filtered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView word, definition, btn_bookmark;
        ImageButton btn_option, btn_tts;
//        ImageView ;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.result_word);
            definition = itemView.findViewById(R.id.result_definition);
            btn_option = itemView.findViewById(R.id.btn_option);
            btn_tts = itemView.findViewById(R.id.btn_tts);
            btn_bookmark = itemView.findViewById(R.id.btn_bookmark);

            btn_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, madta_filtered.get(position));
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });

            btn_tts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, madta_filtered.get(position));
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });

            btn_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, madta_filtered.get(position));
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
                        mListener.onItemClick(v, position, madta_filtered.get(position));
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });
        }
    }

    public void filter(String text) {
        if(text.isEmpty()){

            madta_filtered = mdata_unfiltered;

        }
        else {
            ArrayList<search_item> result = new ArrayList<>();
            text = text.toLowerCase();
            for(search_item item: mdata_unfiltered){
                Log.e("adapter", "단어: " + item.getSearch_word());
                Log.e("adapter", "입력 글자: " + text);
                if(item.getSearch_word().toLowerCase().contains(text) || item.getDefinition().toLowerCase().contains(text)){
                    result.add(item);
                }
            }
//            Log.e("adapter", "result sizeL " + result.size());
            madta_filtered = new ArrayList<>();
            madta_filtered.addAll(result);

        }
        notifyDataSetChanged();
    }
}
