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

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder>{

    private ArrayList<Shared_item> mdata_unfiltered= null;
    private ArrayList<Shared_item> madta_filtered = null;

//    private ArrayList<Shared_item> mdata= null;
    private Context mContext;
    private SharedAdapter.OnItemClickListener mListener = null; // 리스너 객체를 저장하는 변수
    SharedClass sharedClass = new SharedClass();
    private String user_id;

    public SharedAdapter(Context context, ArrayList<Shared_item> mdata) {
//        this.mdata = mdata;
        this.mdata_unfiltered = mdata;
        this.madta_filtered = mdata;
        this.mContext = context;
        user_id = sharedClass.loadUserId(mContext);
    }

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    //
    // OnItemClickListener 객체를 전달하는 메서드
    public void setOnItemClickListener(SharedAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SharedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view= inflater.inflate(R.layout.item_shared_category, parent, false);
        SharedAdapter.ViewHolder vh = new SharedAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SharedAdapter.ViewHolder holder, int position) {
        Shared_item item = madta_filtered.get(position);

        holder.category_name.setText(item.getCategory_name());
        holder.user_id.setText(item.getUser_id() + " | ");
        holder.num_download.setText(item.getNum_download() + "회 다운로드");
        holder.num_word.setText(item.getNum_word());

        if(user_id.equals(item.getUser_id())){
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
        else{
            holder.btn_delete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return madta_filtered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_name, user_id, num_download, num_word;
        ImageButton btn_delete;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);
            user_id = itemView.findViewById(R.id.user_id);
            num_download = itemView.findViewById(R.id.num_download);
            num_word = itemView.findViewById(R.id.num_word);
            btn_delete = itemView.findViewById(R.id.btn_delete);

            btn_delete.setOnClickListener(new View.OnClickListener() {
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

    public void filter(String text) {
        if(text.isEmpty()){

            madta_filtered = mdata_unfiltered;

        }
        else {
            ArrayList<Shared_item> result = new ArrayList<>();
            text = text.toLowerCase();
            for(Shared_item item: mdata_unfiltered){
                Log.e("adapter", "단어: " + item.getCategory_name());
                Log.e("adapter", "입력 글자: " + text);
                if(item.getCategory_name().toLowerCase().contains(text) || item.getUser_id().toLowerCase().contains(text)){
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
