package com.slinksoft.urlshortner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.slinksoft.urlshortner.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText enURL, shURL;
    private String lURL, sURL;
    private TextView vDisplay;
    boolean ifClicked = false;
    private int version, revision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enURL = findViewById(R.id.enterURL);
        shURL = findViewById(R.id.shortnedURL);
        vDisplay = findViewById(R.id.versionDisplay);
        lURL = "";
        sURL = "";
        version = 1;
        revision = 0;
        vDisplay.setText("Version " + version + "." + revision);

        setTitle("URL Shortner: By SlinkSoft");
    }


    public void shortURL(View v) throws Exception {
        lURL = enURL.getText().toString(); // update original url via user input
        accessTUAPI(lURL); // execute new thread accessing Tiny Url's api

        /* Due to the nature of UI thread execution in Android, we must manipulate an additional click
        per click to update the shURL (shorten url) text view properly (for some unknown and unlogical reason,
         the text view only updates if the new thread is executed twice. Having a runOnUiThread did not work).*/
        if (ifClicked == false) {
            ifClicked = true;
            v.performClick();
        }
        ifClicked = false;

    }

    // Accesses Tinyurl's API
    public void accessTUAPI(String url) throws Exception {
        final String durl = url; // for use in new thread execution; var must be final
        // We cannot use a BufferedReader in the UI execution thread, therefore, we must execute in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL tinyurl = new URL("http://tinyurl.com/api-create.php?url=" + durl); // create URL object
                    BufferedReader read = new BufferedReader(new InputStreamReader(tinyurl.openStream())); // created buffered reader
                    sURL = read.readLine(); // read the contents of the URL (for the TU api, the shorten url is the only thing returned
                    shURL.setText(sURL); // set short url text view to the short url

                    System.out.println("run new thread executed with " + sURL); // to verify that the url was shorten

                    read.close(); // close buffered reader
                } catch (Exception e) {
                }

            }
        }).start();

    }

    public void copyToClipboard(View v) {
        if (sURL != "") {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("shortURL", sURL);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Shorten URL Copied To Clipboard", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Shorten A URL First!", Toast.LENGTH_SHORT).show();
    }

    // Function for credits button with use of Alert Dialog
    public void credits(View v)
    {
        final AlertDialog credits = new AlertDialog.Builder(MainActivity.this).create();
        credits.setTitle("Credits");
        credits.setMessage("Developed By: \n- Slink (Dan) (UI/API usage)\n-Tinyurl (actual API and free" +
                " URL shortening service)\n\nVisit:\nhttps://www.realslinksoft.wixsite.com/slink-soft-portfolio" +
                "\nand\nhttp://www.YouTube.Com/ReTrOSlink\nThank you for using this app! Thank you" +
                " to Tinyurl for providing a free and simple-to-use URL shortening service!\n\n- Slink");
        credits.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        credits.setButton(AlertDialog.BUTTON_POSITIVE, "Visit SlinkSoft",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://realslinksoft.wixsite.com/slink-soft-portfolio"));
                        startActivity(browserIntent);
                        dialogInterface.dismiss();
                    }
                });

        credits.setButton(AlertDialog.BUTTON_NEGATIVE, "Visit Tinyurl",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tinyurl.com"));
                        startActivity(browserIntent);
                        dialogInterface.dismiss();
                    }
                });
        credits.show();
    }

    // A note for users wondering why this app came to be and its basic scenario uses
    public void note(View v)
    {
        AlertDialog note = new AlertDialog.Builder(MainActivity.this).create();
        note.setTitle("What For?");
        note.setMessage("If you need to share a long link to a friend, colleague, etc. but want it shorten, and " +
                "don't want to open up your Internet browser to find a URL shortening service; OR " +
                "you don't want to use a URL shortening service app packed with ads, this URL shortner" +
                " app comes in handy! Thanks to Tinyurl's free and easy to use URL shortening service and" +
                " API, this type of app came to be! There will never be an ad on this app and will always be free.\n\n" +
                "Speical thanks to Kevin Gilbertson and all the staff at Tinyurl!");

        note.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        note.show();
    }
}

