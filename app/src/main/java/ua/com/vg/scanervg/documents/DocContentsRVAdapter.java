package ua.com.vg.scanervg.documents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.com.vg.scanervg.R;

public class DocContentsRVAdapter extends RecyclerView.Adapter<DocContentsRVAdapter.DocContentsViewHolder>{
    private List<RowContent> mRowContentsList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    public DocContentsRVAdapter(Context ctx,List<RowContent> data){
        this.mLayoutInflater = LayoutInflater.from(ctx);
        this.mRowContentsList = data;
    }

    @NonNull
    @Override
    public DocContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.rowcontent_layout,parent,false);
        return new DocContentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocContentsViewHolder holder, int position) {
        RowContent rowContent = mRowContentsList.get(position);
        holder.tvRowContentEntName.setText(rowContent.getEntName());
        holder.tvRowContentEnCode.setText(rowContent.getEntCode());
        holder.tvRowContentQty.setText(String.valueOf(rowContent.getQty()));


    }

    @Override
    public int getItemCount() {
        return mRowContentsList.size();
    }

    public class DocContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvRowContentEntName;
        TextView tvRowContentEnCode;
        TextView tvRowContentQty;

        public DocContentsViewHolder(View itemView) {
            super(itemView);
            tvRowContentEntName = itemView.findViewById(R.id.tvRowContentEntName);
            tvRowContentEnCode  = itemView.findViewById(R.id.tvRowContentEnCode);
            tvRowContentQty     = itemView.findViewById(R.id.tvRowContentQty);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null){
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }

    public RowContent getItem(int index){
        return mRowContentsList.get(index);
    }

    public void removeItem(int index){
        mRowContentsList.remove(index);
        notifyItemRemoved(index);
    }
}
