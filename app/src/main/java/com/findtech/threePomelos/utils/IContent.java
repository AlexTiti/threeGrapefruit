package com.findtech.threePomelos.utils;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alex
 * @date 2017/5/3
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public class IContent {

    private static IContent iContent = new IContent();

    private IContent() {
    }

    public static IContent getInstacne() {
        return iContent;
    }

    public static final int[] TABTEXT_IDS = new int[]{R.string.page_tab1, R.string.page_tab2, R.string.page_tab3, R.string.page_tab4};
    public static final String FILEM_USIC = "Music";
    public static final String UPDATE = "Update";
    public static final String IS_FIRST_USE = "isFirstUse";
    public static final String IS_VERSION = "is_version";
    public static final String VERSION_UPDATE = "version_update";
    public static final String APP_UPDATE = "app_update";
    public static final String MEI_ZU = "Meizu";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String NUMBER = "number";
     public static final String SINGLE_CLICK = "com.findtech.threePomelos.playdetail";
    public static final String DOUBLE_CLICK = "com.findtech.threePomelos.stopplay";

    /**
     * ---------------------------------------------  BlueTooth------------------------------------------------
     */
    public static final String SERVERUUID_PRE = "ffe0";
    public static final String CHARACTERUUID_PRE = "ffe4";
    public static final String SERVERUUID_BLE = "a032";
    public static final String CHARACTERUUID_BLE = "a042";
    public static final String WRITEUUID_BLE = "a040";
    public static final String READUUID_BLE = "a041";
    public static final long TEN_YEAR = 315619200000L ;

    public static boolean isBLE = true;
    public static byte[] BLUEMODE = {0x55, (byte) 0xAA, 0x01, 0x01, 0x02, 0x03, (0 - (4 + 0x03))};
    public static byte[] NOTIFY_DATA = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x0c, (byte) 0xE9};
    public static byte[] CLOSE_DEVICE = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0xAA, (byte) 0x4B};
    public static byte[] BRAKE_CAR = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x05, (byte) 0xF0};
    public static byte[] BRAKE_CAR_CLEAR = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x06, (byte) 0xEF};
    public static byte[] REPAIR_CAR = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x55, (byte) 0xA0};

    /**
     *  8101
     */
    public static byte[] CODE = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x0B, (byte) 0xEA};
    public static byte[] BREAK_MODEL_CLOSE = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x0F, 0x00, (byte) (0 - (0x01 + 0x0B + 0x0F))};
    public static byte[] BREAK_MODEL_AUTO = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x0F, 0x01, (byte) (0 - (0x01 + 0x0B + 0x0F + 0x01))};
    public static byte[] BREAK_MODEL_AI = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x0F, 0x02, (byte) (0 - (0x01 + 0x0B + 0x0F + 0x02))};
    public static byte[] TEM = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x11, (byte) 0xE4};



    public byte[] WRITEVALUE;
    public String address = null;
    public String deviceName = null;
    public String functionType = null;
    public String company = null;

    public Map<String, MusicInfo> map = new HashMap<>();
    public List<String> collectedList = new ArrayList<>();
    public ArrayList<BluetoothLinkBean> bluetoothLinkBeen = new ArrayList<>();
    public String clickPositionAddress = "clickPositionAddress";
    public String clickPositionName = "clickPositionName";
    public String clickPositionFunction = "clickPositionFunction";
    public String newCode = null;
    public String newVersion = null;
    public String clickPositionType = null;
    public boolean isBind = false;
    public String code = "V1.0.1";
    public boolean SD_Mode = false;
    public static boolean isSended = false;
    public ArrayList<DeviceCarBean> addressList = new ArrayList<>();
    public static boolean isModePlay = true;
    public static final String ACTION_PLAY_OR_PAUSE = "ACTION_PLAY_OR_PAUSE";
    public static String MUSIC_NAME = null;

    public static final String TRAVEL_FRAGMRNT = "travel_fragment";

    public static final String USER_LOCATION = "user_location";
}
