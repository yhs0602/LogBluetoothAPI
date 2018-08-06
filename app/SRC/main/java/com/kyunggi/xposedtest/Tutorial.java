package com.kyunggi.xposedtest;

import android.util.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;
import java.io.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Tutorial implements IXposedHookLoadPackage
{

	private String TAG="TUTORIAL";
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.bluetooth"))
		{
			Log.i(TAG,"Not: "+lpparam.packageName);
			return;
		}
		Log.i(TAG,"Yes "+lpparam.packageName);	

		findAndHookMethod("com.android.bluetooth.opp.BluetoothOppManager", lpparam.classLoader, "isWhitelisted", String.class,new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					Log.v(TAG,"HOOK DONE");
					param.setResult(true);
				}

			});
	}
}
