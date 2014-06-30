package net.maxters.droid.xpsd.whatsapp.pwd;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Base64;

import net.maxters.droid.xposed.hook.XPosedHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by nizam on 1/25/14.
 */
public abstract class WPHook extends XPosedHook {
    @Override
    public String getApplicationClass() {
        return "com.whatsapp.App";
    }

    public WPHook(String version) {
        super("com.whatsapp", version);
    }

    public String getPwdMethod() {
        return "c";
    }

    public String getPwdClass() {
        return "com.whatsapp.jj";
    }

    public String getUserMethod() {
        return "zb";
    }



    @Override
    public void afterAppCreate(XC_MethodHook.MethodHookParam param) {
        super.afterAppCreate(param);
        String[] data = null;
        data = runMethods(getApplicationClass(), getUserMethod(), getPwdClass(), getPwdMethod(), param.thisObject);
        if (data != null && data.length == 2 && data[0] != null && data[1] != null) {
            Intent i = new Intent("net.maxters.droid.SHOW_PWD");
            i.setComponent(new ComponentName("net.maxters.droid.xpsd.whatsapp.pwd", "net.maxters.droid.xpsd.whatsapp.pwd.PasswdRcvr"));
            i.putExtra("u", data[0]);
            i.putExtra("p", data[1]);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ((Application) param.thisObject).sendBroadcast(i);
        }
    }

    private String[] runMethods(String appCLass, String userMethod, String pwdClass, String pwdMethod, Object obj) {
        String[] data = new String[2];
        Method mPass = null;
        Method mUser = null;
        String user;
        mPass = XposedHelpers.findMethodExact(pwdClass, getClassLdr(), pwdMethod, String.class);
        mUser = XposedHelpers.findMethodExact(appCLass, getClassLdr(), userMethod);
        try {
            user = (String) mUser.invoke(obj);
            Object pout = mPass.invoke(null, user);
            byte[] d = (byte[]) pout;
            String s = Base64.encodeToString(d, Base64.DEFAULT);
            data[0] = user;
            data[1] = s;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return data;
    }

}
