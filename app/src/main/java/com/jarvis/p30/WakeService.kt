package com.jarvis.p30
import android.app.*
import android.content.*
import android.os.*
import android.speech.tts.*
import org.vosk.*
import org.vosk.android.*
import org.json.JSONObject
import java.util.Locale
class WakeService:Service(),RecognitionListener {
 private var model:Model?=null; private var speech:SpeechService?=null; private var recognizer:Recognizer?=null
 private var tts:TextToSpeech?=null; private var ready=false; private var loading=false; private var armedUntil=0L; private var lock:PowerManager.WakeLock?=null; private var speaking=false; private var dead=false
 override fun onBind(i:Intent?)=null
 override fun onCreate(){super.onCreate(); getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("jarvis","Jarvis écoute",NotificationManager.IMPORTANCE_LOW)); tts=TextToSpeech(this){status-> if(status==TextToSpeech.SUCCESS){val engine=tts; val voice=engine?.voices?.filter{it.locale.language=="fr"&&!it.isNetworkConnectionRequired}?.sortedByDescending{it.quality}?.firstOrNull(); if(voice!=null){engine?.voice=voice;engine?.setPitch(0.72f);engine?.setSpeechRate(0.92f);ready=true}}}; lock=getSystemService(PowerManager::class.java).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"jarvis:listen");lock?.acquire() }
 fun notice(message:String):Notification {val flags=PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE; val open=PendingIntent.getActivity(this,1,Intent(this,MainActivity::class.java),flags);val talk=PendingIntent.getService(this,2,Intent(this,WakeService::class.java).putExtra("manual",true),flags);val stop=PendingIntent.getService(this,3,Intent(this,WakeService::class.java).setAction("stop"),flags);return Notification.Builder(this,"jarvis").setSmallIcon(android.R.drawable.ic_btn_speak_now).setContentTitle("Jarvis Offline").setContentText(message).setContentIntent(open).setOngoing(true).addAction(Notification.Action.Builder(null,"Parler",talk).build()).addAction(Notification.Action.Builder(null,"Arrêter",stop).build()).build()}
 fun update(s:String){getSystemService(NotificationManager::class.java).notify(7,notice(s))}
 override fun onStartCommand(i:Intent?,flags:Int,id:Int):Int {if(i?.action=="stop"){stopSelf();return START_NOT_STICKY};startForeground(7,notice("Chargement du modèle vocal"));if(i?.getBooleanExtra("manual",false)==true)armedUntil=SystemClock.elapsedRealtime()+30000; if(model!=null){update("Micro actif");return START_NOT_STICKY};if(!loading){loading=true;StorageService.unpack(this,"model-fr","model-fr",{m->if(dead){m.close()}else{model=m;listen()}},{e->update("Erreur modèle : "+e.message);loading=false})};return START_NOT_STICKY}
 fun listen(){try{val m=model?:return;recognizer=Recognizer(m,16000f);speech=SpeechService(recognizer,16000f);speech?.startListening(this);update("Micro actif • dis Jarvis")}catch(e:Exception){update("Micro indisponible : "+e.message)}}
 override fun onResult(h:String?){if(speaking||dead)return;val text=try{JSONObject(h?:"{}").optString("text")}catch(e:Exception){""};if(text.isBlank())return;val n=Brain.norm(text);val wake=Regex("\\b(jarvis|jar vice|jar vise|jervis)\\b").find(n);if(wake!=null){armedUntil=SystemClock.elapsedRealtime()+15000;val command=n.substring(wake.range.last+1).trim();if(command.isEmpty()){update("Je t’écoute");return};execute(command)}else if(SystemClock.elapsedRealtime()<armedUntil){execute(text)}}
 fun execute(s:String){armedUntil=0;val answer=Brain.run(this,s);update(answer);if(ready){speaking=true;tts?.speak(answer,TextToSpeech.QUEUE_FLUSH,null,"answer");Handler(Looper.getMainLooper()).postDelayed({speaking=false},4000)}}
 override fun onPartialResult(h:String?){};override fun onFinalResult(h:String?){};override fun onError(e:Exception?){update("Erreur micro : "+e?.message)};override fun onTimeout(){update("Écoute interrompue")}
 override fun onDestroy(){dead=true;speech?.stop();speech?.shutdown();recognizer?.close();model?.close();tts?.shutdown();if(lock?.isHeld==true)lock?.release();super.onDestroy()}
}
