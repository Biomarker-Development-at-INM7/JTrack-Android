package inm7.JTrack.Jtrack_Social.ActiveLabeling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import android.util.Log;

public class ZipManager {
    public static final String TAG = "ZipManager";


    private static int BUFFER_SIZE = 6 * 1024;

    public static void zip(String inputDirPath, String outputDirPath) throws IOException {
        File[] files = new File(inputDirPath).listFiles();
       List<String>  filePaths = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            // zip it only for 3gp files
            String fileExtention = files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("."));

           if (fileExtention.equals(".3gp") & (filePaths.size()<5)){
                filePaths.add(files[i].getAbsolutePath());


            }
        }

        // delete when zipped
        if (filePaths.size()>0){
            if (zip(filePaths, outputDirPath)){
                for (int i = 0; i < filePaths.size(); i++) {
                    File filesDir = new File(filePaths.get(i));
                    filesDir.delete();

                }
            }
        }

    }

    public static boolean zip(List<String> filePaths, String outputFilePath) throws IOException {
        List<String>  files = filePaths;
        String zipFile = outputFilePath;

        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.size(); i++) {
                String filePath = files.get(i);
                File file = new File(filePath);
                if (file.isFile()) {
                    FileInputStream fi = new FileInputStream(filePath);
                    origin = new BufferedInputStream(fi, BUFFER_SIZE);
                    try {
                        ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                            out.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        Log.e(ZipManager.TAG, "Exception while zipping file: " + filePath, e);
                    } finally {

                        origin.close();

                        //
                    }
                } else {
                    Log.e(ZipManager.TAG, "Path is not a file");
                }

            }
        } catch (Exception e) {
            Log.e(ZipManager.TAG, "Exception while zipping", e);
            return false;
        } finally {

            out.close();
            return true;

        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        File f = new File(location);
        if (!f.isDirectory()) {
            f.mkdirs();
        }

        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        try {
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String path = location + File.separator + ze.getName();

                if (ze.isDirectory()) {
                    File unzipFile = new File(path);
                    if (!unzipFile.isDirectory()) {
                        unzipFile.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(path, false);
                    try {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        zin.closeEntry();
                    } finally {
                        fout.close();
                    }
                }
            }
        } finally {
            zin.close();
        }
    }



}