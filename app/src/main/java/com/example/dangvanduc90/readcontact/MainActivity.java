package com.example.dangvanduc90.readcontact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvContact;
    ArrayList<String> arrayContact;
    ArrayAdapter<String> adapter;
    private static final int MY_REQUEST = 9999;
    private static final String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    private static final List<String> listPermission = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvContact = (ListView) findViewById(R.id.lv_contact);
        arrayContact = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                MainActivity.this, android.R.layout.simple_list_item_1,
                arrayContact
        );
        lvContact.setAdapter(adapter);
//        checkAndRequestPermission();
        if (hasPermission()) {
            showAllContact();
        } else {
            requestPermission();
        }
    }

    private void showAllContact() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        arrayContact.clear();
        while (cursor.moveToNext()) {
            String idName = ContactsContract.Contacts.DISPLAY_NAME;
            String name = cursor.getString(cursor.getColumnIndex(idName));

            String idPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
            String phone = cursor.getString(cursor.getColumnIndex(idPhone));

            arrayContact.add(name + " - " + phone);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void checkAndRequestPermission() {
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermission.add(permission);
            }
        }
        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermission.toArray(new String[listPermission.size()]), MY_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_REQUEST:
//                if (grantResults.length > 0) {
//                    showAllContact();
//                    Toast.makeText(MainActivity.this, "Allow", Toast.LENGTH_LONG).show();
//                }
//                break;
//            default:
//                Toast.makeText(MainActivity.this, "Deny", Toast.LENGTH_LONG).show();
//                break;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;
        switch (requestCode) {
            case MY_REQUEST:
                for (int res: grantResults) {
                    allowed = true && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }
        if (allowed) {
            showAllContact();
        } else {
            Toast.makeText(MainActivity.this, "Deny", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasPermission() {
        int res;

        for (String permission: permissions) {
            res = checkCallingOrSelfPermission(permission);
            if (res == PackageManager.PERMISSION_GRANTED) {
                return  true;
            }
        }
        return false;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestPermissions(permissions, MY_REQUEST);
        }
    }
}
