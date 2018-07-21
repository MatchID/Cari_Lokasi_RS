package com.cemerlang.rs;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.cemerlang.rs.R;
import com.cemerlang.rs.ListView.ListViewRS;
import com.cemerlang.rs.ListView.ListViewRSTerdekat;
import com.google.android.gms.maps.model.LatLng;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class List_RS_Terdekat extends Activity {
	ListView list;
    JSONArray artikel = null;
    ListViewRSTerdekat adapter;
    ArrayList<HashMap<String, String>> daftar_rs = new ArrayList<HashMap<String, String>>();

    String link_url = "http://s1creative.com/cemerlang/gis-rs/web/android/list_rs.php";

	static LatLng AWAL;
	double latitude ,longitude ;
	GpsService	gps;
	Geocoder gcd;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint({ "CutPasteId", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_rs_terdekat);
		
		if (android.os.Build.VERSION.SDK_INT > 9)
		 {
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		 }
		
		gps = new GpsService(List_RS_Terdekat.this);
		if (gps.canGetLocation())
		{

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			AWAL = new LatLng(latitude,longitude);
			    			
		} else
		{
			gps.showSettingAlert();
		}
		
		list=(ListView)findViewById(R.id.listView1);

        list.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	            String kode = ((TextView) view.findViewById(R.id.kode)).getText().toString();
	                //mempersiapkan untuk membuka activity baru berdasarkan ID Kategori
	                Intent in = new Intent(List_RS_Terdekat.this, RS_Detail.class);
	            in.putExtra("Id_Rs", kode);
	            startActivity(in);
	        }
        });

		
		findViewById(R.id.sinb).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		data_cari();
		
	}

    private void data_cari(){

      //mempersiapkan koneksi JSON
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.AmbilJson(link_url);
        
    	//memasukkan data dari file JSON ke penampungan
    	try {
       		 //mempersiapkan koneksi JSON	               
               artikel = json.getJSONArray("data_rs");

               for(int i = 0; i < artikel.length(); i++){
                   JSONObject ar = artikel.getJSONObject(i);                   

               	LatLng POSISI = new LatLng(Double.parseDouble(ar.getString("Lat")),Double.parseDouble(ar.getString("Lon")));

       	    	MapDirection hasiljarak = new MapDirection();

       	    	int jarak_ = (hasiljarak.DistanceInfo(AWAL, POSISI));
       	    	
       	    	if(jarak_ <= 5000 ){
       	    	
                   HashMap<String, String> map = new HashMap<String, String>();

                   map.put("id", ar.getString("Id_Rs"));
                   map.put("nama", ar.getString("Nama_Rs"));
                   map.put("alamat", ar.getString("Alamat_Rs"));
                   map.put("gambar", ar.getString("Gambar"));
                   map.put("jarak", String.valueOf(jarak_));

                   daftar_rs.add(map);
       	    	}
               }
        } 
    	catch (JSONException e) {
            e.printStackTrace();
        }
    	
        adapter=new ListViewRSTerdekat(List_RS_Terdekat.this, daftar_rs); 
        list.setAdapter(adapter);
    	
    }

}
