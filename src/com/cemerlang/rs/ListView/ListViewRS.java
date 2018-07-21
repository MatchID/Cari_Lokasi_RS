package com.cemerlang.rs.ListView;

import java.util.ArrayList;
import com.cemerlang.rs.R;
import java.util.HashMap;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ListViewRS extends BaseAdapter {
    
    Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    
    public ListViewRS(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null) {
		} inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.isi_list_rs, null);

	        TextView judul=(TextView)vi.findViewById(R.id.judul);
	        TextView deskripsi=(TextView)vi.findViewById(R.id.content);
	        TextView kode=(TextView)vi.findViewById(R.id.kode);
	        ImageView gambar=(ImageView)vi.findViewById(R.id.imageView1);
	        
	        HashMap<String, String> artikel = new HashMap<String, String>();
	        artikel = data.get(position);

	        judul.setText(artikel.get("nama"));
	        deskripsi.setText(artikel.get("alamat"));
	        kode.setText(artikel.get("id"));
	        
	        Picasso.with(activity).load(artikel.get("gambar")).into(gambar);
	        
	        return vi;
    }
}