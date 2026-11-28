package org.arnu.diagnostic;
import android.content.SharedPreferences;
final class Players {
 static final String[] PACKAGES={"com.nuvio.tv","com.nuvio.tv.lite","com.stremio.one","com.nuvio.tv.test"};
 static final String[] NAMES={"Nuvio","Nuvio Lite","Stremio","Nuvio Arc"};
 static boolean enabled(SharedPreferences p,String pkg){
  for(String candidate:PACKAGES) if(candidate.equals(pkg)) return p.getBoolean("player."+pkg,true);
  return false;
 }
}
