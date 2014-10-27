package com.beekay.astra;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.beekay.jtest.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

public class Login extends ActionBarActivity {
	AutoCompleteTextView user;
	AutoCompleteTextView pass;
	Button login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if(isNetAvailable()){
			Marquee mar=new Marquee();
			mar.execute("http://www.astra.edu.in");
			Log.v("sent", "sent");
		}
		user=(AutoCompleteTextView)findViewById(R.id.username);
		pass=(AutoCompleteTextView)findViewById(R.id.password);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399ff")));
		
		login=(Button)findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			Intent i=new Intent(Login.this,Home.class);
			String name=user.getText().toString();
			String word=pass.getText().toString();
			if(name.length()>0&&word.length()>0){
				if(isNetAvailable()){
			i.putExtra("user", name);
			i.putExtra("pass", word);
			startActivity(i);
				}
				else
					Toast.makeText(getApplicationContext(), "no internet",Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "yo enter some thing", Toast.LENGTH_LONG).show();
			}
			}

			
		});
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first_page, menu);
		return true;
		
	}
	private boolean isNetAvailable() {
		// TODO Auto-generated method stub
		ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net=cm.getActiveNetworkInfo();
		return net!=null&&net.isConnected();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		finish();
		return super.onOptionsItemSelected(item);
	}
	public class Marquee extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			String url=arg0[0];
			Log.v("got", url);
			try {
				Document doc=Jsoup.connect(url).get();
				Log.v("got", doc.text());
				Element marqe=doc.getElementById("pathway-text").select("script").first();
				
				Log.v("got", marqe.toString());
				if(marqe!=null)
					return marqe.toString();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			TextView mark=(TextView)findViewById(R.id.notification);
			mark.setText(result);
			mark.setSelected(true);
			
			super.onPostExecute(result);
		}
		
	}
}
