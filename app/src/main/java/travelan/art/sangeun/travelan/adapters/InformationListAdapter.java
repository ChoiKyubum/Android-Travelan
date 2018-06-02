package travelan.art.sangeun.travelan.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import travelan.art.sangeun.travelan.R;
import travelan.art.sangeun.travelan.models.Information;

/**
 * Created by sangeun on 2018-05-31.
 */

public class InformationListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "InformationListAdapter";
    private List<Information> items;
    private Context context;

    public InformationListAdapter(List<Information> items, Context context){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_information,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mViewHolder = (ViewHolder)holder;
        final Information item = items.get(position);
        Log.i(TAG, "onBindViewHolder: "+item.toString());
        mViewHolder.title.setText(item.title);
        mViewHolder.wrtDt.setText(item.wrtDt);
        mViewHolder.countryName.setText(item.countryName);
        mViewHolder.expandableTextView.setText(item.content);

        if (item.flagImage!=null) {
//        Picasso.get().load(item.flagImage).into(mViewHolder.flag);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView countryName, wrtDt, title,expandableTextView;
//        public ExpandableTextView expand_text_view;
        public ImageView flag;

        public ViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.countryName);
            wrtDt = itemView.findViewById(R.id.wrtDt);
            title = itemView.findViewById(R.id.title);
            expandableTextView = itemView.findViewById(R.id.expandableTextView);

            flag = itemView.findViewById(R.id.flag);
        }
    }
}