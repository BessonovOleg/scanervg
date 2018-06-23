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
import ua.com.vg.scanervg.documents.DocInfo;
import ua.com.vg.scanervg.utils.AppUtils;


public class DocInfoRVAdapter extends RecyclerView.Adapter<DocInfoRVAdapter.DocInfoViewHolder> {
    private List<DocInfo> mdocInfoList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    @NonNull
    @Override
    public DocInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.docinfo_layout,parent,false);
        return new DocInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocInfoViewHolder holder, int position) {
        DocInfo docInfo = mdocInfoList.get(position);
        holder.docName.setText(docInfo.getDocName());
        holder.docNumber.setText(docInfo.getDocNumber());
        holder.docMemo.setText(docInfo.getDocMemo());
        holder.docAgent.setText(docInfo.getDocAgentName());
        holder.docDate.setText(docInfo.getStrDocDate());
        if(docInfo.getDocSum() != 0){
            holder.docSum.setText(AppUtils.roundAndConvertToStringDigit(docInfo.getDocSum()));
        }
    }

    @Override
    public int getItemCount() {
        return mdocInfoList.size();
    }

    public DocInfoRVAdapter(Context context, List<DocInfo> data){
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mdocInfoList = data;
    }

    public class DocInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView docName;
        TextView docDate;
        TextView docNumber;
        TextView docAgent;
        TextView docMemo;
        TextView docSum;

        public DocInfoViewHolder(View itemView){
            super(itemView);
            docName = itemView.findViewById(R.id.di_docName);
            docDate = itemView.findViewById(R.id.di_docDate);
            docNumber = itemView.findViewById(R.id.di_docNumber);
            docAgent = itemView.findViewById(R.id.di_docAgentName);
            docMemo  = itemView.findViewById(R.id.di_docMemo);
            docSum   = itemView.findViewById(R.id.di_docSum);
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
