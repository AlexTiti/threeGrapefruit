package com.findtech.threePomelos.musicserver.proxy.utils;

import android.content.Context;

import com.findtech.threePomelos.music.utils.L;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * 代理
 * @author Administrator
 */
public class MediaPlayerProxy implements Runnable {
    private static final String LOG_TAG = MediaPlayerProxy.class.getSimpleName();
    private int port;
    private ServerSocket socket;
    private Thread thread;

    private boolean isRunning = true;

    protected static RequestDealThread downloadThread;
    private Context mContext;

    public MediaPlayerProxy(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 创建ServerSocket，使用自动分配端口
     */
    public void init() {
        try {
            socket = new ServerSocket(port, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            socket.setSoTimeout(5000);
            port = socket.getLocalPort();

        } catch (UnknownHostException e) {

        } catch (IOException e) {

        }
    }

    public void start() {
        if (socket == null) {
            throw new IllegalStateException("Cannot start proxy; it has not been initialized.");
        }
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        if (thread == null) {
            throw new IllegalStateException("Cannot stop proxy; it has not been started.");
        }
        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String getProxyURL(String url) {
        return String.format(Locale.getDefault(), "http://127.0.0.1:%d/%s", port, url);
    }

    @Override
    public void run() {

        while (isRunning) {
            try {
                final Socket client = socket.accept();
                if (client == null) {
                    continue;
                }
                HttpURLConnection request = readRequest(client);
                if (request != null) {
                    downloadThread = new RequestDealThread(mContext, request, client);
                    downloadThread.start();
                }
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
            }
        }
    }

    void http() {
        URLConnection rulConnection = null;
        try {
            URL url = new URL("http://localhost:8080/TestHttpURLConnectionPro/index.jsp");

            rulConnection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 此处的urlConnection对象实际上是根据URL的
        // 请求协议(此处是http)生成的URLConnection类
        // 的子类HttpURLConnection,故此处最好将其转化
        // 为HttpURLConnection类型的对象,以便用到
        // HttpURLConnection更多的API.如下:

        HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;

    }

    private HttpURLConnection readRequest(Socket client) {
        // 得到Request String
        HttpURLConnection request = null;
        int bytes_read;
        byte[] local_request = new byte[1024];
        StringBuilder requestStr = new StringBuilder();
        try {
            while ((bytes_read = client.getInputStream().read(local_request)) != -1) {
                byte[] tmpBuffer = new byte[bytes_read];
                System.arraycopy(local_request, 0, tmpBuffer, 0, bytes_read);
                String str = new String(tmpBuffer);
                L.e(LOG_TAG + " Header-> ", str + "str");
                requestStr.append(str);
                if (requestStr.toString().contains("GET") && requestStr.toString().contains(Constants.HTTP_END)) {
                    break;
                }
            }
        } catch (IOException e) {
            L.e(LOG_TAG, "获取Request Header异常");
            return request;
        }

        if (requestStr.toString() == "") {
            L.e(LOG_TAG, "请求头为空，获取异常");
            return request;
        }

        // 将Request String组合为HttpUriRequest
        String[] requestParts = requestStr.toString().split(Constants.LINE_BREAK);
        StringTokenizer st = new StringTokenizer(requestParts[0]);
        String method = st.nextToken();
        String uri = st.nextToken();

        L.e(LOG_TAG + " URL-> ", uri);
        URLConnection rulConnection = null;
        try {
            URL url = new URL(uri.substring(1));
            rulConnection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 此处的urlConnection对象实际上是根据URL的
        // 请求协议(此处是http)生成的URLConnection类
        // 的子类HttpURLConnection,故此处最好将其转化
        // 为HttpURLConnection类型的对象,以便用到
        // HttpURLConnection更多的API.如下:

        request = (HttpURLConnection) rulConnection;


        for (int i = 1; i < requestParts.length; i++) {
            int separatorLocation = requestParts[i].indexOf(":");
            String name = requestParts[i].substring(0, separatorLocation).trim();
            String value = requestParts[i].substring(separatorLocation + 1).trim();
            // 不添加Host Header，因为URL的Host为127.0.0.1
            if (!name.equals(Constants.HOST)) {
                try {
                    request.setRequestProperty(name, value);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        // 如果没有Range，统一添加默认Range,方便后续处理

        try {
            request.setRequestProperty(Constants.RANGE, Constants.RANGE_PARAMS_0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return request;
    }
}