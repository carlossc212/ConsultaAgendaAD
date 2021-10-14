package com.example.consultaagenda;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button BtSearch;
    private EditText ETPhone;
    private TextView tvResult;
    private final int PERMISSION_CONTACTS = 1;
    private final String TAG = "xyzyx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "He entrado en onCreate");//verbose
        initialize();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "He entrado en onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "He entrado en onPause");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "He entrado en onRequestPermissions");
        switch (requestCode){
            case PERMISSION_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length >0){
                    //permiso
                    search();
                }else{
                    //sin permiso
                }
            break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "He entrado en onrestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "He entrado en onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "He entrado en onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "He entrado en onStop");
    }

    //Mensaje de explicacion de los permisos
    private void explain() {
        showRationaleDialog(getString(R.string.title), getString(R.string.message), Manifest.permission.READ_CONTACTS, PERMISSION_CONTACTS );
    }
//Se inicializan los componentes
    private void initialize() {
        BtSearch = findViewById(R.id.BtSearch);
        ETPhone = findViewById(R.id.ETPhone);
        tvResult = findViewById(R.id.tvResult);


        BtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });
    }
//Pedir permisos
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_CONTACTS);
    }
//Buscar
    private void search() {



        //Buscar entre los contactos
        //ContentProvider Proveedor de contenidos
        //ContentResolver Consultor de contenidos
        // Queries the user dictionary and returns results
        //url: https://ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
        //uri: protocolo://dirección/ruta/recurso
        /*Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,                   // The content URI of the words table
                new String[] {"projection"},                        // The columns to return for each row
                "selectionClause",                                  // Selection criteria
                new String[] {"selectionArgs"},                     // Selection criteria
                "sortOrder");                                       // The sort order for the returned rows

         */


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null;
        argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        for (String s : columnas){
            Log.v(TAG,s);
        }
        String displayname;


        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String contactos = "";

        while (cursor.moveToNext()){

            displayname = cursor.getString(columnaNombre);
            System.out.println(displayname);

            if (cursor.getString(columnaNumero).contains(ETPhone.getText().toString())){
                contactos = contactos+displayname+"\n";
            }
        }
        tvResult.setText(contactos);



        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = sharedPreferences.getString("email", "no existe");
    }
// Buscar si esta permitido
    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //La version de android es posterior a la 6 (incluida)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();

            } else {
                requestPermission();
            }
        } else { //La version de android es anterior a la 6
            //Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog (String title, String message, String permission, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nada
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                }
            }
        });
        builder.create().show();

    }

}