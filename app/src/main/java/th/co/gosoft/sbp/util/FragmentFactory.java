package th.co.gosoft.sbp.util;

import android.app.Fragment;

import th.co.gosoft.sbp.fragment.BoardContentFragment;
import th.co.gosoft.sbp.fragment.SelectRoomFragment;

public class FragmentFactory {

    public FragmentFactory() {
    }

    public Fragment getFactory(String key){
        if(key.equals("selectRoom")){
            return new SelectRoomFragment();
        }
        if(key.equals("boardContent")){
            return new BoardContentFragment();
        }
        return null;
    }
}
