package com.beekay.astra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beekay.jtest.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ProgressBarICS;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends ActionBarActivity {
	
	
	ListView lv;
	RelativeLayout rl;
	List<String> categories=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	String user,pass;
	TextView tv;
	List<String> list=new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399ff")));
		
		if(savedInstanceState==null){
		Bundle extra=getIntent().getExtras();
		if(extra!=null){
			Log.v("got", extra.getString("user")+extra.getString("pass"));
			user=extra.getString("user");
			pass=extra.getString("pass");
			getSupportActionBar().setTitle(user.toUpperCase(Locale.US));
		}
		HomeList homelist=new HomeList();
		
		
		
			homelist.execute(user+pass);
		}
		else
		{
			ProgressBar prog=(ProgressBar)findViewById(R.id.hprog);
			prog.destroyDrawingCache();
			prog.setVisibility(ProgressBar.GONE);
			categories=savedInstanceState.getStringArrayList("cat");
			adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, categories);
			lv=(ListView)findViewById(R.id.list);
			lv.setAdapter(adapter);

		}
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	public class HomeList extends AsyncTask<String,String,ArrayList<String>> {

		private  List<String> cookies;
		private HttpURLConnection conn;
		private final String USER_AGENT="Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36";
		private Home home;
		private  int BUFF=50;
		String username;
		List<String> cook=new ArrayList<String>();
		
			


			@Override
			
			 
			 protected ArrayList<String> doInBackground(String... arg0) {
					// TODO Auto-generated method stub
				 	publishProgress("Loading");
					String user=arg0[0].substring(0,10);
					Log.v("user", user);
					String pass=arg0[0].substring(10,arg0[0].length());
					Log.v("pass", pass);
					ArrayList<String> no=new ArrayList<String>();
					no.add("rejected");
					
					String url = "http://borealis.astra.edu.in";
					//action url in form
					
					HomeList coll=new HomeList();
					//for cookies
					CookieHandler.setDefault(new CookieManager());
					
					//get form data to be sent
					String page=coll.GetPageContent(url);
					//collecting form data
					
					String postParams = coll.getFormParams(page,user,pass);
					System.out.println(postParams);
					ArrayList<String> result=coll.sendPost(url,postParams);
					if(result.contains("error"))
						return no;
					else
					return result;
					}
				

			private ArrayList<String> sendPost(String url, String postParams) {
				// TODO Auto-generated method stub
				
				URL obj;
				try {
					obj = new URL(url);
				
				conn=(HttpURLConnection)obj.openConnection();
				conn.setInstanceFollowRedirects(false); 
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Host", "borealis.astra.edu.in");
				conn.setRequestProperty("User-Agent", USER_AGENT);
				conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				conn.setRequestProperty("Accept-Language","en-US,en;q=0.8");
				for(String cookie:this.cookies){
					conn.setRequestProperty("cookie", cookie.split(";",1)[0]);
					System.out.println(cookie);
				}
				conn.setRequestProperty("Connection","keep-alive");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				DataOutputStream wr=new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(postParams);
				wr.flush();
				wr.close();
				int responseCode=conn.getResponseCode();
				System.out.println(conn.getURL());
				System.out.println("posting data to "+url);
				System.out.println("parametrs are "+postParams);
				System.out.println("response Code "+responseCode);
			
				URL secondURL = new URL(conn.getHeaderField("Location"));
				conn=(HttpURLConnection) secondURL.openConnection();

				BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()),BUFF);

				System.out.println("redirected url" + conn.getURL());
				String inputLine;
				StringBuffer response=new StringBuffer();
				while((inputLine=in.readLine())!=null){
					response.append(inputLine);
					inputLine=null;
					}
				
				in.close();
				Document doc=Jsoup.parse(response.toString());
				
				Elements table=doc.getElementsByClass("tiles_box");
				
				ArrayList<String> ele=new ArrayList<String>(); 
				if(table.first()!=null){
					
					Iterator<Element> ite=table.select("tr").select("td").iterator();
					ele.add(ite.next().text());
					
					while(ite.hasNext()){
						String td=ite.next().text();
						if(!ele.contains(td)){
							ele.add(td);
							
							}
					}
					return ele;
				}
			
			else
			{
				ArrayList<String> error=new ArrayList<String>();
				error.add("error");
				return error;
			
			}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			


			private String getFormParams(String html, String user, String pass){
				// TODO Auto-generated method stub
				try{
					
				System.out.println("extracting form data");
				Document doc=Jsoup.parse(html);
				html=null;
				Element form=doc.getElementById("form-login");
				Elements inputElements=form.getElementsByTag("input");
				List<String> paramsList=new ArrayList<String>();
				for(Element inputElement:inputElements){
					String key=inputElement.attr("name");
					String value=inputElement.attr("value");
					if(key.equals("username"))
						value=user;
						if(key.equals("passwd"))
							value=pass;
							paramsList.add(key+"="+URLEncoder.encode(value,"UTF-8"));
				}
						StringBuilder result=new StringBuilder();
						for(String param:paramsList){
							if(result.length()==0){
								result.append(param);
							}else{
								result.append("&"+param);
							}
						}
						return result.toString();
				}catch(Exception e){
					Log.d("error", e.toString());
				}
				return null;
				
			}
			
			private String GetPageContent(String url){
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
			protected void onProgressUpdate(String... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			
			}
			@Override
			protected void onPostExecute(ArrayList<String> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result.contains("rejected")){
					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
					finish();
				}
				else{
					categories=result;
					adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.list_layout, result);
					lv=(ListView)findViewById(R.id.list);
					lv.setAdapter(adapter);
					ProgressBar prog=(ProgressBar)findViewById(R.id.hprog);
					prog.destroyDrawingCache();
					prog.setVisibility(ProgressBar.GONE);
					lv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long id) {
							// TODO Auto-generated method stub
							switch(position)
							{
							case 0:Intent att=new Intent(Home.this,Attendance.class);
							if(isNetAvailable())
							startActivity(att);
							else
								Toast.makeText(getApplicationContext(), "Please turn on the internet", Toast.LENGTH_LONG).show();
							break;
							case 2:Intent marks=new Intent(Home.this,Marks.class);
							if(isNetAvailable())
							startActivity(marks);
							else
								Toast.makeText(getApplicationContext(), "Please turn on the internet", Toast.LENGTH_LONG).show();
							break;
							case 1:Intent tt=new Intent(Home.this,Attendance.class);
							if(isNetAvailable())
							startActivity(tt);
							else
								Toast.makeText(getApplicationContext(), "Please turn on the internet", Toast.LENGTH_LONG).show();
							break;
							case 5:Intent lt=new Intent(Home.this,LibraryTransactions.class);
							if(isNetAvailable())
							startActivity(lt);
							else
								Toast.makeText(getApplicationContext(), "Please turn on the internet", Toast.LENGTH_LONG).show();
							break;
							
							}
						}
					});
				
			}
				
	}
			private boolean isNetAvailable() {
				// TODO Auto-generated method stub
				ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo net=cm.getActiveNetworkInfo();
				return net!=null&&net.isConnected();
			}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		
			outState.putStringArrayList("cat", (ArrayList<String>) categories);
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
	}
	
	
}
