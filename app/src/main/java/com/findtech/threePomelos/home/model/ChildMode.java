package com.findtech.threePomelos.home.model;


import com.findtech.threePomelos.music.fragment.MusicNewFragment;
import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.travel.fragment.TravelFragment;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2018/03/14
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public class ChildMode implements Contract.ModeMvp{

    public ArrayList<BaseCompatFragment> getTabArray(){

        ArrayList<BaseCompatFragment> strings = new ArrayList<>();
        strings.add(new TravelFragment());
        strings.add(new MusicNewFragment());
        return strings;
    }

}
