package com.hfhuaizhi.permissionlib.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.core.content.ContextCompat;

import java.io.Serializable;

public class RequestPermissionUtil {
    private static volatile RequestPermissionUtil instance;
    private Messenger mMessenger;
    private Context mContext;
    private OnPermissionListener mListener;
    private String[] mPermissionList;

    public static RequestPermissionUtil getInstance() {
        if (instance == null) {
            synchronized (RequestPermissionUtil.class) {
                if (instance == null) {
                    instance = new RequestPermissionUtil();
                }
            }
        }
        return instance;
    }

    private RequestPermissionUtil() {
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mListener != null && msg.getData().get(RequestPermissionService.RESP_RESULT) != null) {
                mListener.onPermissionResult((Boolean) msg.getData().get(RequestPermissionService.RESP_RESULT));
            }
            mContext.unbindService(mServiceConnection);
            return true;
        }
    });

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            Message mMessage = Message.obtain(null, RequestPermissionService.MSG_FROMCLIENT);
            mMessage.replyTo = new Messenger(mHandler);
            mMessage.obj = mPermissionList;
            try {
                mMessenger.send(mMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void requestPermissions(Context context, String[] permissionArr, OnPermissionListener listener) {
        if (context == null || listener == null) {
            return;
        }
        mContext = context;
        mListener = listener;
        mPermissionList = permissionArr;
        if (Build.VERSION.SDK_INT <= 22 || allOpen(permissionArr)) {
            mListener.onPermissionResult(true);
        } else {
            Intent intent = new Intent(mContext, RequestPermissionService.class);
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private boolean allOpen(String[] permissionArr) {
        if (permissionArr == null || permissionArr.length == 0) {
            return true;
        }
        for (String str : permissionArr) {
            if (ContextCompat.checkSelfPermission(mContext, str) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface OnPermissionListener extends Serializable {

        void onPermissionResult(boolean success);

    }
}