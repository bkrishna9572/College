package com.beekay.astra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.beekay.jtest.R;
import com.beekay.jtest.R.id;
import com.beekay.jtest.R.layout;
import com.beekay.jtest.R.menu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LibraryTransactions extends ActionBarActivity {
	
	private TextView library;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library_transactions);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399ff")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Library lib=new Library();
		lib.execute("http://borealis.astra.edu.in/index.php?option=com_base_library_transactions&Itemid=264");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.library_transactions, menu);
		return true;
	}
	
	public class Library extends AsyncTask<String, String, String>{

		private static final int BUFF = 50;
		private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36";
		private HttpURLConnection conn;
		private List<String> cookies;
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url=params[0];
			CookieHandler.getDefault();
			Library l=new Library();
			String page=l.getPageContent(url);
			System.out.println(page);
			String text=l.getText(page);
			return text;
		}
		private String getText(String page) {
			// TODO Auto-generated method stub
			Document doc=Jsoup.parse(page);
			String text=doc.select("div.infomessage").text();
			if(text!=null)
			return text;
			else
				return null;
		}
		private String getPageContent(String url){
			// TODO Auto-generated method stub
			try{
			URL obj=new URL(url);
			
			conn=(HttpURLConnection)obj.openConnection();
			conn.setChunkedStreamingMode(20);
			conn.setRequestMethod("GET");
			conn.setUseCaches(true);
			conn.setRequestProperty("Host", "borealis.astra.edu.in");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language","en-US,en;q=0.8");
			
			if(cookies!=null)
				{
				for(String cookie:this.cookies){
				
				conn.addRequestProperty("cookie", cookie.split(";",1)[0]);
			}
				}
			int responseCode=conn.getResponseCode();
			System.out.println("sending get request "+url);
			System.out.println("response code is "+responseCode);
			BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()),BUFF);
			String inputLine;
			StringBuffer response=new StringBuffer();
			while((inputLine=in.readLine())!=null){
					response.append(inputLine);
					inputLine=null;
				}
			
			in.close();
			setCookies(conn.getHeaderFields().get("Set-Cookie"));
			System.out.println(response.toString());
			return response.toString();
			}catch(Exception e){
				Log.d("error", e.toString());
			}
			return null;
		}

		private void setCookies(List<String> cookies) {
			// TODO Auto-generated method stub
			this.cookies=cookies;
			
		}
		public List<String> getCookies(){
			return cookies;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			ProgressBar prog=(ProgressBar)findViewById(R.id.lprog);
			prog.destroyDrawingCache();
			prog.setVisibility(ProgressBar.GONE);
			super.onPostExecute(result);
			library=(TextView)findViewById(R.id.libview);
			library.setText(result);
		}
		
	}


}
