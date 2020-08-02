package com.example.my_dictionary;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<CategoryItem> mdata= null;
    private Context mContext;
    private  OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, View itemView, int position);
    }
//
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemLongClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
//
//
//    // --------------------------------------------------------------------------------------------------
    public CategoryAdapter(Context context, ArrayList<CategoryItem> mdata) {
        this.mdata = mdata;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_category, parent, false);
        CategoryAdapter.ViewHolder vh = new CategoryAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

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
        ImageButton btn_option;



        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);
            btn_option = itemView.findViewById(R.id.btn_option);
            num_word = itemView.findViewById(R.id.num_word);


            btn_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, itemView, position);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // 리스너 객체의 메서드 호출
                    if(position != RecyclerView.NO_POSITION){
                        mListener.onItemClick(v, itemView, position);
                    }
                }
            });
        }

    }
}

