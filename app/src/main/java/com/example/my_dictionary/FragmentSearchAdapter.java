package com.example.my_dictionary;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentSearchAdapter extends RecyclerView.Adapter<FragmentSearchAdapter.ViewHolder> {

    private ArrayList<search_item> list= null;

    private Context mContext;
    private FragmentSearchAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    public FragmentSearchAdapter(Context context, ArrayList<search_item> mdata) {
        this.list = mdata;
        this.mContext = context;
    }

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position, search_item item);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemLongClickListener(FragmentSearchAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FragmentSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_fragment_search, parent, false);
        FragmentSearchAdapter.ViewHolder vh = new FragmentSearchAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentSearchAdapter.ViewHolder holder, int position) {
        search_item item = list.get(position);



//        holder.word.setText(item.getSearch_word());
        String word = item.getSearch_word();
        word = word.replaceAll(System.getProperty("line.separator"), "");

        holder.word.setText(word);
        holder.definition.setText(item.getDefinition());
    }

    @Override
    public int getItemCount() {
        return list.size();
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

            btn_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, position, list.get(position));
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
                        mListener.onItemClick(v, position, list.get(position));
                        Log.e("adapter", String.valueOf(position));
                    }
                }
            });

        }
    }
}
