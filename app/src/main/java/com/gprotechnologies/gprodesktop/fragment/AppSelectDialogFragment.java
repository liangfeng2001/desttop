package com.gprotechnologies.gprodesktop.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gprotechnologies.gprodesktop.activity.MainActivity;
import com.gprotechnologies.gprodesktop.bean.AppInfo;
import com.gprotechnologies.gprodesktop.utils.AppUtils;

import java.util.List;

/**
 * @Description
 * @auther Kason
 * @create 2019-12-18 14:12
 */
public class AppSelectDialogFragment extends DialogFragment {

    private List<AppInfo> appList;

    private AppSelectDialogFragment() {
    }

    private static AppSelectDialogFragment appSelectDialogFragment = new AppSelectDialogFragment();;

    public static void show(MainActivity mainActivity) {
        appSelectDialogFragment.show(mainActivity.getFragmentManager(), "appSelectDialog");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         appList = AppUtils.getAppList(getActivity(), "(com.android.calculator2)|(android.rk.RockVideoPlayer)");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView listView = new ListView(getActivity());
        ArrayAdapter<AppInfo> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, appList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppUtils.launchApp(getActivity(),appList.get(position));
            }
        });
        return listView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appSelectDialogFragment.getDialog().setTitle("请选择应用");
    }
}
