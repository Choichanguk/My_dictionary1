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

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder>{

    private ArrayList<CategoryItem> mdata= null;
    private Context mContext;
    private ShareAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    public ShareAdapter(Context context, ArrayList<CategoryItem> mdata) {
        this.mdata = mdata;
        this.mContext = context;
    }

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemClickListener(ShareAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_share_category, parent, false);
        ShareAdapter.ViewHolder vh = new ShareAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ShareAdapter.ViewHolder holder, int position) {
        CategoryItem item = mdata.get(position);

        holder.category_name.setText(item.getCategory_name());
        holder.num_word.setText(item.getNum_word());

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_name, num_word;
        ImageButton btn_share;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            category_name = itemView.findViewById(R.id.category_name);
            num_word = itemView.findViewById(R.id.num_word);
            btn_share = itemView.findViewById(R.id.btn_share);

            btn_share.setOnClickListener(new View.OnClickListener() {
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
