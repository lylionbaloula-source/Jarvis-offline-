package com.jarvis.p30
import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.*
import android.graphics.Color
class MainActivity:Activity(){
 override fun onCreate(b:Bundle?){ super.onCreate(b)
 val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(32,48,32,32);setBackgroundColor(Color.rgb(12,18,32))}
 fun label(t:String){root.addView(TextView(this).apply{text=t;textSize=20f;setTextColor(Color.WHITE);setPadding(0,16,0,16)})}
 fun button(t:String,action:()->Unit){root.addView(Button(this).apply{text=t;setOnClickListener{action()}})}
 label("JARVIS • OFFLINE 2.0"); label("Sans API ni Internet. Dis Jarvis puis ta commande.")
 button("Activer écoute Jarvis"){start(false)}; button("Parler maintenant"){start(true)}; button("Arrêter écoute"){stopService(Intent(this,WakeService::class.java))}
 button("Autoriser microphone et lampe"){requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA),1)}
 label("Exemples : ouvre Spotify, ouvre les paramètres, augmente le volume, allume la lampe, quelle heure est-il ?")
 label("La notification permet de parler ou d’arrêter. Le téléphone peut devoir être déverrouillé.")
 setContentView(ScrollView(this).apply{isFillViewport=true;addView(root)})
 if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),1)
 }
 fun start(manual:Boolean){if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),1);return};startForegroundService(Intent(this,WakeService::class.java).putExtra("manual",manual))}
}
