package com.findtech.threePomelos.musicserver.control;

/**
 *
 * @author wm
 * @date 2016/12/23
 */
public interface MusicStateListener {

    /**
     * 更新歌曲状态信息
     */
     void updateTrackInfo();

    /**
     * 更新歌曲时间
     */
     void updateTime();
    /**
     * 更新歌曲主题
     */
     void changeTheme();
    /**
     * 重新加载
     */
     void reloadAdapter();
}
