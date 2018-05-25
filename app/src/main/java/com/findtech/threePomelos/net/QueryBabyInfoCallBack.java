package com.findtech.threePomelos.net;


/**
 * 查询回调接口
 * @author Administrator
 */
public interface QueryBabyInfoCallBack {

    /**
     * 查询完成回调
     */
    void finishQueryAll();

    interface QueryIsBind {
        /**
         * 登陆后查询结束回调
         * @param isBind
         * @param deviceId
         */
        void finishQueryIsBind(boolean isBind, String deviceId);
    }

    interface SaveIsBind {
        /**
         * 保存成功
         * @param isBind
         * @param deviceId
         */
        void finishSaveIsBindSuccess(boolean isBind, String deviceId);

        /**
         * 保存失败
         */
        void finishSaveIsBindFail();
    }
}