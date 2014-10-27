package com.beekay.astra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.beekay.jtest.R;


import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Attendance extends ActionBarActivity {

	
	private TextView only;
	private TableLayout tl;
	private String att="http://borealis.astra.edu.in/index.php?option=com_base_attendancereport&Itemid=98";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance);
		ActionBar ab=getSupportActionBar();
		ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399ff")));
		ab.setDisplayHomeAsUpEnabled(true);
		only=(TextView)findViewById(R.id.only);
		GetAtt atndnce=new GetAtt();
		atndnce.execute(att);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attendance, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class GetAtt extends AsyncTask<String, String, Element>
	{
		private  List<String> cookies;


		private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36";

		private HttpURLConnection conn;
		
		private  int BUFF=50;


		@Override
		protected Element doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url=params[0];
			CookieHandler.getDefault();
			GetAtt ga=new GetAtt();
			String page=ga.getPageContent(url);
			Element table=ga.getTable(page);
			
			return table;
		}

		private Element getTable(String page) {
			// TODO Auto-generated method stub
			
			ArrayList<String> rows=new ArrayList<String>();
			Document doc=Jsoup.parse(page);
			Element table=doc.select("table[width=75%]").first();
			Iterator<Element> ite=table.select("tr").select("td").iterator();
			while(ite.hasNext())
			{
			String	element=ite.next().text();
				rows.add(element);
				Log.v("test",element);
			}
			System.out.print(rows);
			
			return table;
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
		protected void onPostExecute(Element result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ProgressBar prog=(ProgressBar)findViewById(R.id.prog);
			prog.destroyDrawingCache();
			prog.setVisibility(ProgressBar.GONE);
			Log.v("got", result.toString());
			only.setText(result.select("th").last().text());
		}
		
	}

}
