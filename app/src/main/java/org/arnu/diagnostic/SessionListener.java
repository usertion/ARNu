package org.arnu.diagnostic;
import android.content.*;
import android.media.MediaMetadata;
import android.media.session.*;
import android.os.*;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import java.util.*;

public final class SessionListener extends NotificationListenerService {
 private final Handler h=new Handler(Looper.getMainLooper());
 private MediaSessionManager manager;
 private boolean connected;
 private final Map<MediaSession.Token,Tracked> tracked=new HashMap<>();
 private SharedPreferences p(){return getSharedPreferences("arnu",0);}
 private boolean enabled(){return !p().getBoolean("runtime_stopped",false)&&p().getBoolean("enabled",false)&&(p().getBoolean("autostart",true)||MainActivity.openedThisBoot(this));}
 private final MediaSessionManager.OnActiveSessionsChangedListener sessions=list->refresh();
 private final Runnable tick=new Runnable(){public void run(){if(connected&&enabled()){refresh();h.postDelayed(this,2000);}}};
 private final SharedPreferences.OnSharedPreferenceChangeListener prefs=(p,k)->{
 if("runtime_stopped".equals(k)&&p.getBoolean(k,false)){suspend();return;}
 if("enabled".equals(k)||"runtime_stopped".equals(k)||"autostart".equals(k)||"opened_boot".equals(k)||k.startsWith("player.")){
 refresh();h.removeCallbacks(tick);if(connected&&enabled())h.postDelayed(tick,2000);
 }};
 @Override public void onListenerConnected(){
 super.onListenerConnected();if(p().getBoolean("runtime_stopped",false)){requestUnbind();return;}connected=true;p().registerOnSharedPreferenceChangeListener(prefs);
 manager=(MediaSessionManager)getSystemService(MEDIA_SESSION_SERVICE);
 try{manager.addOnActiveSessionsChangedListener(sessions,new ComponentName(this,SessionListener.class),h);}catch(Exception e){Log.e("ARNu","Listener registration",e);}
 h.removeCallbacks(tick);h.post(tick);
 }
 private void refresh(){
 if(!connected||!enabled()){clear();return;}
 try{
 Set<MediaSession.Token> seen=new HashSet<>();
 for(MediaController c:manager.getActiveSessions(new ComponentName(this,SessionListener.class))){
 if(!Players.enabled(p(),c.getPackageName()))continue;
 MediaSession.Token token=c.getSessionToken();seen.add(token);
 if(!tracked.containsKey(token)){Tracked tr=new Tracked(c);tracked.put(token,tr);try{c.registerCallback(tr,h);}catch(Exception e){tracked.remove(token);throw e;}Log.i("ARNu","Session detected "+c.getPackageName());}
 }
 for(MediaSession.Token token:new ArrayList<>(tracked.keySet()))if(!seen.contains(token))detach(token);
 }catch(Exception e){Log.e("ARNu","Session scan failed",e);clear();}
 }
 private final class Tracked extends MediaController.Callback {
 final MediaController c;final MediaSession.Token token;final String pkg;
 long paused=-1,started=-1;boolean played, seeking;
 final Runnable rearm=this::rearmIfPlaying;
 void rearmIfPlaying(){if(!valid())return;PlaybackState now=c.getPlaybackState();if(now!=null&&now.getState()==PlaybackState.STATE_PLAYING){seeking=false;played=true;reset();Log.i("ARNu",pkg+" rearmed after stable playback");}}
 Tracked(MediaController controller){c=controller;token=c.getSessionToken();pkg=c.getPackageName();PlaybackState s=c.getPlaybackState();played=s!=null&&s.getState()==PlaybackState.STATE_PLAYING;}
 boolean valid(){return tracked.get(token)==this&&enabled()&&Players.enabled(p(),pkg);}
 void reset(){paused=-1;started=-1;}
 @Override public void onMetadataChanged(MediaMetadata m){reset();}
 @Override public void onSessionDestroyed(){detach(token);}
 @Override public void onPlaybackStateChanged(PlaybackState s){
 if(!valid()||s==null){reset();played=false;return;}
 int state=s.getState();long pos=s.getPosition();
 Log.i("ARNu",pkg+" state="+state+" position="+pos+" actions="+s.getActions());
 if(seeking){
 h.removeCallbacks(rearm);
 if(state==PlaybackState.STATE_PLAYING)h.postDelayed(rearm,2000);
 else if(state==PlaybackState.STATE_STOPPED||state==PlaybackState.STATE_NONE||state==PlaybackState.STATE_ERROR){seeking=false;played=false;reset();}
 Log.i("ARNu",pkg+" ignoring seek transition");return;
 }
 if(state==PlaybackState.STATE_PAUSED){
 if(played&&pos>=0){if(paused<0)started=SystemClock.elapsedRealtime();paused=pos;}return;
 }
 if(state==PlaybackState.STATE_BUFFERING||state==PlaybackState.STATE_CONNECTING)return;
 if(state!=PlaybackState.STATE_PLAYING){reset();played=false;return;}
 played=true;if(paused<0)return;
 long before=paused,elapsed=SystemClock.elapsedRealtime()-started;reset();
 if(elapsed<1000L*p().getInt("min_pause",0))return;
 if((s.getActions()&PlaybackState.ACTION_SEEK_TO)==0||pos<0||Math.abs(pos-before)>3000){Log.w("ARNu",pkg+" seek unsupported or position changed");return;}
 long target=Math.max(0,before-1000L*p().getInt("seconds",7));
 if(target>=pos||target>=before)return;
 seeking=true;h.removeCallbacks(rearm);
 try{c.getTransportControls().seekTo(target);h.postDelayed(rearm,2000);Log.i("ARNu",pkg+" seek requested "+before+" -> "+target);
 h.postDelayed(()->{if(valid()){PlaybackState now=c.getPlaybackState();Log.i("ARNu",pkg+" after seek position="+(now==null?-1:now.getPosition()));}},1500);
 }catch(Exception e){seeking=false;h.removeCallbacks(rearm);Log.e("ARNu",pkg+" seek failed",e);}
 }
 }
 private void detach(MediaSession.Token token){Tracked tr=tracked.remove(token);if(tr!=null){h.removeCallbacks(tr.rearm);tr.c.unregisterCallback(tr);Log.i("ARNu","Session detached "+tr.pkg);}}
 private void clear(){for(MediaSession.Token token:new ArrayList<>(tracked.keySet()))detach(token);}
 private void suspend(){cleanup();Log.i("ARNu","Stopped by user; preferences retained");requestUnbind();}
 private void cleanup(){connected=false;h.removeCallbacksAndMessages(null);clear();p().unregisterOnSharedPreferenceChangeListener(prefs);if(manager!=null)manager.removeOnActiveSessionsChangedListener(sessions);}
 @Override public void onListenerDisconnected(){cleanup();super.onListenerDisconnected();}
 @Override public void onDestroy(){cleanup();super.onDestroy();}
}
