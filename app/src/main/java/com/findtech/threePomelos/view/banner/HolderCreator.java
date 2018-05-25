package com.findtech.threePomelos.view.banner;




/**
 * @author Administrator
 */
public interface HolderCreator<VH extends ViewHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
    public VH createViewHolder();
}
