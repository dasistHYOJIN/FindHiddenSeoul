package com.seoulapp.findhiddenseoul.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulapp.findhiddenseoul.Activity.H_DetailActivity;
import com.seoulapp.findhiddenseoul.ListViewAdapter.ListViewAdapter1;
import com.seoulapp.findhiddenseoul.ApplicationController;
import com.seoulapp.findhiddenseoul.ListViewItem.ListViewItem;
import com.seoulapp.findhiddenseoul.NetworkService;
import com.seoulapp.findhiddenseoul.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by BANGYURA on 2017-10-26.
 */

public class ListFragment2 extends Fragment {
    ApplicationController applicationController;
    private NetworkService networkService;

    public ListFragment2()
    {
    }

    public static ListFragment2 newInstance(String guText, String cate){
        Bundle args = new Bundle();
        args.putString("guText", guText);
        args.putString("cate", cate);

        ListFragment2 fragment = new ListFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.listview, container, false);

        //final ListView listView;
        final ListView listView;

        //final ListViewAdapter adapter = new ListViewAdapter();
        final ListViewAdapter1 adapter = new ListViewAdapter1();

        final TextView tv = (TextView) layout.findViewById(R.id.listview_tv);

        applicationController = new ApplicationController();
        applicationController.onCreate();

        applicationController = ApplicationController.getInstance();
        applicationController.buildNetworkService();
        networkService = ApplicationController.getInstance().getNetworkService();

        listView = (ListView) layout.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //list item 클릭 시
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //pos = position;
                ListViewItem listViewItem = (ListViewItem) parent.getItemAtPosition(position);

                //다음 페이지로 넘어가기 위해 클릭된 메모의 index를 넘긴다.
                Bundle extras = new Bundle();
                extras.putString("heritage_index", listViewItem.getHeritage_index());

                Intent intent = new Intent(getActivity(), H_DetailActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
                Toast.makeText(getActivity(), listViewItem.getHeritage_index(), Toast.LENGTH_SHORT).show();
            }
        });

        Call<List<ListViewItem>> gu = networkService.getGu(getArguments().getString("guText"));
        gu.enqueue(new Callback<List<ListViewItem>>() {
            @Override
            public void onResponse(Call<List<ListViewItem>> call, Response<List<ListViewItem>> response) {
                if(response.isSuccessful()){
                    List<ListViewItem> listViewItem_temp = response.body();
                    for (int i = 0; i < listViewItem_temp.size(); i++) {
                        if(listViewItem_temp.get(i).getCategory().equals(getArguments().getString("cate")))
                            adapter.addItem(listViewItem_temp.get(i).getHeritage_mainImg(), listViewItem_temp.get(i).getHeritage_name(),
                                    listViewItem_temp.get(i).getLikes(), listViewItem_temp.get(i).getHeritage_index());
                    }
                    listView.setAdapter(adapter);

                    if(listView.getAdapter().getCount() == 0){
                        tv.setVisibility(View.VISIBLE);
                        tv.setTypeface(Typeface.createFromAsset(layout.getContext().getAssets(), "fonts/NotoSansKR-Medium.mp3"));
                        tv.setIncludeFontPadding(false);
                        tv.setTextSize(20);
                    }
                }else{
                    int statusCode = response.code();
                    Log.i("mytag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<ListViewItem>> call, Throwable t) {

            }
        });
        return layout;
    }
}