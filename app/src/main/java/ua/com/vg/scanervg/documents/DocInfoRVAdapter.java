package ua.com.vg.scanervg.documents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import ua.com.vg.scanervg.R;


public class DocInfoRVAdapter extends RecyclerView.Adapter<DocInfoRVAdapter.DocInvoViewHolder> {
    private List<DocInfo> mdocInfoList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    @NonNull
    @Override
    public DocInvoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.docinfo_layout,parent,false);
        return new DocInvoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocInvoViewHolder holder, int position) {
        DocInfo docInfo = mdocInfoList.get(position);
        holder.docName.setText(docInfo.getDocName());
        holder.docNumber.setText(docInfo.getDocNumber());
    }

    @Override
    public int getItemCount() {
        return mdocInfoList.size();
    }

    public DocInfoRVAdapter(Context context, List<DocInfo> data){
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mdocInfoList = data;
    }

    public class DocInvoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView docName;
        TextView docDate;
        TextView docNumber;

        public DocInvoViewHolder(View itemView){
            super(itemView);
            docName = itemView.findViewById(R.id.docName);
            docDate = itemView.findViewById(R.id.docDate);
            docNumber = itemView.findViewById(R.id.docNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener != null) mClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener{
        void onItemClick(View view,int position);
    }

    public DocInfo getItem(int index){
       return mdocInfoList.get(index);
    }
}
