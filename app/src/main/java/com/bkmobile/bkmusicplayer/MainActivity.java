package com.bkmobile.bkmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    public ListView listView;
    String[] items;

    private int storeagePermissionCode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView=findViewById(R.id.listViewMySong);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this, "You Have Already Granted This Permission!", Toast.LENGTH_SHORT).show();

            displaySongs();
        }
        else{
            listView=findViewById(R.id.listViewMySong);
            requestStoragePermission();
        }


    }

    public class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView= getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong=myView.findViewById(R.id.txtsongname);
            //listView=(ListView) myView.findViewById(R.id.listViewSong);
            textSong.setSelected(true);
            textSong.setText(items[i]);

            return myView;
        }
    }

private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission Needed").setMessage("This Permission For Read Your Music List")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},storeagePermissionCode);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Permission Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},storeagePermissionCode);
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == storeagePermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                displaySongs();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<File> findSong(File file){
        ArrayList<File>arrayList=new ArrayList<>();
        String deneme;
        deneme=file.toString();
        //System.out.println(deneme);
        //System.out.println(deneme+" try");
            try {
                //if(deneme!="/storage/emulated/0/Android/data"||deneme!="/storage/emulated/0/Android/obb"){
                File[] files = file.listFiles();
                //System.out.println(file+"Try");
                for (File singleFile : files) {
                    if (singleFile.isDirectory() && !singleFile.isHidden()) {
                        arrayList.addAll(findSong(singleFile));
                    } else {
                        if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                            arrayList.add(singleFile);
                        }

                    }
                    //}
                }
            } catch (NullPointerException e) {
                //deneme = file.toString();
                //System.out.println(deneme + "Fuck catch");
                //Toast.makeText(MainActivity.this, "Error : " + e + " " + e.getStackTrace()[0].getLineNumber(), Toast.LENGTH_SHORT).show();
            }

        return arrayList;
    }

    public void  displaySongs(){
        final ArrayList<File> mySongs=findSong(Environment.getExternalStorageDirectory());

        items=new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
       /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(myAdapter);*/
       //try {

            CustomAdapter customAdapter=new CustomAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName=(String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class).putExtra("songs",mySongs)
                        .putExtra("songName",songName).putExtra("pos",i));
            }
        });
        //}catch (NullPointerException e){
          //  System.out.println("Error");
            //Toast.makeText(MainActivity.this, "Error : " + e + " " + e.getStackTrace()[0].getLineNumber(), Toast.LENGTH_SHORT).show();
        //}


    }


}