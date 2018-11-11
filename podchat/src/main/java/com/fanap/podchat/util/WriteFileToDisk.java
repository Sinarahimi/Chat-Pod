package com.fanap.podchat.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

import static com.fanap.podchat.util.FileUtils.TAG;

public class WriteFileToDisk extends AsyncTask<Boolean, Void, String> {

    private ResponseBody body;
    private String fileName;
    private Context context;
    private String localPath;

    public WriteFileToDisk(ResponseBody body, String fileName, Context context) {
        this.fileName = fileName;
        this.body = body;
        this.context = context;
    }

    //TODO Should Create the Folder once
    private String writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File directory = context.getDir("fChatPlatform", Context.MODE_PRIVATE);
            File file = new File(directory, fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();


                return file.getPath();
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

//    @Override
//    protected Boolean doInBackground(Boolean... booleans) {
//        return writeResponseBodyToDisk(body, fileName);
//    }


    @Override
    protected String doInBackground(Boolean... booleans) {
        return writeResponseBodyToDisk(body, fileName);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        setLocalPath(s);
    }

    public String getLocalPath(){
        return localPath;
    }

    private void setLocalPath(String localPath){
        this.localPath = localPath;
    }
}
