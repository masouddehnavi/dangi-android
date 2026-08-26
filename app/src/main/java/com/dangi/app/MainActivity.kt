package com.dangi.app
import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: AppCompatActivity(){
 lateinit var web:WebView
 override fun onCreate(b:Bundle?){super.onCreate(b); web=WebView(this);setContentView(web);web.settings.javaScriptEnabled=true;web.settings.domStorageEnabled=true;web.addJavascriptInterface(BioBridge(),"AndroidBiometric");web.addJavascriptInterface(NotifyBridge(),"AndroidNotify");web.loadUrl("file:///android_asset/index.html"); if(Build.VERSION.SDK_INT>=33&&ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),7)}
 inner class BioBridge{@JavascriptInterface fun isAvailable()=BiometricManager.from(this@MainActivity).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)==BiometricManager.BIOMETRIC_SUCCESS
 @JavascriptInterface fun authenticate(){runOnUiThread{val p=BiometricPrompt(this@MainActivity,mainExecutor,object:BiometricPrompt.AuthenticationCallback(){override fun onAuthenticationSucceeded(r:BiometricPrompt.AuthenticationResult){web.evaluateJavascript("window.onBiometricResult(true)",null)};override fun onAuthenticationError(c:Int,e:CharSequence){web.evaluateJavascript("window.onBiometricResult(false)",null)}});p.authenticate(BiometricPrompt.PromptInfo.Builder().setTitle("ورود به دَنگی").setSubtitle("اثر انگشت را تأیید کنید").setNegativeButtonText("استفاده از رمز").build())}}}
 inner class NotifyBridge{@JavascriptInterface fun schedule(id:String,title:String,text:String,date:String){try{val fmt=if(date.contains("T"))SimpleDateFormat("yyyy-MM-dd'T'HH:mm",Locale.US) else SimpleDateFormat("yyyy-MM-dd",Locale.US);val whenMs=fmt.parse(date)?.time?:return;val i=Intent(this@MainActivity,ReminderReceiver::class.java).putExtra("title",title).putExtra("text",text).putExtra("id",id.hashCode());val pi=PendingIntent.getBroadcast(this@MainActivity,id.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);val am=getSystemService(ALARM_SERVICE) as AlarmManager;if(Build.VERSION.SDK_INT>=31&&am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,pi) else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,whenMs,pi)}catch(_:Exception){}}
 @JavascriptInterface fun cancel(id:String){val pi=PendingIntent.getBroadcast(this@MainActivity,id.hashCode(),Intent(this@MainActivity,ReminderReceiver::class.java),PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE);if(pi!=null)(getSystemService(ALARM_SERVICE) as AlarmManager).cancel(pi)}}
}
class ReminderReceiver:BroadcastReceiver(){override fun onReceive(c:Context,i:Intent){val ch="dangi_reminders";val nm=c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(NotificationChannel(ch,"یادآوری‌های دَنگی",NotificationManager.IMPORTANCE_HIGH));val n=NotificationCompat.Builder(c,ch).setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle(i.getStringExtra("title")?:"دَنگی").setContentText(i.getStringExtra("text")?:"یادآوری").setAutoCancel(true).build();if(Build.VERSION.SDK_INT<33||ActivityCompat.checkSelfPermission(c,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)nm.notify(i.getIntExtra("id",1),n)}}
