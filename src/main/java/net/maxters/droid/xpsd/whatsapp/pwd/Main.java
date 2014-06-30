package net.maxters.droid.xpsd.whatsapp.pwd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import net.maxters.whatsapp.decoder.PwDecoder;
import net.maxters.whatsapp.decoder.WPResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import eu.chainfire.libsuperuser.Shell;

public class Main extends Activity {

    private String user;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.read_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPwd();
            }
        });
        refresh();

    }

    private void refresh() {
        SharedPreferences prf = getSharedPreferences("settings", MODE_PRIVATE);
        user = prf.getString("u", null);
        pwd = prf.getString("p", null);
        if (user == null || pwd == null) {
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showError("No credentials detected");
                }
            });
        } else {
            ((TextView) findViewById(R.id.user)).setText(user);
            ((TextView) findViewById(R.id.pass)).setText(pwd);
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String data = "phone=" + user + "\npassword=" + pwd;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });
        }
    }

    private void showError(String error) {
        ((TextView) findViewById(R.id.error)).setText(error);
    }


    private void readPwd() {
        PwdTask t = new PwdTask() {
            @Override
            protected void onPostExecute(WPResult wpResult) {
                super.onPostExecute(wpResult);

            }
        };
        t.execute();

    }


    private class PwdTask extends AsyncTask<Void, Void, WPResult> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Main.this);
            pd.setMessage("extracting");
            pd.show();
        }

        @Override
        protected void onPostExecute(WPResult wpResult) {
            super.onPostExecute(wpResult);
            try {
                pd.dismiss();
                if(wpResult==null){
                    showError("Could not read from files");
                    return;
                }
                String u = wpResult.id;
                String p = Base64.encodeToString(wpResult.passwd, Base64.DEFAULT);;
                SharedPreferences prf = getSharedPreferences("settings", Context.MODE_PRIVATE);
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
                    refresh();
                }
            } catch (Exception e) {
            }
            ;
        }

        @Override
        protected WPResult doInBackground(Void... params) {
            File pwd = new File(getFilesDir(), "pw");
            if (pwd.exists()) pwd.delete();
            File me = new File(getFilesDir(), "me");
            if (me.exists()) me.delete();
            String[] cmd = new String[]{"cp /data/data/com.whatsapp/files/pw " + pwd.getAbsolutePath(),
                    "cp /data/data/com.whatsapp/files/me " + me.getAbsolutePath(),
                    "chmod 777 " + me.getAbsolutePath(),
                    "chmod 777 " + pwd.getAbsolutePath()};
            Shell.SU.run(cmd);
            try {
                FileInputStream ps = new FileInputStream(pwd);
                FileInputStream ms = new FileInputStream(me);
                return PwDecoder.decrypt(ms, ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                cmd = new String[]{"rm " + pwd.getAbsolutePath(),
                        "rm " + me.getAbsolutePath()};
                Shell.SU.run(cmd);
            }
            return null;
        }
    }


}
