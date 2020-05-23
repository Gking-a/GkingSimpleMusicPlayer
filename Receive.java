package com.gking.simplemusicplayer;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.net.*;
public class Receive extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)

	{
		// TODO: Implement this met
		super.onCreate(savedInstanceState);
		Intent ti=getIntent();
		String action=ti.getAction();
		String type=ti.getType();
		String ts = null;
		if (Intent.ACTION_SEND.equals(action) && type != null){
			ts=ti.getStringExtra(Intent.EXTRA_TEXT);
		}
		//Format SWQ
		String its = null;
		if(ts.indexOf("&userid=")>-1){
			its=ts.substring(ts.indexOf("?id=")+4,ts.indexOf("&userid="));
		}//Format Menoy Wang
		else if(ts.indexOf("/?userid=")>-1){
			ts=ts.substring(ts.indexOf("http"));
			ts=ts.substring(0,ts.indexOf("(来自@网易云音乐)"));
			its=ts.substring(ts.indexOf("song/")+5,ts.indexOf("/?userid"));
		}//Format Gking
		else{
			its=ts.substring(ts.indexOf("id=")+3);
		}
		final String sid=its;
		final String s=ts;
		//int id=Integer.valueOf(sid);
		try
		{
			new Thread(){
				public void run(){
					try
					{
						URL url=new URL(s);
						BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
						String line;
						while((line=br.readLine())!=null){
							int startp;
							if((startp=line.indexOf("<title>"))>=0){
								int endp=line.indexOf("-");
								line=line.substring(startp+7,endp-1);
								break;
							}
						}
						FileWriter fw=new FileWriter(MainActivity.MusicListTXT,true);
						fw.write("\n[Gking:name]*"+line);
						fw.write("\n[Gking:id]*"+sid);
						fw.flush();
						fw.close();
						Intent inte=new Intent(Receive.this,MainActivity.class);
						inte.putExtra("b","f");
						// startActivity(inte);
						 finish();
					}
					catch (Exception e)
					{}
			}}.start();
			Intent inte=new Intent(this,MainActivity.class);
			startActivity(inte);
			finish();
			}
		catch (Exception e)
		{Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();}
	}
	
}
