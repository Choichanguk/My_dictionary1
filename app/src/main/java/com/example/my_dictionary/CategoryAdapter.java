package com.example.my_dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<CategoryItem> mdata= null;
    private Context mContext;
//    private  OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수

//    // 커스텀 리스너 인터페이스
//    public interface OnItemClickListener{
//        void onItemClick(View v, int position);
//    }
//
//    // OnItemClickListener 객체를 전달하는 메서드
//    public void setOnItemClickListener(OnItemClickListener listener){
//        this.mListener = listener;
//    }
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

//        holder.delete.setTag(holder.getAdapterPosition()); // 뷰홀더의 어댑터 포지션을 태그로 달아준다.
//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = (int)v.getTag(); // 달아뒀던 태그 값을 position 변수에 담는다.
//                mdata.remove(position);
//                notifyDataSetChanged();
//
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_name;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);


//            edit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if(pos != RecyclerView.NO_POSITION){
//
//                        // 리스너 객체의 메서드 호출
//                        if(mListener != null){
//                            mListener.onItemClick(v, pos);
//                        }
//
//                    }
//                }
//            });

        }
    }

}
