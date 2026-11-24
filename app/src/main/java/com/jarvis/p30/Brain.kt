package com.jarvis.p30
import android.content.*
import android.media.AudioManager
import android.hardware.camera2.CameraManager
import android.provider.Settings
import android.net.Uri
import java.text.Normalizer
import java.util.Locale
object Brain {
 fun norm(s:String)=Normalizer.normalize(s.lowercase(Locale.FRANCE),Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "").trim()
 fun run(c:Context, raw:String):String {
 val s=norm(raw).removePrefix("jarvis").trim()
 fun open(i:Intent):String { c.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); return "Ouverture demandée." }
 return try { when {
 s.contains("volume") -> { val m=c.getSystemService(AudioManager::class.java); m.adjustStreamVolume(AudioManager.STREAM_MUSIC,if(s.contains("baisse")||s.contains("diminue")) AudioManager.ADJUST_LOWER else AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI); "Volume ajusté." }
 s.contains("lampe") || s.contains("torche") -> { val m=c.getSystemService(CameraManager::class.java); val id=m.cameraIdList.first { m.getCameraCharacteristics(it).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE)==true }; m.setTorchMode(id,!s.contains("eteins")&&!s.contains("desactive")); "Commande lampe effectuée." }
 s.contains("bluetooth") -> open(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
 s.contains("wifi") || s.contains("wi-fi") -> open(Intent(Settings.ACTION_WIFI_SETTINGS))
 s.contains("parametres") || s.contains("reglages") -> open(Intent(Settings.ACTION_SETTINGS))
 s.startsWith("ouvre ") || s.startsWith("lance ") -> { val q=s.substringAfter(' ').trim(); val pm=c.packageManager; val apps=pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),0); val app=apps.firstOrNull { norm(it.loadLabel(pm).toString())==q } ?: apps.firstOrNull { norm(it.loadLabel(pm).toString()).contains(q) }; if(app==null) "Application introuvable." else open(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setClassName(app.activityInfo.packageName,app.activityInfo.name)) }
 s.startsWith("appelle ") -> { val n=s.filter { it.isDigit() }; if(n.length>=4) open(Intent(Intent.ACTION_DIAL,Uri.parse("tel:$n"))) else "Ouvre le téléphone pour choisir ton contact." }
 s.contains("heure") -> java.text.SimpleDateFormat("HH:mm",Locale.FRANCE).format(java.util.Date())
 s.contains("bonjour") -> "Bonjour, je suis Jarvis, prêt à recevoir tes commandes."
 else -> "Je ne connais pas encore cette commande."
 } } catch(e:Exception){ "Action indisponible. Vérifie les autorisations ou déverrouille le téléphone." }
 }
}
