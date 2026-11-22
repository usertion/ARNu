package org.arnu.diagnostic;
import android.content.*;
import android.media.session.*;
import android.os.*;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import java.util.List;

public final class SessionListener extends NotificationListenerService {
 private final Handler h=new Handler(Looper.getMainLooper());
 private MediaSessionManager manager;
 private MediaController controller;
 private MediaController.Callback callback;
 private long paused=-1;
 private boolean connected;
 private final SharedPreferences.OnSharedPreferenceChangeListener prefs=(p,k)->{if("enabled".equals(k)){paused=-1;refresh();}};
 private final MediaSessionManager.OnActiveSessionsChangedListener sessions=list->refresh();
 private final Runnable tick=new Runnable(){public void run(){if(connected){refresh();h.postDelayed(this,2000);}}};
 private boolean enabled(){return getSharedPreferences("arnu",0).getBoolean("enabled",false);}
 @Override public void onListenerConnected(){
 super.onListenerConnected(); connected=true;
 getSharedPreferences("arnu",0).registerOnSharedPreferenceChangeListener(prefs);
 manager=(MediaSessionManager)getSystemService(MEDIA_SESSION_SERVICE);
 try{manager.addOnActiveSessionsChangedListener(sessions,new ComponentName(this,SessionListener.class),h);}catch(Exception e){Log.e("ARNu","Listener registration",e);}
 h.removeCallbacks(tick);h.post(tick);Log.i("ARNu","Listener connected");
 }
 private void refresh(){
 if(!connected||!enabled()){detach();return;}
 try{
 List<MediaController> list=manager.getActiveSessions(new ComponentName(this,SessionListener.class));
 MediaController found=null;
 for(MediaController c:list)if("com.nuvio.tv".equals(c.getPackageName())){found=c;break;}
 if(found==null){detach();return;}
 if(controller!=null&&controller.getSessionToken().equals(found.getSessionToken()))return;
 detach();controller=found;
 final MediaController current=found;
 callback=new MediaController.Callback(){
 @Override public void onPlaybackStateChanged(PlaybackState s){if(controller==current)handle(s);}
 @Override public void onSessionDestroyed(){if(controller==current)detach();}
 @Override public void onMetadataChanged(android.media.MediaMetadata m){paused=-1;}
 };
 controller.registerCallback(callback,h);
 Log.i("ARNu","Nuvio session detected");handle(controller.getPlaybackState());
 }catch(Exception e){Log.e("ARNu","Session scan failed",e);detach();}
 }
 private void handle(PlaybackState s){
 if(s==null||!enabled()){paused=-1;return;}
 int state=s.getState();
 Log.i("ARNu","State="+state+" position="+s.getPosition()+" actions="+s.getActions());
 if(state==PlaybackState.STATE_PAUSED){paused=s.getPosition();return;}
 if(state==PlaybackState.STATE_BUFFERING||state==PlaybackState.STATE_CONNECTING)return;
 if(state==PlaybackState.STATE_PLAYING&&paused>=0){
 long before=paused;paused=-1;
 if((s.getActions()&PlaybackState.ACTION_SEEK_TO)==0){Log.w("ARNu","Seek unsupported");return;}
 if(s.getPosition()<0||Math.abs(s.getPosition()-before)>3000){Log.w("ARNu","Resume position changed; skipping rewind");return;}
 long target=Math.max(0,before-7000);
 final MediaController current=controller;
 try{current.getTransportControls().seekTo(target);Log.i("ARNu","Seek requested "+before+" -> "+target);
 h.postDelayed(()->{if(controller==current){PlaybackState now=current.getPlaybackState();Log.i("ARNu","After seek position="+(now==null?-1:now.getPosition()));}},1500);
 }catch(Exception e){Log.e("ARNu","Seek failed",e);}
 }else if(state!=PlaybackState.STATE_PLAYING){paused=-1;}
 }
 private void detach(){if(controller!=null){controller.unregisterCallback(callback);Log.i("ARNu","Nuvio session detached");}controller=null;callback=null;paused=-1;}
 private void cleanup(){connected=false;h.removeCallbacksAndMessages(null);detach();getSharedPreferences("arnu",0).unregisterOnSharedPreferenceChangeListener(prefs);if(manager!=null)manager.removeOnActiveSessionsChangedListener(sessions);}
 @Override public void onListenerDisconnected(){cleanup();super.onListenerDisconnected();}
 @Override public void onDestroy(){cleanup();super.onDestroy();}
}
