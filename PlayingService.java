package com.gking.simplemusicplayer;
import android.app.*;
import android.content.*;
import android.media.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import android.os.PowerManager.*;

import androidx.core.app.NotificationCompat;

import com.Gking.SimpleMusicPlayer.R;

public class PlayingService extends Service
{
	public static final MediaPlayer mp=new MediaPlayer();
	
	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}
	PowerManager.WakeLock wakeLock;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO: Implement this method
		mp.stop();
		mp.reset();
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		 wakeLock=powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SimpleMusicPlayer:tag1");
		wakeLock.acquire();
		final String sid=intent.getExtras().getString("mp");
		final String name=intent.getExtras().getString("name");
		//Toast.makeText(this,name,Toast.LENGTH_LONG).show();
		//Toast.makeText(this,sid,Toast.LENGTH_LONG).show();
		new Thread(){
			public void run(){
				try
				{
					File f=File.createTempFile("musiccache", ".mp3",MainActivity.MusicListTXT.getParentFile());
					URL url=new URL(sid);
					InputStream inputStream= url.openStream();
					byte[] bytes=new byte[16];
					int read;
					if(sid.indexOf(1359058327+"")<0){
					FileOutputStream fileOutputStream=new FileOutputStream(f);
					while (inputStream.read(bytes) != -1)
					{
						fileOutputStream.write(bytes);
					}	
					/*while ((read=inputStream.read()) != -1)
					{
						fileOutputStream.write(read);
					}*/
					inputStream.close();
					fileOutputStream.flush();
					fileOutputStream.close();
					mp.setDataSource(f.getAbsolutePath());
					mp.prepare();
					mp.start();
					FileWriter tempf=new FileWriter(f.getParentFile().getAbsolutePath()+"/MusicList2.txt");
					//tempf.write(name);
					tempf.write(sid);
					tempf.flush();tempf.close();
					f.delete();
					}else{
						mp.setDataSource(f.getParentFile().getAbsolutePath()+"/水上灯（赤绫）（Cover：Braska） - 溱绫西陌,乐正绫,赤羽.mp3");
						mp.prepare();
						mp.start();
					}
				}catch (Exception e){
					Toast.makeText(PlayingService.this,e.toString(),Toast.LENGTH_LONG).show();
				}
			}
		}.start();
		// 1. 设置 PendingIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(PlayingService.this, 0, new Intent(PlayingService.this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
// 2.将 PendingIntent 加入到 通知动作里
		Notification notification = new
			Notification(R.mipmap.ic_launcher,name,System.currentTimeMillis());
//		notification.setLatestEventInfo(PlayingService.this,name,sid.substring(sid.indexOf("=")+1), pendingIntent);
		NotificationCompat.Builder nb=new NotificationCompat.Builder(this).
				setContentTitle(name).
				setContentText(sid.substring(sid.indexOf("=")+1)).
				setSmallIcon(R.mipmap.ic_launcher);
		startForeground(1,nb.build());
		/*new Thread(){
			public void run(){
				while(true){
					if(!mp.isPlaying()){
						stopForeground(true);
						stopSelf();
					}
				}
				
			}
		}.start();*/
		return super.onStartCommand(intent, flags, startId);
		
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		wakeLock.release();
		super.onDestroy();
	}
	
}
