package com.findtech.threePomelos.musicserver.proxy.utils;

import android.os.StatFs;

import com.findtech.threePomelos.music.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Administrator
 */
public class ProxyUtils {

    /**
     * 删除多余的缓存文件
     *
     * @param maximun 缓存文件的最大数量
     */
    static protected void asynRemoveBufferFile(final int maximun) {
        new Thread() {
            @Override
            public void run() {
                List<File> lstBufferFile = getFilesSortByDate(Constants.DOWNLOAD_PATH);
                while (lstBufferFile.size() > maximun) {
                    lstBufferFile.get(0).delete();
                    lstBufferFile.remove(0);
                }
            }
        }.start();
    }

    /**
     * 外部储存控件过小时删除缓存文件
     */

    static protected boolean removeBufferFile() {

                List<File> listBufferFile = getFilesSortByDate(Constants.DOWNLOAD_PATH);
                L.e("ProxyFileUtils",listBufferFile.size()+"=");
                if (listBufferFile.size() == 0){
                    return  false;
                }
               if (  listBufferFile.size() >5){
                   for (int i=0 ;i<3;i++){
                       listBufferFile.get(i).delete();
                       listBufferFile.remove(i);
                       L.e("ProxyFileUtils","listBufferFile.remove=>5"+listBufferFile.size());
                   }
                   return true;
               }else {
                   for (int i=0 ;i<listBufferFile.size();i++){
                       listBufferFile.get(i).delete();
                       listBufferFile.remove(i);
                       L.e("ProxyFileUtils","listBufferFile.remove=>5");
                   }
                   return true;
               }


    }


    /**
     * 获取外部存储器可用的空间
     *
     * @return
     */
    static public long getAvailaleSize(String dir) {
        StatFs stat = new StatFs(dir);
        long totalBlocks = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取文件夹内的文件，按日期排序，从旧到新
     *
     * @param dirPath
     * @return
     */
    static private List<File> getFilesSortByDate(String dirPath) {
        List<File> result = new ArrayList<File>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        L.e("ProxyUtils",dir.list().length+"=="+dir.listFiles().length);
        if (files == null || files.length == 0) {
            return result;
        }

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        Collections.addAll(result, files);
        return result;
    }

    public static String getExceptionMessage(Exception ex) {
        StringBuilder result = new StringBuilder();
        StackTraceElement[] stes = ex.getStackTrace();

        for (int i = 0; i < stes.length; i++) {
            result.append(stes[i].getClassName()).append(".").append(stes[i].getMethodName()).append("  ")
                    .append(stes[i].getLineNumber()).append("line").append("\r\n");
        }
        return result.toString();
    }

}
