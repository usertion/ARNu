package org.arnu.diagnostic;
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;
public final class MainActivity extends Activity {
 private final boolean es=Locale.getDefault().getLanguage().equals("es");
 private String t(String en,String sp){return es?sp:en;}
 @Override public void onCreate(Bundle b){
 super.onCreate(b);
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(48,32,48,32);root.setGravity(Gravity.CENTER_VERTICAL);
 TextView title=new TextView(this);title.setText("ARNu Diagnostic 0.2");title.setTextSize(28);root.addView(title);
 Button grant=new Button(this);grant.setText(t("Grant media access","Conceder acceso multimedia"));grant.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));}catch(Exception e){Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}});root.addView(grant);
 Switch toggle=new Switch(this);toggle.setText(t("Automatic rewind (7 s)","Retroceso automático (7 s)"));toggle.setTextSize(20);toggle.setChecked(getSharedPreferences("arnu",0).getBoolean("enabled",false));toggle.setOnCheckedChangeListener((v,on)->getSharedPreferences("arnu",0).edit().putBoolean("enabled",on).apply());root.addView(toggle);
 TextView info=new TextView(this);info.setText(t("Grant access, enable rewind, then return to Nuvio. Play, pause and resume without leaving Nuvio. Diagnostics: adb logcat -s ARNu","Concede acceso, activa el retroceso y vuelve a Nuvio. Reproduce, pausa y reanuda sin salir de Nuvio. Diagnóstico: adb logcat -s ARNu"));info.setTextSize(18);root.addView(info);
 setContentView(root);grant.requestFocus();
 }
}
