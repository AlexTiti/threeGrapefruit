package com.findtech.threePomelos.utils.glide;


import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.findtech.threePomelos.R;

/**
 * @author :   Alex
 * @version :   V 1.0.9
 * @e_mail :   18238818283@sina.cn
 * @time :   2018/03/15
 * @desc :
 */

public class GlideUtils {

    public static RequestOptions getGifOptions() {
        return RequestOptions.placeholderOf(R.drawable.icon_rhyme)
                .error(R.drawable.icon_rhyme)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fitCenter();
    }

    public static RequestOptions simpleOptions(){

        return RequestOptions.placeholderOf(R.mipmap.ic_launcherp)
                .error(R.drawable.ic_vector_net_error)
                .fitCenter();
    }

}
