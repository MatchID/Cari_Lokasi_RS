package com.cemerlang.rs;
 
import com.cemerlang.rs.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Utama extends Activity {
static String pilih, st_lat,st_lon="";
GpsService	gps;
String ID, PS;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.utama);
		
		aktifGps();
		

		
		
		findViewById(R.id.button4).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			 
			   Intent a=new Intent(Utama.this,List_RS_Terdekat.class);
			   a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			   startActivity(a);
				 
			}
		});
		findViewById(R.id.button2).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				
			   Intent a=new Intent(Utama.this,List_RS.class);
			   a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			   startActivity(a);
			 
			}
		});	
		findViewById(R.id.button3).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
			   Intent a=new Intent(Utama.this,Peta.class);
			   a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			   a.putExtra("peta", "0");
			   startActivity(a);
			 
			}
		});		
		findViewById(R.id.button1).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			 
			   Intent a=new Intent(Utama.this,About.class);
			   a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			   startActivity(a);
			 
			}
		});
	
	}

	 public void aktifGps(){
			gps = new GpsService(Utama.this);
			if (gps.canGetLocation())
			{
				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();
				st_lat=""+ gps.getLatitude();
				st_lon=""+gps.getLongitude();			
				
			} else
			{
				gps.showSettingAlert();
			}
	 }

}
