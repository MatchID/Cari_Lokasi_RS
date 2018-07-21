package com.cemerlang.rs;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cemerlang.rs.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class Peta extends Activity implements OnMapLongClickListener, OnInfoWindowClickListener, OnMarkerClickListener{
	
	static LatLng AWAL;
	ArrayList<HashMap<String, String>> dataMap = new ArrayList<HashMap<String, String>>();
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	JSONArray str_json = null;
	float totalDistance =0;
	ArrayList<HashMap<Integer, LatLng>> listjarak = new ArrayList<HashMap<Integer, LatLng>>();
	List<Integer> listjarak_ = new ArrayList<Integer>();
	HashMap<Integer, LatLng> mapa = new HashMap<Integer, LatLng>();
	LatLng ASAL = null;
	String cek = "0";
    SQLiteDatabase db=null;
    ClassUrl ur=new ClassUrl();
    Utama pt=new Utama();
    private static String link_url = "list_rs.php";

	double latitude ,longitude ;
	
	ArrayList<LatLng> markerPoints;

	GpsService	gps;
	Geocoder gcd;
    Button submit,keluar;
	
	class getListInfo extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();           
            pDialog = new ProgressDialog(Peta.this);
            pDialog.setMessage("Loading Peta...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}

		protected String doInBackground(String... args) {

			JSONObject json = jParser.AmbilJson(ur.link()+link_url);

			try {
				str_json = json.getJSONArray("data_rs");
				
				for(int i = 0; i < str_json.length(); i++)
				{
					JSONObject ar = str_json.getJSONObject(i);
					HashMap<String, String> map = new HashMap<String, String>();

					map.put("Nama_Rs", ar.getString("Nama_Rs"));
					map.put("Alamat_Rs", ar.getString("Alamat_Rs"));
					map.put("Lat",  ar.getString("Lat"));
					map.put("Lon",  ar.getString("Lon"));
					map.put("Id_Rs",  ar.getString("Id_Rs"));
					map.put("Gambar",  ar.getString("Gambar"));

					dataMap.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
                	
            for (int i = 0; i < dataMap.size(); i++)
            {
            	HashMap<String, String> map = new HashMap<String, String>();
            	map = dataMap.get(i);
            	
            	LatLng POSISI = new LatLng(Double.parseDouble(map.get("Lat")),Double.parseDouble(map.get("Lon")));

    	    	MapDirection hasiljarak = new MapDirection();

    	    	int jarak = (hasiljarak.DistanceInfo(AWAL, POSISI));
    	    	
    	    	//if(jarak == 0){
        	    	//hasiljarak = new MapDirection();
        	    	//jarak = (hasiljarak.DistanceInfo(AWAL, POSISI));
    	    	//}else{
                	MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(POSISI)
                            .title(map.get("Nama_Rs")+"--"+map.get("Alamat_Rs"))
                            .snippet(map.get("Id_Rs")+"--"+map.get("Gambar")+"--"+jarak+"--"+map.get("Koridor"));
                            //.icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));
                    //Marker m = 
                	myMap.addMarker(markerOptions);
    	    	//}
                //m.setTag(info);
                //m.showInfoWindow();

            }
            pDialog.dismiss();
                        
           
		}

	}
	
	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	TextView tvLocInfo;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peta);

		//Toast.makeText(getApplicationContext(), " "+pt.st_lat.toString()+ " /" + pt.st_lon.toString(),Toast.LENGTH_LONG).show();
        //mengecek versi android untuk membuka koneksi internet
        if (android.os.Build.VERSION.SDK_INT > 9)
	 {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	 }
		gps = new GpsService(Peta.this);
		if (gps.canGetLocation())
		{

			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			AWAL = new LatLng(latitude,longitude);
			    			
		} else
		{
			gps.showSettingAlert();
		}
        
        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment  = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
        myMap = myMapFragment.getMap();
        
        myMap.setMyLocationEnabled(true);
        
        myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        myMap.getUiSettings().setAllGesturesEnabled(true);
        myMap.setTrafficEnabled(true);
        
        myMap.setOnMapLongClickListener(this);
        myMap.setOnInfoWindowClickListener(this);
        
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AWAL, 1));
        myMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        
        new getListInfo().execute();
        myMap.setOnMarkerClickListener(this);

		Intent in = getIntent();
        
        if(in.getStringExtra("peta").equals("1")){	        
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			
	    	LatLng ASAL = new LatLng(latitude,longitude); 
	    	LatLng TUJUAN = new LatLng(Double.parseDouble(in.getStringExtra("lat")),Double.parseDouble(in.getStringExtra("lon")));
			
			String url = getDirectionsUrl(ASAL, TUJUAN);
			DownloadTask downloadTask = new DownloadTask();
			
			downloadTask.execute(url);
        }
		
		findViewById(R.id.sinb).setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		
		if (resultCode != ConnectionResult.SUCCESS)
		{
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		//String kode = marker.getPosition();
        //Intent in = new Intent(Peta_wisata.this, Detail_wisata.class);
        //in.putExtra("id", "peta");
        //in.putExtra("latlng",  marker.getPosition().latitude +","+marker.getPosition().longitude);
        //startActivity(in);
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
        if(marker.getSnippet() == null){
        	myMap.moveCamera(CameraUpdateFactory.zoomIn());
            return true;
        }
        
        String string = marker.getTitle();
        String[] parts = string.split("--");
        final String part1 = parts[0]; // 004
        final String part2 = parts[1];

        String stringb = marker.getSnippet();
        String[] partsb = stringb.split("--");
        final String part1b = partsb[0]; // 004
        final String part2b = partsb[1];
        final String part3b = partsb[2];
        final String part4b = partsb[3];
        
        //arg0.showInfoWindow();
        final Dialog d = new Dialog(Peta.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //d.setTitle("Select");
        
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        d.setContentView(R.layout.maps_custom_infowindow);
        
        TextView tvName = (TextView)d.findViewById(R.id.infocontent_tv_name);
        tvName.setText(part1);
        
        TextView textView2 = (TextView)d.findViewById(R.id.textView2);
        textView2.setText("Jarak dari anda "+ part3b +" Meter");
        
        ImageView infocontent_iv_image = (ImageView)d.findViewById(R.id.infocontent_iv_image);
        Picasso.with(getApplicationContext()).load(part2b).into(infocontent_iv_image);

        Button button1 = (Button)d.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        //Intent in = new Intent(Peta_wisata.this, Detail_wisata.class);
		        //in.putExtra("id", part1b);
		        //startActivity(in);
		        Intent in = new Intent(Peta.this, RS_Detail.class);
		        in.putExtra("Id_Rs", part1b);
		        startActivity(in);
			}

			private String valueOf(double latitude) {
				// TODO Auto-generated method stub
				return null;
			}
        	
        });

        Button button2 = (Button)d.findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myMap.clear();
		        new getListInfo().execute();
		        
				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();
				
		    	LatLng ASAL = new LatLng(latitude,longitude);
		    	LatLng TUJUAN = marker.getPosition();
				
				String url = getDirectionsUrl(ASAL, TUJUAN);
				DownloadTask downloadTask = new DownloadTask();
				
				downloadTask.execute(url);
				d.cancel();
			}
        	
        });
        
		d.show();
		return true;
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest){
		
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
		String sensor = "sensor=false";			
		String waypoints = "";
		
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		return url;
	}
	
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }

	
	
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		@Override
		protected String doInBackground(String... url) {
				
			String data = "";
					
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			parserTask.execute(result);
				
		}		
	}
	
	public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;		           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	MapDirection parser = new MapDirection();
            	
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				List<HashMap<String, String>> path = result.get(i);
				
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				lineOptions.addAll(points);
				lineOptions.width(8);
				lineOptions.color(Color.BLUE);			
			}
			
			myMap.addPolyline(lineOptions);		
			
		}			
    }   
    
}