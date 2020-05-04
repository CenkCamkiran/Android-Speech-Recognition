package camera.android.com.speechrecognition;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 1234;
    Button Start, btn_hakkinda, btn_tutorial;
    TextView Speech;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;
    final String permissionToCall = Manifest.permission.CALL_PHONE;
    Animation animFadein;

    public void enableBT() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    public void disableBT() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public String telefon_numarasi(String person) {
        String name = null, phoneNumber = null;

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Log.v("Tel", name +" " + phoneNumber) ;

            if (person.equalsIgnoreCase(name))
                break;

        }
        phones.close();

        return phoneNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        Speech = (TextView) findViewById(R.id.welcome);

        Start = (Button) findViewById(R.id.start_reg);
        btn_hakkinda = (Button) findViewById(R.id.btn_hakkinda);
        btn_hakkinda.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Kocaeli Üniversitesi Bilgisayar Mühendisliği 3.Sınıf Cenk Camkıran. İlk Android Uygulamam.");
                dialog.setTitle("Android Asistanı");
                dialog.setIcon(R.mipmap.asistant_icon);

                dialog.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();
            }
        });

        btn_tutorial = (Button) findViewById(R.id.btn_klavuz);
        btn_tutorial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Tutorial.class);
                startActivity(intent);
            }
        });

        Start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen İnternet Bağlantınızı Kontrol Ediniz!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_hakkinda.startAnimation(animFadein);
        btn_tutorial.startAnimation(animFadein);
        Speech.startAnimation(animFadein);
        Start.startAnimation(animFadein);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            match_text_dialog = new Dialog(MainActivity.this);
            match_text_dialog.setContentView(R.layout.dialog_matches_frag);
            match_text_dialog.setTitle("Uyuşan cümleyi seçiniz");
            textlist = (ListView) match_text_dialog.findViewById(R.id.list);
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches_text);
            textlist.setAdapter(adapter);
            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    match_text_dialog.hide();

                    func(matches_text.get(position));
                }
            });
            match_text_dialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void func(String soylenen) {

        CharSequence cs1 = "ara";
        boolean contain = soylenen.contains(cs1);

        if (soylenen.equalsIgnoreCase("Hava Durumu"))//Ok
        {
            System.out.println("Cenk");
            Log.i("hey", "Cenk");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.google.com.tr/search?q=hava+durumu"));
            startActivity(intent);
        } else if (soylenen.equalsIgnoreCase("Galeriyi Aç")) //Ok
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (soylenen.equalsIgnoreCase("Kamerayı Aç")) //Ok
        {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        } else if (soylenen.equalsIgnoreCase("Müzik Çaları Aç")) //Ok
        {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
            startActivity(intent);
        } else if (soylenen.equalsIgnoreCase("Whatsapp'ı aç")) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else if (soylenen.equalsIgnoreCase("Bluetooth'u Aç")) {
            enableBT();
        } else if (soylenen.equalsIgnoreCase("Bluetooth'u Kapat")) {
            disableBT();
        } else if (soylenen.equalsIgnoreCase("Saat ve Tarih")) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Saat ve Tarih Bilgisi");
            dlg.setMessage(date);

            dlg.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dlg.show();
        }

        else if (contain)
        {
            int index = soylenen.indexOf("ara");

            StringBuffer sb = new StringBuffer(soylenen);
            sb.delete(index-1, soylenen.length());
            String person = sb.toString();
            Log.v("Hey", person);

            String phone_number = telefon_numarasi(person);

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone_number));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissionToCall}, 1);
                return;
            }
            startActivity(callIntent);

        }

        else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + soylenen));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permissionToCall}, 1);
                return;
            }
            startActivity(callIntent);
        }

    }

}