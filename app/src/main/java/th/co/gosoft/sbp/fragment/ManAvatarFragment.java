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
public class ManAvatarFragment extends Fragment {

    private final String LOG_TAG = "ManAvatarFragment";
    private List<Integer> manAvatarList = new ArrayList<>();
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
            View view = inflater.inflate(R.layout.fragment_man_avatar, container, false);
            gridViewAvatarPic = (GridView) view.findViewById(R.id.gridMan);
            avatarPicAdapter = new AvatarPicAdapter(getActivity(), R.layout.avatar_grid, manAvatarList);
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
        Log.i(LOG_TAG, "onResumes man");
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
                passData(manAvatarList.get(position));
            }
        });
    }

    private void generateAvatarImageList() {
        manAvatarList.add(R.drawable.man01);
        manAvatarList.add(R.drawable.man02);
        manAvatarList.add(R.drawable.man03);
        manAvatarList.add(R.drawable.man04);
        manAvatarList.add(R.drawable.man05);
        manAvatarList.add(R.drawable.man06);
        manAvatarList.add(R.drawable.man07);
        manAvatarList.add(R.drawable.man08);
        manAvatarList.add(R.drawable.man09);
        manAvatarList.add(R.drawable.man10);
        manAvatarList.add(R.drawable.man11);
        manAvatarList.add(R.drawable.man12);
        manAvatarList.add(R.drawable.man13);
        manAvatarList.add(R.drawable.man14);
    }

    public void passData(int data) {
        dataPasser.onDataPass(getResources().getResourceName(data));
    }


}
