package inm7.JTrack.Jtrack_Social.ActiveLabeling;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.Serverinterface;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UploadFiles {

    private Context mContext;
    String TAG = "uploadFile";
    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";

    public UploadFiles(Context context) {
        mContext = context;

    }

    // added after room integration
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.serveraddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Serverinterface serverinterface = retrofit.create(Serverinterface.class);





//    @RequiresApi(api = Build.VERSION_CODES.O)
    public void  getFiles(){

        //**
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        // get device id if null use username
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);


        ContextWrapper cw = new ContextWrapper(mContext);
        String fullPath =cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

//        String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)  + "/JTrack/";

//       File directory = new File(fullPath);
//
        String mediaPath = "";

        while (countDirectoryFilesByExtention(directory,".3gp")>0) {
            startZip();
        }



            for (File f : directory.listFiles()) {

                String fileExtention = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."));


                if (f.isFile() && fileExtention.equals(".zip")) {
                    mediaPath = f.getName();
                    Log.d(TAG, mediaPath + "will be uploaded!");
                    try {
                        upload(f);
                    } catch (IOException e) {
                        Log.d(TAG, "there is a problem in encoding");
                    }
                }

            }




//            if (directory.listFiles().length>0)
//        {
//            int count = countDirectoryFilesByExtention(directory,".zip");
//
//            for (File f : directory.listFiles()) {
//
//                String fileExtention = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."));
//
//
//                if (f.isFile() && count>0 && fileExtention.equals(".zip")) {
//                    mediaPath = f.getName();
//                    Log.d(TAG, mediaPath + "will be uploaded!");
//                    try {
//                        upload(f);
//                        count--;
//                    } catch (IOException e) {
//                        Log.d(TAG, "there is a problem in encoding");
//                    }
//                }
//
//            }
//        } else
//        {
//            Log.d(TAG, "there is no file to upload");
//        }



    }

    private int countDirectoryFilesByExtention(File directory,String extention){
        int count=0;

        for (File f : directory.listFiles()) {
            String fileExtention = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("."));
            if (fileExtention.equals(extention)){
                count++;
            }
        }
        return count;
    }

    private void startZip() {
        String FILES_DIR = "JTrack";
        String ZIP_FILE = studyId+"_"+username+"_"+deviceid+"_"+ System.currentTimeMillis()+".zip";
        try {
            ContextWrapper cw = new ContextWrapper(mContext);
            String fullPath =cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
            File directory = cw.getExternalFilesDir(fullPath);

            File filesDir = new File(fullPath);
            File outFile = new File(fullPath, ZIP_FILE);
            ZipManager.zip(filesDir.getAbsolutePath(),  outFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e(ZipManager.TAG, "Exception while zipping files", e);
        } finally {

        }
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
    public void upload(File f) throws IOException {
//        File file = new File(mediaPath);


        // Parsing any Media type file
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("application/zip"), zipB64(f));

        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("fileToZip", f.getName(), requestBody1);

        Call<ResponseBody> call = serverinterface.uploadMulFile("upload_zip",f.getName(),"something","audio",studyId+"/"+username+"/"+deviceid+"/",zipB64(f));


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.code()==200)
                {
                    //           to delete a afile
                     f.delete();
                }

                Log.v(TAG, "the response code  for file upload is:"+response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String zipB64(File files) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

                try (FileInputStream fis = new FileInputStream(files)) {
                    zos.putNextEntry(new ZipEntry(files.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }

        }
        byte[] bytes = baos.toByteArray();

        return new String(Base64.encode(bytes,Base64.NO_WRAP), "UTF-8") ;

    }


}
