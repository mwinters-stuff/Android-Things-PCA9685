package nz.org.winters.android.androidthingspca9685;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */

class AppPrefs {
  private final SharedPreferences sharedPref;

  AppPrefs(Context context){
    sharedPref = context.getSharedPreferences("test",Context.MODE_PRIVATE);
  }

  int selectedChannel() {
    return sharedPref.getInt("selectedChannel",0);
  }

  void selectedChannel(int value) {
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putInt("selectedChannel",value);
    editor.apply();
  }

  String channelAnglesLeft() {
    return sharedPref.getString("channelAnglesLeft","");
  }

  String channelAnglesRight() {
    return sharedPref.getString("channelAnglesRight","");
  }

  void channelAnglesLeft(String value){
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("channelAnglesLeft",value);
    editor.apply();
  }

  void channelAnglesRight(String value){
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("channelAnglesRight",value);
    editor.apply();
  }
}

