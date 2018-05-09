package ua.com.vg.scanervg.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.documents.RowContent;

public class InventContentRVAdapter extends RecyclerView.Adapter<InventContentRVAdapter.InventContentsViewHolder>{
    private List<RowContent> mInventContentsList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    public InventContentRVAdapter(Context ctx, List<RowContent> data){
        this.mLayoutInflater = LayoutInflater.from(ctx);
        this.mInventContentsList = data;
    }

    public class InventContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView entNameInventContent;
        TextView unitInventContent;
        TextView qtyInventContent;

        public InventContentsViewHolder(View itemView) {
            super(itemView);
            entNameInventContent = itemView.findViewById(R.id.entNameInventContent);
            unitInventContent = itemView.findViewById(R.id.unitInventContent);
            qtyInventContent = itemView.findViewById(R.id.qtyInventContent);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null){
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public InventContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.inventaraizationcontent_layout,parent,false);
        return new InventContentRVAdapter.InventContentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventContentsViewHolder holder, int position) {
        RowContent rowContent = mInventContentsList.get(position);
        holder.entNameInventContent.setText(rowContent.getEntName());
        holder.unitInventContent.setText(rowContent.getUnitName());
        holder.qtyInventContent.setText(String.valueOf(rowContent.getQty()));
    }

    @Override
    public int getItemCount() {
       return mInventContentsList.size();
    }

    public RowContent getItem(int index){
        return mInventContentsList.get(index);
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public void removeItem(int index){
        mInventContentsList.remove(index);
        notifyItemRemoved(index);
    }
}
