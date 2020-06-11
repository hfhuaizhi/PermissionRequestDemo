package com.hfhuaizhi.permissionlib.util;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class RequestPermissionService extends Service {
    public static final int MSG_FROMCLIENT = 1000;
    public static final String RESP_RESULT = "result";

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_FROMCLIENT:
                    final Messenger mMessenger = msg.replyTo;
                    String[] permissionList = (String[]) msg.obj;
                    RequestPermissionActivity.start(getApplicationContext(), permissionList, new RequestPermissionUtil.OnPermissionListener() {

                        @Override
                        public void onPermissionResult(boolean success) {
                            Message mMessage = Message.obtain(null, RequestPermissionService.MSG_FROMCLIENT);
                            Bundle mBundle = new Bundle();
                            mBundle.putBoolean(RESP_RESULT, success);
                            mMessage.setData(mBundle);
                            try {
                                mMessenger.send(mMessage);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;
            }
            return true;
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mHandler).getBinder();
    }
}
