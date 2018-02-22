package com.wangw.xposeddemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {

    public static final String SP_NAME = "MY_CONFIG";
    public static final String KEY_DIR = "dir";
    public static final String KEY_PACKAGE = "package_name";

    ListView mListView;
    SharedPreferences mSp;
    List<PackageInfo> mApps;
    PackageManager mPm;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        checkSupport();
        mSp = getSharedPreferences(SP_NAME, 1);

        getApps();
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

    }

    private void getApps() {
        mPm = getPackageManager();
        mApps = new ArrayList<>();
        for (PackageInfo pk : mPm.getInstalledPackages(0)) {
            ActivityInfo[] ac = pk.activities;
            if ((pk.applicationInfo.flags & 1) == 0){
                mApps.add(pk);
            }
        }
    }

    private void checkSupport() {
        try {
            Class<?> clz = Class.forName("com.android.dex.Dex");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DialogInterface.OnClickListener exitListener = new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            };
            builder.setMessage("这台设备不支持DumpDex")
                    .setPositiveButton("退出",  exitListener)
                    .show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PackageInfo pk = mApps.get(position);
        mSp.edit()
                .putString(KEY_PACKAGE, pk.packageName)
                .apply();
        mSp.edit()
                .putString(KEY_DIR, pk.applicationInfo.dataDir)
                .apply();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("设置保存成功,重新打开目标软件, \nPackage = "+pk.packageName+"\ndex文件输出目录 = \n"+pk.applicationInfo.dataDir)
                .show();
        mAdapter.notifyDataSetChanged();
    }


    class  MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public Object getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = new TextView(MainActivity.this);

            TextView view = (TextView) convertView;
            PackageInfo pk = mApps.get(position);
            view.setText(pk.applicationInfo.loadLabel(mPm));
            if (pk.packageName.equals(mSp.getString(KEY_PACKAGE,""))){
                view.setTextColor(Color.RED);
            }else {
                view.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }
}
