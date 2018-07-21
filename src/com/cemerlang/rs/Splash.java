package com.cemerlang.rs;

import com.cemerlang.rs.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				   Intent a=new Intent(Splash.this,Utama.class);
				   a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				   startActivity(a);
				   finish();
			}
			
		}, 1200);
	}

 

}
