package org.arnu.diagnostic;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;

public final class MainActivity extends Activity {
 static boolean openedThisBoot(Context c){int boot=Settings.Global.getInt(c.getContentResolver(),"boot_count",-1);return boot>=0&&c.getSharedPreferences("arnu",0).getInt("opened_boot",-2)==boot;}
 private final boolean es=Locale.getDefault().getLanguage().equals("es");
 private static final int BG=0xff101925,CARD=0xff1c2b3c,INK=0xfff3f7fc,MUTED=0xffafc0d2,ACCENT=0xff73d8ef;
 private SharedPreferences p;
 private TextView status,summary;
 private String t(String en,String sp){return es?sp:en;}
 private int dp(float n){return Math.round(n*getResources().getDisplayMetrics().density);}
 private GradientDrawable shape(int color,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(12));if(stroke!=0)d.setStroke(dp(2),stroke);return d;}
 private StateListDrawable focus(){StateListDrawable d=new StateListDrawable();d.addState(new int[]{android.R.attr.state_focused},shape(0xff31546a,ACCENT));d.addState(new int[]{android.R.attr.state_pressed},shape(0xff31546a,ACCENT));d.addState(new int[]{},shape(CARD,0));return d;}
 private TextView label(String text,int size,int color,boolean strong){TextView v=new TextView(this);v.setText(text);v.setTextSize(size);v.setTextColor(color);v.setFontFeatureSettings("tnum");if(strong)v.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));return v;}
 private LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 private LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
 private void gap(LinearLayout l,int h){Space s=new Space(this);l.addView(s,new LinearLayout.LayoutParams(1,dp(h)));}
 private void fill(LinearLayout l,android.view.View v){l.addView(v,new LinearLayout.LayoutParams(0,-2,1));}
 private Button button(String text){Button b=new Button(this);b.setText(text);b.setTextSize(17);b.setTextColor(INK);b.setAllCaps(false);b.setBackground(focus());b.setMinHeight(dp(44));b.setMinimumHeight(dp(44));b.setPadding(dp(12),dp(5),dp(12),dp(5));b.setFocusable(true);return b;}
 private void toggle(LinearLayout parent,String title,String subtitle,String key,boolean def){
 Switch s=new Switch(this);s.setText(title);s.setTextColor(INK);s.setTextSize(19);s.setShowText(false);s.setChecked(p.getBoolean(key,def));s.setBackground(focus());s.setPadding(dp(14),dp(9),dp(14),dp(9));s.setFocusable(true);
 s.setThumbTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{ACCENT,0xffa0acba}));
 s.setOnCheckedChangeListener((v,on)->{p.edit().putBoolean(key,on).apply();refresh();});parent.addView(s,new LinearLayout.LayoutParams(-1,-2));gap(parent,4);TextView hint=label(subtitle,13,MUTED,false);hint.setPadding(dp(12),0,0,0);parent.addView(hint);gap(parent,12);
 }
 private void counter(LinearLayout parent,String title,String subtitle,String key,int min,int max,int def){
 LinearLayout card=vertical();card.setBackground(shape(CARD,0));card.setPadding(dp(14),dp(10),dp(14),dp(10));card.addView(label(title,19,INK,true));gap(card,3);card.addView(label(subtitle,13,MUTED,false));gap(card,8);
 LinearLayout row=horizontal();Button minus=button("−"),plus=button("+");TextView val=label("",26,ACCENT,true);val.setGravity(Gravity.CENTER);row.addView(minus,new LinearLayout.LayoutParams(dp(54),dp(48)));fill(row,val);row.addView(plus,new LinearLayout.LayoutParams(dp(54),dp(48)));card.addView(row);parent.addView(card);gap(parent,10);
 Runnable display=()->{int n=p.getInt(key,def);val.setText(n==0?t("Always","Siempre"):n+" s");minus.setContentDescription(t("Decrease ","Reducir ")+title);plus.setContentDescription(t("Increase ","Aumentar ")+title);};
 minus.setOnClickListener(v->{p.edit().putInt(key,Math.max(min,p.getInt(key,def)-1)).apply();display.run();refresh();});
 plus.setOnClickListener(v->{p.edit().putInt(key,Math.min(max,p.getInt(key,def)+1)).apply();display.run();refresh();});display.run();
 }
 @Override public void onCreate(Bundle b){
 super.onCreate(b);p=getSharedPreferences("arnu",0);p.edit().putInt("opened_boot",Settings.Global.getInt(getContentResolver(),"boot_count",-1)).apply();
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);LinearLayout root=vertical();root.setPadding(dp(28),dp(18),dp(28),dp(18));scroll.addView(root);setContentView(scroll);
 LinearLayout header=horizontal();ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.arnu_icon);header.addView(logo,new LinearLayout.LayoutParams(dp(64),dp(64)));LinearLayout branding=vertical();branding.setPadding(dp(12),0,0,0);branding.addView(label("ARNu",32,INK,true));branding.addView(label("Auto Rewind Nuvio",15,MUTED,false));fill(header,branding);header.addView(label("v0.5",13,MUTED,false));root.addView(header);gap(root,6);status=label("",15,ACCENT,true);root.addView(status);gap(root,14);
 boolean wide=getResources().getConfiguration().screenWidthDp>=650;LinearLayout columns=wide?horizontal():vertical();columns.setGravity(Gravity.TOP);root.addView(columns);
 LinearLayout left=vertical(),right=vertical();if(wide){fill(columns,left);Space sp=new Space(this);columns.addView(sp,new LinearLayout.LayoutParams(dp(20),1));fill(columns,right);}else{columns.addView(left);columns.addView(right);}
 left.addView(label(t("GENERAL","GENERAL"),13,MUTED,true));gap(left,8);
 toggle(left,t("Automatic rewind","Retroceso automático"),t("Resume with context","Retoma la historia sin perderte nada"),"enabled",false);
 toggle(left,t("Start with Android TV","Iniciar con Android TV"),t("Ready after every restart","Listo después de cada reinicio"),"autostart",true);
 Button grant=button(t("Media access settings","Configurar acceso multimedia"));grant.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));}catch(Exception e){Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}});left.addView(grant);gap(left,12);
 right.addView(label(t("PLAYBACK","REPRODUCCIÓN"),13,MUTED,true));gap(right,8);
 counter(right,t("Rewind time","Tiempo de retroceso"),t("Seconds to go back","Segundos que se rebobinan"),"seconds",1,30,7);
 counter(right,t("Minimum pause","Pausa mínima"),t("Ignore shorter interruptions","Ignora las interrupciones breves"),"min_pause",0,120,0);
 summary=label("",14,MUTED,false);root.addView(summary);refresh();
 }
 @Override protected void onResume(){super.onResume();refresh();}
 private void refresh(){if(status==null||p==null)return;NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);boolean access=nm.isNotificationListenerAccessGranted(new ComponentName(this,SessionListener.class));status.setText(!access?t("● Media access required","● Falta acceso multimedia"):p.getBoolean("enabled",false)?t("● Enabled · Media access granted","● Activado · Acceso concedido"):t("● Disabled","● Desactivado"));if(summary!=null){int n=p.getInt("min_pause",0),s=p.getInt("seconds",7);summary.setText(n==0?t("Rewind "+s+" s after every pause.","Retrocede "+s+" s después de cualquier pausa."):t("Rewind "+s+" s after a pause of at least "+n+" s.","Retrocede "+s+" s tras una pausa de al menos "+n+" s."));}}
}
