package net.maxters.droid.xpsd.whatsapp.pwd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PasswdRcvr extends BroadcastReceiver {
    public PasswdRcvr() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String u = intent.getStringExtra("u");
        String p = intent.getStringExtra("p");
        SharedPreferences prf = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String user = prf.getString("u", null);
        String pwd = prf.getString("p", null);
        if(!(p==null||u==null)){
            p = p.trim();
            u = u.trim();
            if(user==null|| pwd==null){
                prf.edit().putString("u",u).putString("p", p).commit();
            }else{
                user = user.trim();
                pwd = pwd.trim();
                if(u.equalsIgnoreCase(user)&&p.equals(pwd)){
                    return;
                }else{
                    prf.edit().putString("u",u).putString("p",p).commit();
                }
            }
            Intent i = new Intent(context,Main.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(i);
        } 
    }
}
