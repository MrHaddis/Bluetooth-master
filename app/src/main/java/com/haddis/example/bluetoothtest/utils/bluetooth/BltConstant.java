package com.haier.fridge.bletest_phone.utils.bluetooth;

import java.util.UUID;

/**
 * Created by haddis on 2018/4/15.
 */
public class BltConstant {

    /**
     * 蓝牙UUID
     */
    public static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //启用蓝牙
    public static final int BLUE_TOOTH_OPEN = 1000;
    //禁用蓝牙
    public static final int BLUE_TOOTH_CLOSE = 1001;
    //搜索蓝牙
    public static final int BLUE_TOOTH_SEARCH = 1002;
    //被搜索蓝牙
    public static final int BLUE_TOOTH_MY_SEARCH = 1003;
    //关闭蓝牙连接
    public static final int BLUE_TOOTH_CLEAR = 1004;
}
