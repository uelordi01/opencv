package utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrivateFileRW 
{
	private Context mContext;
	private static final String LOG_TAG="PrivateFileRW";
	public PrivateFileRW()
	{
		mContext=null;
	}
	public PrivateFileRW(Context context)
	{
		mContext=context;
	}
	public void writeFileToPrivateStorage(int fromFile, String toFile) 
	{
		 InputStream is = mContext.getResources().openRawResource(fromFile);
		 int bytes_read;
		 byte[] buffer = new byte[4096]; 
		 
		 try 
		 {
			FileOutputStream fos = mContext.openFileOutput(toFile, Context.MODE_PRIVATE);
			
			while ((bytes_read = is.read(buffer)) != -1)		    
		        fos.write(buffer, 0, bytes_read); // write
			
			fos.close();
			is.close();
		    
		} catch (IOException e)
		 {			
			e.printStackTrace();
		}		 		
	}	
	public String getPrivateDirectoryFilesPath(String inputfile)
	{
		String path;
		String absolute_path;
		path=mContext.getApplicationContext().getFilesDir().toString();
		absolute_path=path+"/"+inputfile;
		//IS DIRECTORY:
		 File temp_file = new File(absolute_path);
		 if(temp_file.exists())
		 {
	         Uri data = Uri.fromFile(temp_file);
	         //String type = getMimeType(data.toString());
	    	 Log.v(LOG_TAG, "file exists "+path);
			 return path;
		
		 }
		 else
		 {
			 Log.v(LOG_TAG, "file doesn't exist");
			 return null;
		 }

	}
	private String getMimeType(String string) {

		return null;
	}
}
