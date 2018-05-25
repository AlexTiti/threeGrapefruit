package com.findtech.threePomelos.music.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.sdk.base.recycler.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/08
 */

public class SixMusicAdapter extends BaseRecyclerViewAdapter<MusicInfo, BaseViewHolder> {

    private Animation animation;

    public SixMusicAdapter(int layoutResId, @Nullable List<MusicInfo> data) {
        super(layoutResId, data);
    }

    public SixMusicAdapter(int layoutResId) {
        super(layoutResId);
        initAnimation();
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {

        helper.setText(R.id.text_name, item.musicName)
                .setText(R.id.text_number, String.valueOf(helper.getAdapterPosition() + 1));

        String downing = "downing";
        if (downing.equals(item.artist)) {
            helper.setImageResource(R.id.image_down, R.drawable.iconload);
            View view = helper.getView(R.id.image_down);
            view.setClickable(false);
            if (animation != null) {
                view.setAnimation(animation);
                animation.start();
            }
        } else if (item.islocal) {
            helper.setImageResource(R.id.image_down, R.drawable.icon_downed);
            helper.getView(R.id.image_down).setClickable(false);
            if (animation != null ){
                animation.cancel();
                helper.getView(R.id.image_down).clearAnimation();
            }
        } else {
            helper.setImageResource(R.id.image_down, R.drawable.icon_down);
            helper.getView(R.id.image_down).setClickable(true);
            helper.addOnClickListener(R.id.image_down);
            if (animation != null ){
                animation.cancel();
                helper.getView(R.id.image_down).clearAnimation();
            }
        }

        if (MusicPlayer.getCurrentAudioId() == item.songId) {
            helper.setVisible(R.id.text_number, false);
            helper.setVisible(R.id.image_sound, true);
            helper.setImageResource(R.id.image_sound, R.drawable.song_play_icon);
        } else {
            helper.setVisible(R.id.text_number, true);
            helper.setVisible(R.id.image_sound, false);
        }

    }

    private void initAnimation() {
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setRepeatCount(-1);
    }
}
