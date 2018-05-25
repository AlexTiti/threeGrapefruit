package com.findtech.threePomelos.music.info;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/09
 */

public class MusicBean {
    private String imageUrl;
    private String name;
    private String numberPlayed;
    private String numberMusic;

    public MusicBean(String imageUrl, String name, String numberPlayed, String numberMusic) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.numberPlayed = numberPlayed;
        this.numberMusic = numberMusic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getNumberPlayed() {
        return numberPlayed;
    }

    public String getNumberMusic() {
        return numberMusic;
    }
}
