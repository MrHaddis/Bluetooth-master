package com.haier.fridge.bletest_phone.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haier.fridge.bletest_phone.R;
import com.haier.fridge.bletest_phone.help.Constants;
import com.haier.fridge.bletest_phone.ui.adapter.BlueAdapter;
import com.haier.fridge.bletest_phone.utils.bluetooth.BltConstant;
import com.haier.fridge.bletest_phone.utils.bluetooth.BltManager;
import com.haier.fridge.bletest_phone.utils.bluetooth.BluetoothChatService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mBlueDeviceList;
    private BlueAdapter mBlueAdapter;
    private ListView mListView;
    private BluetoothChatService mChatService;
    private Button mButtonSwtich;
    private Button mButtonSend;
    private TextView mTextViewBlueStatus;
    private TextView mTextViewSend;
    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
        initBlue();
        setListener();
    }


    private void initView() {
        mListView = findViewById(R.id.lv_main);
        mButtonSwtich = findViewById(R.id.btn_ble_bluetooth);
        mButtonSend = findViewById(R.id.btn_ble_send);
        mTextViewBlueStatus = findViewById(R.id.tv_ble_bluetooth_status);
        mTextViewSend = findViewById(R.id.tv_ble_send);
        mTextViewResult = findViewById(R.id.tv_ble_result);


        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void initDate() {
        mBlueDeviceList = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mTextViewBlueStatus.setText("蓝牙已关闭");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1111);
        } else {
            mTextViewBlueStatus.setText("蓝牙已开启");
        }

        mBlueAdapter = new BlueAdapter(this, mBlueDeviceList);
        mListView.setAdapter(mBlueAdapter);
    }

    private void initBlue() {
        removePairDevice();
        BltManager.getInstance().initBltManager(this);
        BltManager.getInstance().checkBleDevice(this);
        BltManager.getInstance().registerBltReceiver(this, registerBltReceiver);
        BltManager.getInstance().registerBltReceiver2(this, registerBltFinishReceiver);
        BltManager.getInstance().clickBlt(this, BltConstant.BLUE_TOOTH_CLEAR);
        BltManager.getInstance().clickBlt(this, BltConstant.BLUE_TOOTH_SEARCH);
        mChatService = new BluetoothChatService(MainActivity.this, mHandler);
        mChatService.start();
    }

    private void setListener() {
        mListView.setOnItemClickListener(this);

        mButtonSwtich.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (RESULT_OK == resultCode) {
                Log.d("MainActivity", "开启成功");
                mTextViewBlueStatus.setText("蓝牙已开启");
            } else {
                Log.d("MainActivity", "开启失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://搜索蓝牙
                    break;
                case 2://蓝牙可以被搜索
                    break;
                case 3://设备已经接入
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    Log.i("blu", "设备已接入");
                    break;
                case 4://已连接某个设备
                    BluetoothDevice device1 = (BluetoothDevice) msg.obj;
                    Log.i("blu", "已连接" + device1.getName());
                    break;
                case 5:
                    break;
                case 6:
                    BluetoothDevice device2 = (BluetoothDevice) msg.obj;
                    Log.e("blu", device2.getName() + "连接失败");
                    break;
                default:
                    break;
            }
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("Dyx", readMessage);
                    mTextViewResult.setText(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    break;
                case Constants.MESSAGE_TOAST:
                    break;
            }
        }
    };

    //配对
    private BltManager.OnRegisterBltReceiver registerBltReceiver = new BltManager.OnRegisterBltReceiver() {
        @Override
        public void onBluetoothDevice(BluetoothDevice device) {
            if (mBlueDeviceList != null && !mBlueDeviceList.contains(device)) {
                mBlueDeviceList.add(device);
                Log.e("onBluetoothDevice", device.getName() + "- new");
            } else {
                Log.e("onBluetoothDevice", device.getName() + "- exists");
            }
            if (mBlueAdapter != null) {
                mBlueAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBltMatchDevice(BluetoothDevice device) {
            if (mBlueDeviceList != null && !mBlueDeviceList.contains(device)) {
                mBlueDeviceList.add(device);
                Log.e("onBltMatchDevice", device.getName() + "- new");
            } else {
                Log.e("onBltMatchDevice", device.getName() + "- exists");
            }
        }

        @Override
        public void onBltIng(BluetoothDevice device) {

        }

        @Override
        public void onBltEnd(BluetoothDevice device) {

        }

        @Override
        public void onBltNone(BluetoothDevice device) {

        }
    };

    //扫描结果
    private BltManager.OnRegisterBltFinishReceiver registerBltFinishReceiver = new BltManager.OnRegisterBltFinishReceiver() {
        @Override
        public void onBlueFinish() {
            Log.d("MainActivity", "完成");
        }

        @Override
        public void onOpenBlu() {
            Log.d("MainActivity", "开启蓝牙");
            mTextViewBlueStatus.setText("蓝牙已开启");
        }

        @Override
        public void onCloseBlu() {
            Log.d("MainActivity", "关闭蓝牙");
            mTextViewBlueStatus.setText("蓝牙已关闭");
        }

        @Override
        public void onOpening() {
            mTextViewBlueStatus.setText("蓝牙正在开启");
        }

        @Override
        public void onClosing() {
            mTextViewBlueStatus.setText("蓝牙正在关闭");
        }
    };

    //得到配对的设备列表，清除已配对的设备
    public void removePairDevice() {
        if (mBluetoothAdapter != null) {
            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                unpairDevice(device);
            }
        }
    }

    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d("MainActivity", "" + mBlueDeviceList.get(position).getName());
        //使用True 是因为 每次都会清除所有的配对记录 所有 每次连接都是新设备  所以用true，如果是已经配对过的设备用false就行
        mChatService.connect(mBlueDeviceList.get(position), true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ble_bluetooth:
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    mBlueDeviceList.clear();
                    mBlueAdapter.notifyDataSetChanged();
                    if (mChatService != null) {
                        mChatService.stop();
                    }
                } else if (!mBluetoothAdapter.isEnabled()) {//未开启
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1111);
                    initBlue();
                }
                break;
            case R.id.btn_ble_send:
                sendMessage("true");
                break;
        }
    }

    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.d("BleTestActivity", "发送失败:" + message);
            mTextViewSend.setText("发送指令失败");
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            Log.d("BleTestActivity", "发送成功:" + message);
            mTextViewSend.setText("发送指令成功");

        }
    }
}
