package com.kyunggi.logcalls;

import android.content.pm.*;
import android.util.*;
import dalvik.system.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.app.*;
import android.bluetooth.*;

public class Main implements IXposedHookLoadPackage
{
	private String TAG="LogBluetooth";
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
	{
		if (!lpparam.packageName.equals("it.medieval.blueftp") && !lpparam.packageName.equals("javax.obex"))
		{
			//Log.v(TAG, "Not: " + lpparam.packageName);
			return;
		}

		Log.i(TAG, "Yes " + lpparam.packageName);
		TAG += lpparam.packageName;
		//Modified https://d3adend.org/blog/?p=589
		/*	ApplicationInfo applicationInfo =lpparam.appInfo;//AndroidAppHelper.currentApplicationInfo();
		 if (applicationInfo == null)
		 {
		 Log.e(TAG, "AppInfo null!!");
		 return;
		 }
		 Log.i(TAG, "succ 35");
		 /*
		 //if (applicationInfo.processName.equals("com.android.bluetooth"))
		 {		
		 Set<String> classes = new HashSet<>();
		 DexFile dex;
		 try
		 {
		 dex = new DexFile(applicationInfo.sourceDir);
		 Enumeration entries = dex.entries();
		 while (entries.hasMoreElements())
		 {
		 String entry = (String) entries.nextElement();
		 classes.add(entry);
		 }
		 dex.close();
		 } 
		 catch (IOException e)
		 {
		 Log.e(TAG, e.toString());
		 }

		 for (String className : classes)
		 {
		 boolean obex=false;
		 if (className.startsWith("android.bluetooth") || (obex = className.startsWith("javax.obex"))/*||className.startsWith("org.codeaurora.bluetooth"))
		 {
		 try
		 {
		 final Class clazz = lpparam.classLoader.loadClass(className);
		 for (final Method method : clazz.getDeclaredMethods())
		 {
		 */

		/*if (Modifier.isAbstract(method.getModifiers()))
		 {
		 continue;  //Avoid hooking abstract methods
		 }
		 if (obex)
		 {
		 if (!Modifier.isPublic(method.getModifiers()))
		 {
		 continue;
		 }			
		 }
		 */
		ClassLoader sysClsLoader=lpparam.classLoader.getSystemClassLoader();
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothOutputStream", sysClsLoader, "close", new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BTOutputStream.Close() called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothOutputStream", sysClsLoader, "write", int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BTOutputStream.write(" + param.args[0] + ")");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothOutputStream", sysClsLoader, "write", byte[].class, int.class, int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BTOutputStream.write(" + Arrays.toString((byte[])param.args[0]) + "," + param.args[1] + "," + param.args[2] + ")");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothOutputStream", sysClsLoader, "flush", new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BTOutputStream.flush() called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothInputStream", sysClsLoader, "available", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Object o=param.getResult();
					if (o != null)
					{
						Log.i(TAG, "BTInputStream.available()=" + (int)o);
					}
					else
					{
						Log.i(TAG, "BTInputStream.available() may have thrown an exception");
					}
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothInputStream", sysClsLoader, "close", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BTInputStream.close()");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothInputStream", sysClsLoader, "read", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Object o=param.getResult();
					if (o != null)
						Log.i(TAG, "BTInputStream.read() ret:" + (int)o);
					else
						Log.i(TAG, "BTInputStream.read() may have thrown an exception");

				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothInputStream", sysClsLoader, "read", byte[].class, int.class, int.class, new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Object o=param.getResult();
					if (o != null)
					{
						Log.i(TAG, "BTInputStream.read(buf," + param.args[1] + "," + param.args[2] + ") ret:" + (int)o);
						if (param.args[0] != null)
							Log.i(TAG, "buf=" + Arrays.toString((byte[])param.args[0]));
						else
							Log.i(TAG, "buf is null;;;;;");
					}
					else
						Log.i(TAG, "BTInputStream.read(buf," + param.args[1] + "," + param.args[2] + ") may have thrown an exception");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createInsecureRfcommSocket", int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createInsecureRfcommSocket(" + param.args[0] + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createInsecureRfcommSocketToServiceRecord", UUID.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createInsecureRfcommSocketToServiceRecord(UUID=" + ((UUID)param.args[0]).toString() + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createRfcommSocketToServiceRecord", UUID.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createRfcommSocketToServiceRecord(UUID=" + ((UUID)param.args[0]).toString() + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createInsecureL2capSocket", int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createInsecureL2capSocket(channel=" + ((int)param.args[0]) + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createL2capSocket", int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createL2capSocket(channel=" + ((int)param.args[0]) + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothDevice", sysClsLoader, "createRfcommSocket", int.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothDeice.createRfcommSocket(channel=" + ((int)param.args[0]) + ")called");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothSocket", sysClsLoader, "connect", new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothSocket.connect() called");
				}
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					try
					{
						Object o=param.getResultOrThrowable();
						if (o != null)
						{
							Log.i(TAG, "BluetoothSocket.connect() ret:" + o.toString());					
						}
						else
							Log.i(TAG, "BluetoothSocket.connect() may have thrown an exception");
					}
					catch (Exception e)
					{
						Log.i(TAG,"BluetoothSocket.connect exception",e);
						param.setResult(e);
					}
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothSocket", sysClsLoader, "isConnected", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothSocket.isConnected() returned " + param.getResult());
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothSocket", sysClsLoader, "getInputStream", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothSocket.getInputStream()");
				}
			});
		XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothSocket", sysClsLoader, "getOutputStream", new XC_MethodHook(){
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.i(TAG, "BluetoothSocket.getOutputStream()");
				}
			});

		/*}
		 }
		 catch (ClassNotFoundException e)
		 {
		 Log.wtf(TAG, e.toString());
		 }
		 }
		 }
		 }*/
		//	ClassLoader rootcl=lpparam.classLoader.getSystemClassLoader();
		//findAndHookMethod("de.robv.android.xposed.XposedBridge", rootcl, "handleHookedMethod", Member.class, int.class, Object.class, Object.class, Object[].class, );
	}
}
