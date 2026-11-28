package org.arnu.diagnostic;
import android.content.*;
import android.service.notification.NotificationListenerService;
import android.util.Log;
public final class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
 if(!Intent.ACTION_BOOT_COMPLETED.equals(i.getAction()))return;
 c.getSharedPreferences("arnu",0).edit().putBoolean("runtime_stopped",false).commit();
 if(c.getSharedPreferences("arnu",0).getBoolean("autostart",true)&&c.getSharedPreferences("arnu",0).getBoolean("enabled",false)){
 Log.i("ARNu","Boot: requesting listener connection");
 try{NotificationListenerService.requestRebind(new ComponentName(c,SessionListener.class));}catch(Exception e){Log.w("ARNu","Boot rebind failed",e);}
 }
 }
}