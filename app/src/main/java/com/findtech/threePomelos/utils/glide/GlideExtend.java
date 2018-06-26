package com.findtech.threePomelos.utils.glide;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.findtech.threePomelos.R;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/06/01
 */

@GlideExtension
public final class GlideExtend extends AppGlideModule {

    private GlideExtend() {
    }

    @GlideOption
    public static void simple(RequestOptions options) {
        options.centerCrop().placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);
    }

    @GlideOption
    public static void simpleFit(RequestOptions options) {
        options.fitCenter().placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

    }


    @GlideOption
    public static void simpleMusic(RequestOptions options) {
        options.centerCrop().placeholder(R.drawable.face_music_car_a)
                .error(R.drawable.face_music_car_a);

    }



}
