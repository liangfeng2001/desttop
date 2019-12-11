package com.gprotechnologies.gprodesktop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gprotechnologies.gprodesktop.activity.MainActivity;
import com.gprotechnologies.gprodesktop.R;
import com.gprotechnologies.gprodesktop.bean.AppInfo;

import java.util.List;

/**
 * @Description
 * @auther Kason
 * @create 2019-12-11 9:43
 */
public class AppRecycleViewAdapter extends RecyclerView.Adapter<AppRecycleViewAdapter.MyHolder> {

    List<AppInfo> appInfos;
    Context context;
    OnItemCLickListener onItemCLickListener;

    public AppRecycleViewAdapter(List<AppInfo> appInfos, MainActivity mainActivity) {
        this.appInfos = appInfos;
        this.context = mainActivity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_recycle_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final AppInfo appInfo = appInfos.get(position);
        holder.tv_app_name.setText(appInfo.getName());
        holder.iv_app_img.setImageDrawable(appInfo.getIco());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCLickListener != null) {
                    onItemCLickListener.onItemClick(appInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        this.onItemCLickListener = listener;
    }

    public interface OnItemCLickListener {
        void onItemClick(AppInfo appInfo);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_app_name;
        ImageView iv_app_img;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            iv_app_img = itemView.findViewById(R.id.iv_app_img);
        }
    }
}
