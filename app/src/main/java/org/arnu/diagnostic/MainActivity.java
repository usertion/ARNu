package org.arnu.diagnostic;
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;
public final class MainActivity extends Activity {
 static boolean openedThisBoot(Context c){int boot=Settings.Global.getInt(c.getContentResolver(),"boot_count",-1);return boot>=0&&c.getSharedPreferences("arnu",0).getInt("opened_boot",-2)==boot;}
 private final boolean es=Locale.getDefault().getLanguage().equals("es");
 private String t(String en,String sp){return es?sp:en;}
 @Override public void onCreate(Bundle b){
 super.onCreate(b);
 getSharedPreferences("arnu",0).edit().putInt("opened_boot",Settings.Global.getInt(getContentResolver(),"boot_count",-1)).apply();
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(48,32,48,32);root.setGravity(Gravity.CENTER_VERTICAL);
 TextView title=new TextView(this);title.setText("ARNu 0.3");title.setTextSize(28);root.addView(title);
 Button grant=new Button(this);grant.setText(t("Grant media access","Conceder acceso multimedia"));grant.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));}catch(Exception e){Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}});root.addView(grant);
 Switch toggle=new Switch(this);toggle.setText(t("Automatic rewind","Retroceso automático"));toggle.setTextSize(20);toggle.setChecked(getSharedPreferences("arnu",0).getBoolean("enabled",false));toggle.setOnCheckedChangeListener((v,on)->getSharedPreferences("arnu",0).edit().putBoolean("enabled",on).apply());root.addView(toggle);
 Switch boot=new Switch(this);boot.setText(t("Start with Android TV","Iniciar con Android TV"));boot.setTextSize(20);boot.setChecked(getSharedPreferences("arnu",0).getBoolean("autostart",true));boot.setOnCheckedChangeListener((v,on)->getSharedPreferences("arnu",0).edit().putBoolean("autostart",on).apply());root.addView(boot);
 TextView duration=new TextView(this);duration.setTextSize(20);root.addView(duration);
 SeekBar bar=new SeekBar(this);bar.setMax(29);bar.setProgress(getSharedPreferences("arnu",0).getInt("seconds",7)-1);duration.setText(t("Rewind: ","Retroceso: ")+(bar.getProgress()+1)+" s");bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar b,int n,boolean user){duration.setText(t("Rewind: ","Retroceso: ")+(n+1)+" s");getSharedPreferences("arnu",0).edit().putInt("seconds",n+1).apply();}public void onStartTrackingTouch(SeekBar b){}public void onStopTrackingTouch(SeekBar b){}});root.addView(bar);
 TextView info=new TextView(this);info.setText(t("Grant access, enable rewind, then return to Nuvio. Play, pause and resume without leaving Nuvio. Diagnostics: adb logcat -s ARNu","Concede acceso, activa el retroceso y vuelve a Nuvio. Reproduce, pausa y reanuda sin salir de Nuvio. Diagnóstico: adb logcat -s ARNu"));info.setTextSize(18);root.addView(info);
 setContentView(root);grant.requestFocus();
 }
}
