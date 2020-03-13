package com.gprotechnologies.gprodesktop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gprotechnologies.gprodesktop.R;

import java.util.List;

import jcifs.smb.SmbFile;

public class AppUpdateListViewAdapter extends RecyclerView.Adapter<AppUpdateListViewAdapter.MyHolder> {

    private Context context;
    private List<SmbFile> data;
    private OnAppNameClick onAppNameClick;

    public AppUpdateListViewAdapter(Context context, List<SmbFile> data, OnAppNameClick onAppNameClick) {
        this.context = context;
        this.data = data;
        this.onAppNameClick = onAppNameClick;
    }

    public void setData(List<SmbFile> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_app_update_list, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tv_app_name.setText(data.get(position).getName());
        holder.tv_app_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAppNameClick.onClick(data.get(position));
            }
        });
    }

    public interface OnAppNameClick {
        void onClick(SmbFile smbFile);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_app_name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
        }
    }
}
