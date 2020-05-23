package com.gking.simplemusicplayer;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import android.view.View.*;
import java.net.*;
import android.media.*;
import android.app.Notification;

import com.Gking.SimpleMusicPlayer.R;

public class MainActivity extends Activity
{
	
	public static File MusicListTXT=new File("/mnt/sdcard/Android/data/com.Gking.SimpleMusicPlayer/MusicList.txt");
	ArrayList<String> names=new ArrayList<String>();
	ArrayList<String> id=new ArrayList<String>();
	final String urlpathall="http://music.163.com/song/media/outer/url?id=";
	ListView list;
	PowerManager.WakeLock wakeLock ;
	AudioManager am;
	Thread autoplay=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		am=(AudioManager) getSystemService(AUDIO_SERVICE);
		
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

		 wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
																  "SimpleMusicPlayer:tag2");
		wakeLock.acquire();
		try
		{
			new Thread(){
				public void run(){
					try
					{
						File ff;
						if(new File("/mnt/sdcard/Android/data/com.Gking.SimpleMusicPlayer/MusicList2.txt").exists()){
							File f=File.createTempFile("musiccache", ".mp3",MainActivity.MusicListTXT.getParentFile());
							BufferedReader tempf=new BufferedReader(new InputStreamReader(new FileInputStream ("/mnt/sdcard/Android/data/com.Gking.SimpleMusicPlayer/MusicList2.txt")));
							String path=tempf.readLine();
							URL url=new URL(path);
							InputStream inputStream= url.openStream();
							byte[] bytes=new byte[512];
							int read;
							if(path.indexOf(1359058327+"")<0){
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
								PlayingService.mp.setDataSource(f.getAbsolutePath());
								PlayingService.mp.prepare();
						}else{
							new File("/mnt/sdcard/Android/data/com.Gking.SimpleMusicPlayer/MusicList2.txt").createNewFile();
						}
					}
					}
					catch (Exception e)
					{}
				}
			}.start();
			if(getIntent().getExtras()!=null&& getIntent().getExtras().getString("b")!=null&&getIntent().getExtras().getString("b").equals("f")){
				finish();
			}

			File f=new File("/mnt/sdcard/Android/data/com.Gking.SimpleMusicPlayer");
            if (!f.exists())
            {f.mkdir();}
            for(File c:f.listFiles()){

                if(c.getName().indexOf("cache")>=0){
                    c.delete();
                }
            }
            f = new File(f.getAbsolutePath() + "/MusicList.txt");
            if (!f.exists())
            {f.createNewFile();}
            MusicListTXT=f;


//*****************BufferedReader********************
			String line=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(MusicListTXT)));
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf("[Gking:name]*") > -1)
				{
					names.add(line.substring(13));
				}
				if (line.indexOf("[Gking:id]*") > -1)
				{
					//id.add(Integer.valueOf(line.substring(11)));
					id.add(line.substring(11));
				}
			}
			//Toast.makeText(this,names.size()+""+id.size()+"",Toast.LENGTH_SHORT).show();
			if(names.size()!=0){
			list=(ListView) findViewById(R.id.mainListView);
			ListAdapter la=new MyAdapter(this, names.toArray(new String[names.size()]));
			list.setAdapter(la);}
		}catch (Exception e)
		{Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				am.setStreamVolume(am.STREAM_MUSIC,(int)(am.getStreamVolume(am.STREAM_MUSIC)-0.1),am.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				am.setStreamVolume(am.STREAM_MUSIC,(int)(am.getStreamVolume(am.STREAM_MUSIC)+1),am.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_MUTE:
				
				return true;
        }
        return super.onKeyDown(keyCode, event);
		}
    class MyAdapter extends ArrayAdapter<String>
    {
		public MyAdapter(Context context, String[] values) 
		{
			super(context, R.layout.list, values);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.list, parent, false);

			final String text = getItem(position);
			TextView textView = (TextView) view.findViewById(R.id.listTextView);
			textView.setText(text);
			Button b=(Button)view.findViewById(R.id.listButton);
				String tempid = null;
				String tempname=null;
				for(int i=0;i<names.size();i++){
					if(text.equals(names.get(i))){
						tempid=id.get(i);
						tempname=names.get(i);
						break;
					}
				}
				final String name=tempname;
				final String sid=tempid;
				b.setOnClickListener(new View.OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							//MediaPlayer mp= PlayingService.mp;
							//Toast.makeText(MainActivity.this,String.valueOf(sid),Toast.LENGTH_SHORT).show();
							Intent ss=new Intent(MainActivity.this,PlayingService.class);
							stopService(ss);
							ss.putExtra("mp",urlpathall+sid);
							//Toast.makeText(MainActivity.this,urlpathall+sid,Toast.LENGTH_SHORT).show();
							ss.putExtra("name",name);
							startService(ss);
							
						}
					});
			Button del=(Button) view.findViewById(R.id.listButtondel);
			del.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						
						try
						{
							ArrayList<String> lines=new ArrayList<String>();
							String line=null;
							BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(MusicListTXT)));
							int b=0;
							while ((line = br.readLine()) != null)
							{
								if(line.indexOf("[Gking:name]*"+text)<0&&line.indexOf("[Gking:id]*"+String.valueOf(sid))<0&&b<2){
									lines.add(line);
								}else if(b==2){
									lines.add(line);
								}else{b++;}
							}
							FileWriter fw=new FileWriter(MusicListTXT);
							for(String l:lines.toArray(new String[lines.size()])){
								fw.write(l+"\n");
							}
							fw.flush();fw.close();
						}
						catch (Exception e)
						{}
					}
				});
			return view;
		}
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		MenuInflater i=getMenuInflater();
		i.inflate(R.menu.menu,menu);
		return true;
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		wakeLock.release();
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean isR=false;
		switch(item.getItemId()){
			case R.id.Re:
				try{
					if(isR){
				autoplay.notifyAll();
				}
				}finally{
				PlayingService.mp.start();
				names=null;id=null;
				names=new ArrayList<String>();
				id=new ArrayList<String>();
				try
				{
					String line=null;
					BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(MusicListTXT)));
					while ((line = br.readLine()) != null)
					{
						if (line.indexOf("[Gking:name]*") > -1)
						{
							names.add(line.substring(13));
						}
						if (line.indexOf("[Gking:id]*") > -1)
						{
							id.add(line.substring(11));
						}
					}
					if(names.size()!=0){
						list=(ListView) findViewById(R.id.mainListView);
						ListAdapter la=new MyAdapter(this, names.toArray(new String[names.size()]));
						list.setAdapter(la);}
				}
				catch (Exception e)
				{Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}
				break;
				}
				case R.id.Pa:
					PlayingService.mp.pause();
				try
				{
					autoplay.interrupt();
				}
				catch (Exception e)
				{Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
				}
				break;
				case R.id.St:
				try
				{
					MediaPlayer mp=PlayingService.mp;
					mp.stop();
					isR=false;
					autoplay.interrupt();
				}
				catch (Exception e)
				{
					isR=false;
					autoplay=null;
					Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
				}
						break;
				case R.id.Ra:
				    //rapl();
					try{
					isR=true;
					
				autoplay=new Thread(){
					public void run(){
						try
						{
							MediaPlayer m=PlayingService.mp;
							while(true){
                                if(this.currentThread().isInterrupted()){
                                    break;
                                }
								if(!m.isPlaying()){
									rapl();
									sleep(1000 * 10);
								}else{
									sleep(1000*10);
								}
							}
						}
						catch (InterruptedException e)
						{}
					}
				};
					autoplay.start();
					
					break;
					}catch(Exception e){
						Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
					}
				}
		return true;
	}
	private void rapl()
	{
		try
		{
			int sid= (int) (Math.random() * (id.size() - 0 + 1)) + 0;
			Intent ss=new Intent(MainActivity.this, PlayingService.class);
			stopService(ss);
			ss.putExtra("mp", urlpathall + id.get(sid)+"");
			ss.putExtra("name",names.get(sid));
			startService(ss);
			
		}
		catch (Exception e)
		{Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();}
	}
}
