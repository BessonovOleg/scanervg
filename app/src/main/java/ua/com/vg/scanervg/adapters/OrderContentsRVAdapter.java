package ua.com.vg.scanervg.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ua.com.vg.scanervg.R;
import ua.com.vg.scanervg.documents.RowContent;
import ua.com.vg.scanervg.utils.AppUtils;

public class OrderContentsRVAdapter extends RecyclerView.Adapter<OrderContentsRVAdapter.OrderContentsViewHolder>{
    private List<RowContent> mRowContentsList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    public OrderContentsRVAdapter(Context ctx, List<RowContent> data){
        this.mLayoutInflater = LayoutInflater.from(ctx);
        this.mRowContentsList = data;
    }

    public class OrderContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView entNameOrderContent;
        TextView unitOrderContent;
        TextView qtyOrderContent;
        TextView priceOrderContent;
        TextView sumOrderContent;

        public OrderContentsViewHolder(View itemView) {
            super(itemView);
            entNameOrderContent = itemView.findViewById(R.id.entNameOrderContent);
            unitOrderContent    = itemView.findViewById(R.id.unitOrderContent);
            qtyOrderContent     = itemView.findViewById(R.id.qtyOrderContent);
            priceOrderContent   = itemView.findViewById(R.id.priceOrderContent);
            sumOrderContent     = itemView.findViewById(R.id.sumOrderContent);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(mClickListener != null){
                mClickListener.onItemClick(view,getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public OrderContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.ordercontent_layout,parent,false);
        return new OrderContentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderContentsViewHolder holder, int position) {
        RowContent rowContent = mRowContentsList.get(position);
        holder.entNameOrderContent.setText(rowContent.getEntName());
        holder.unitOrderContent.setText(rowContent.getUnitName());
        holder.qtyOrderContent.setText(AppUtils.roundAndConvertToStringDigit(rowContent.getQty()));
        holder.priceOrderContent.setText(AppUtils.roundAndConvertToStringDigit(rowContent.getPrice()));
        holder.sumOrderContent.setText(AppUtils.roundAndConvertToStringDigit(rowContent.getSum()));
    }

    @Override
    public int getItemCount() {
        return mRowContentsList.size();
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
