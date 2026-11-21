package org.arnu.diagnostic;
import android.app.*;
import android.content.*;
import android.media.session.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.Locale;

public final class MainActivity extends Activity {
 TextView status;
 Button rewind;
 MediaController controller;
 boolean es = Locale.getDefault().getLanguage().equals("es");
 String t(String en,String sp){return es?sp:en;}
 @Override public void onCreate(Bundle b){
 super.onCreate(b);
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(48,32,48,32);root.setGravity(Gravity.CENTER_VERTICAL);
 TextView title=new TextView(this);title.setText("ARNu Diagnostic 0.1");title.setTextSize(28);root.addView(title);
 status=new TextView(this);status.setTextSize(18);root.addView(status);
 Button grant=new Button(this);grant.setText(t("Grant media access","Conceder acceso multimedia"));grant.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));}catch(Exception e){status.setText(e.toString());}});root.addView(grant);
 Button detect=new Button(this);detect.setText(t("Detect Nuvio","Detectar Nuvio"));detect.setOnClickListener(v->detect());root.addView(detect);
 rewind=new Button(this);rewind.setText(t("Rewind 7 seconds","Retroceder 7 segundos"));rewind.setOnClickListener(v->seek());root.addView(rewind);
 setContentView(root);grant.requestFocus();
 }
 @Override protected void onResume(){super.onResume();detect();}
 void detect(){controller=null;rewind.setEnabled(false);
 try{
 MediaSessionManager m=(MediaSessionManager)getSystemService(MEDIA_SESSION_SERVICE);
 for(MediaController c:m.getActiveSessions(new ComponentName(this,SessionListener.class)))if(c.getPackageName().equals("com.nuvio.tv")){controller=c;break;}
 if(controller==null){status.setText(t("Nuvio session not found","Sesión de Nuvio no encontrada"));return;}
 PlaybackState s=controller.getPlaybackState();
 if(s==null){status.setText(t("No playback state","Sin estado de reproducción"));return;}
 boolean supported=(s.getActions()&PlaybackState.ACTION_SEEK_TO)!=0;
 rewind.setEnabled(supported);
 status.setText(t("Nuvio detected","Nuvio detectado")+"\n"+t("State","Estado")+": "+s.getState()+" | "+t("Position","Posición")+": "+s.getPosition()+" ms\nSeek: "+supported);
 }catch(SecurityException e){status.setText(t("Grant notification listener access first","Concede primero el acceso a notificaciones"));}catch(Exception e){status.setText(e.toString());}}
 void seek(){try{
 if(controller==null)return;PlaybackState s=controller.getPlaybackState();
 if(s==null||s.getState()!=PlaybackState.STATE_PAUSED||s.getPosition()<0||(s.getActions()&PlaybackState.ACTION_SEEK_TO)==0){status.setText(t("Pause Nuvio and detect again","Pausa Nuvio y vuelve a detectar"));return;}
 long before=s.getPosition(),target=Math.max(0,before-7000);
 controller.getTransportControls().seekTo(target);
 status.setText(t("Command sent","Orden enviada")+": "+before+" → "+target+" ms");
 new Handler(Looper.getMainLooper()).postDelayed(()->{try{PlaybackState now=controller.getPlaybackState();status.append("\n"+t("Reported position","Posición notificada")+": "+(now==null?"?":now.getPosition())+" ms");}catch(Exception ignored){}},1500);
 }catch(Exception e){status.setText(e.toString());}}
}
