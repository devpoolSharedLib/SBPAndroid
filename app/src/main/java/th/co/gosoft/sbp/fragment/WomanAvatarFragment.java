package th.co.gosoft.sbp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import th.co.gosoft.sbp.R;
import th.co.gosoft.sbp.adapter.AvatarPicAdapter;
import th.co.gosoft.sbp.util.OnDataPass;

/**
 * Created by manitkan on 06/06/16.
 */
public class WomanAvatarFragment extends Fragment {

    private final String LOG_TAG = "WomanAvatarFragment";
    private List<Integer> womanAvatarList = new ArrayList<>();
    private GridView gridViewAvatarPic;
    private OnDataPass dataPasser;
    private AvatarPicAdapter avatarPicAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try{
            View view = inflater.inflate(R.layout.fragment_woman_avatar, container, false);
            gridViewAvatarPic = (GridView) view.findViewById(R.id.gridWoman);
            avatarPicAdapter = new AvatarPicAdapter(getActivity(), R.layout.avatar_grid, womanAvatarList);
            generateAvatarImageList();
            generateGridView();
            return view;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResumes woman");
        gridViewAvatarPic.clearChoices();
        avatarPicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    private void generateGridView(){
        gridViewAvatarPic.setAdapter(avatarPicAdapter);
        gridViewAvatarPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                passData(womanAvatarList.get(position));
            }
        });
    }

    private void generateAvatarImageList() {
        womanAvatarList.add(R.drawable.girl01);
        womanAvatarList.add(R.drawable.girl02);
        womanAvatarList.add(R.drawable.girl03);
        womanAvatarList.add(R.drawable.girl04);
        womanAvatarList.add(R.drawable.girl05);
        womanAvatarList.add(R.drawable.girl06);
        womanAvatarList.add(R.drawable.girl07);
        womanAvatarList.add(R.drawable.girl08);
        womanAvatarList.add(R.drawable.girl09);
        womanAvatarList.add(R.drawable.girl10);
        womanAvatarList.add(R.drawable.girl11);
        womanAvatarList.add(R.drawable.girl12);
        womanAvatarList.add(R.drawable.girl13);
        womanAvatarList.add(R.drawable.girl14);
        womanAvatarList.add(R.drawable.girl15);
        womanAvatarList.add(R.drawable.girl16);
        womanAvatarList.add(R.drawable.girl17);
    }

    public void passData(int data) {
        dataPasser.onDataPass(getResources().getResourceName(data));
    }

}
