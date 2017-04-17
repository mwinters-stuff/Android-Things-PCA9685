package nz.org.winters.android.androidthingspca9685;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.DefaultStringSet;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.util.List;
import java.util.Set;

/**
 * Created by mathew on 17/01/17.
 * Copyright 2017 Mathew Winters
 */

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface AppPrefs {
//  @DefaultInt(0)
//  int angleLeft();
//
//  @DefaultInt(0)
//  int angleRight();

  @DefaultInt(0)
  int selectedChannel();

  @DefaultString("")
  String channelAnglesLeft();

  @DefaultString("")
  String channelAnglesRight();

}

