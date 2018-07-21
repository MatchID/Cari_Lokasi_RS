package com.cemerlang.rs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cemerlang.rs.R;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class RS_Detail extends Activity {

	TextView nama, jarak, alamat, kontak, layanan;
	ImageView gambar;
	RelativeLayout peta;
	String kode, lat, lon,
			link_url="http://s1creative.com/cemerlang/gis-rs/web/android/detail_rs.php";
    JSONArray artikel = null;
	static LatLng AWAL;
	double latitude ,longitude ;
	GpsService	gps;
	Geocoder gcd;
	
    @TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint({ "CutPasteId", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_rs);
		
		nama = (TextView) findViewById(R.id.nama);		
		jarak = (TextView) findViewById(R.id.jarak);		
		alamat = (TextView) findViewById(R.id.alamat);		
		kontak = (TextView) findViewById(R.id.kontak);		
		layanan = (TextView) findViewById(R.id.layanan);
		
		gambar = (ImageView) findViewById(R.id.gambar);

		peta = (RelativeLayout) findViewById(R.id.peta);
		
		if (android.os.Build.VERSION.SDK_INT > 9)
		 {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		 }
		

		gps = new GpsService(RS_Detail.this);
		if (gps.canGetLocation())
		{

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			AWAL = new LatLng(latitude,longitude);
			    			
		} else
		{
			gps.showSettingAlert();
		}
		
		Intent in = getIntent();
        kode = in.getStringExtra("Id_Rs");
        
        //mempersiapkan koneksi JSON
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.AmbilJson(link_url + "?Id_Rs=" + kode);
 
      //memasukkan data dari file JSON ke penampungan
        try {
            artikel = json.getJSONArray("data_rs");

            for(int i = 0; i < artikel.length(); i++){
                JSONObject ar = artikel.getJSONObject(i);

            	LatLng POSISI = new LatLng(Double.parseDouble(ar.getString("Lat")),Double.parseDouble(ar.getString("Lon")));

    	    	MapDirection hasiljarak = new MapDirection();

    	    	int jarak_ = (hasiljarak.DistanceInfo(AWAL, POSISI));
                
                nama.setText(ar.getString("Nama_Rs"));
                alamat.setText(ar.getString("Alamat_Rs"));
                jarak.setText(String.valueOf(jarak_) + " M");
                kontak.setText(ar.getString("Kontak"));
                layanan.setText(ar.getString("Layanan_Rs"));
                lat = ar.getString("Lat");
                lon = ar.getString("Lon");
    	        Picasso.with(this).load(ar.getString("Gambar")).into(gambar);
            }
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
        


        peta.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent peta = new Intent(RS_Detail.this, Peta.class);
				peta.putExtra("peta", "1");
				peta.putExtra("lat", lat);
				peta.putExtra("lon", lon);
				startActivity(peta);
			}
        });

		
		findViewById(R.id.sinb).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
