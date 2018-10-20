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

public class Main implements IXposedHookLoadPackage
{
	private String TAG="LogCall";
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
	{
		if (!lpparam.packageName.equals("com.android.bluetooth") && !lpparam.packageName.equals("javax.obex")/*&&!lpparam.packageName.equals("org.codeaurora.bluetooth")*/)
		{
			//Log.v(TAG, "Not: " + lpparam.packageName);
			return;
		}
	
		Log.i(TAG, "Yes " + lpparam.packageName);
		TAG+=lpparam.packageName;
		//Modified https://d3adend.org/blog/?p=589
		ApplicationInfo applicationInfo =lpparam.appInfo;//AndroidAppHelper.currentApplicationInfo();
		if(applicationInfo==null)
		{
			Log.e(TAG,"AppInfo null!!");
			return;
		}
		if (applicationInfo.processName.equals("com.android.bluetooth"))
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
				if (className.startsWith("com.android.bluetooth") || (obex = className.startsWith("javax.obex"))||className.startsWith("org.codeaurora.bluetooth"))
				{
					try
					{
						final Class clazz = lpparam.classLoader.loadClass(className);
						for (final Method method : clazz.getDeclaredMethods())
						{
							if(Modifier.isAbstract( method.getModifiers()))
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
							XposedBridge.hookMethod(method, new XC_MethodHook() {
									final String methodNam=method.getName();
									final String classNam=clazz.getName();
									final StringBuilder sb=new StringBuilder("[");
									final String logstr=classNam + "." + methodNam;
									final boolean isVoid=method.getReturnType().equals(Void.TYPE);
									final String proto=method.getReturnType().toString() + " " + logstr;
									final String voidRet=proto + " returned ";
									final String nullRet=voidRet + "null";
									@Override
									protected void beforeHookedMethod(MethodHookParam param) throws Throwable
									{
										//Method method=(Method)param.args[0];
										sb.setLength(0);
										sb.append(logstr);
										//Log.v(TAG,logstr);
										if (param.args != null)
										{
											for (Object o:param.args)
											{
												String typnam="";
												String value="null";
												if (o != null)
												{
													typnam = o.getClass().getName();
													value = o.toString();
												}
												sb.append(typnam).append(" ").append(value).append(", ");
											}
											
										}
										sb.append("]");
										Log.v(TAG, sb.toString());
									}
									@Override
									protected void afterHookedMethod(MethodHookParam param) throws Throwable
									{
										Object o=null;
										try
										{
											o=param.getResultOrThrowable();
										}
										catch (Throwable e)
										{
											Log.v(TAG,logstr,e);
											param.setResult(e);
											return;
										}
										if (isVoid)
										{
											Log.v(TAG, voidRet);
											return;
										}
										if (o == null)
										{
											Log.v(TAG, nullRet);
											return;
										}
										Log.v(TAG, voidRet + o.toString());
									}
								});
						}
					}
					catch (ClassNotFoundException e)
					{
						Log.wtf(TAG, e.toString());
					}
				}
			}
		}
		//	ClassLoader rootcl=lpparam.classLoader.getSystemClassLoader();
		//findAndHookMethod("de.robv.android.xposed.XposedBridge", rootcl, "handleHookedMethod", Member.class, int.class, Object.class, Object.class, Object[].class, );
	}
}
