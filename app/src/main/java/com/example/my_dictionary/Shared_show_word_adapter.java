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

public class Shared_show_word_adapter extends RecyclerView.Adapter<Shared_show_word_adapter.ViewHolder> {

    private ArrayList<Shared_word_item> mdata= null;
    private Context mContext;
    private Shared_show_word_adapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemClickListener(Shared_show_word_adapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    public Shared_show_word_adapter(Context context, ArrayList<Shared_word_item> mdata) {
        this.mdata = mdata;
        this.mContext = context;
    }

    @NonNull
    @Override
    public Shared_show_word_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_view_shared_category, parent, false);
        Shared_show_word_adapter.ViewHolder vh = new Shared_show_word_adapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Shared_show_word_adapter.ViewHolder holder, int position) {
        Shared_word_item item = mdata.get(position);

        String word = item.getWord();
        word = word.replaceAll(System.getProperty("line.separator"), "");

        holder.word.setText(word);
        holder.meaning.setText(item.getMeaning());

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView word, meaning;
        ImageButton btn_tts;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.result_word);
            meaning = itemView.findViewById(R.id.result_definition);
            btn_tts = itemView.findViewById(R.id.btn_tts);

            btn_tts.setOnClickListener(new View.OnClickListener() {
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
