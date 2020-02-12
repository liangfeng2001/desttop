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
    boolean enableUninstall = false;

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

        if (enableUninstall) {
            holder.iv_del_img.setVisibility(View.VISIBLE);
        } else {
            holder.iv_del_img.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enableUninstall = !enableUninstall;
                notifyDataSetChanged();
                return true;
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

    public void initAppList(List<AppInfo> appList) {
        this.appInfos = appList;
        notifyDataSetChanged();
    }

    public interface OnItemCLickListener {
        void onItemClick(AppInfo appInfo);
    }

    public boolean isEnableUninstall() {
        return enableUninstall;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tv_app_name;
        ImageView iv_app_img;
        ImageView iv_del_img;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            iv_app_img = itemView.findViewById(R.id.iv_app_img);
            iv_del_img = itemView.findViewById(R.id.iv_del_img);
        }
    }
}
