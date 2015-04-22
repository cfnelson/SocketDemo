package com.alertdemo.inluxtris.hospitaldemo;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {

    //private String socketUrl = "http://orzsolutionsrtls-184932.apse2.nitrousbox.com";
    //private String socketUrl = "http://blue-vroom-65-172615.usw1-2.nitrousbox.com";
    private String socketUrl = "http://192.168.0.11:3000";
    private Socket socket;
    private static final String TAG = "HospitalDemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket = connectSocket(socket,socketUrl);
        Log.i(TAG, "Socket has been created.");

        final Button button = (Button) findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new NetworkTask().execute();        //- send the post request on button click
            }
        });

        final Button button2 = (Button) findViewById(R.id.btn_reset);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageView img= (ImageView) findViewById(R.id.img_P1);
                img.setBackgroundResource(R.color.my_green);
                ImageView img2= (ImageView) findViewById(R.id.img_P2);
                img2.setBackgroundResource(R.color.my_green);
                ImageView img3= (ImageView) findViewById(R.id.img_P3);
                img3.setBackgroundResource(R.color.my_green);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetworkTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("device", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!socket.connected()) socket.connect(); //- If not connected, connect and send
            socket.emit("android", obj);

            return obj;
        }
        @Override
        protected void onPostExecute(JSONObject result) {
            System.out.print("Sent Object \n" + result);
        }
    } //- END OF private class NetworkTask


    private Socket connectSocket( Socket socket,String socketUrl){
        IO.Options opts = new IO.Options();
       // Socket socket;
        try {
            socket = IO.socket(socketUrl,opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Server connected");
                //socket.emit("hello", "test");
            }
        }).on("p1_green", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P1);
                        img.setBackgroundResource(R.color.my_green);
                    }
                });
                System.out.println("@p1_green");
            }
        }).on("p1_yellow", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P1);
                        img.setBackgroundResource(R.color.my_yellow);
                    }
                });
                System.out.println("@p1_yellow");
            }
        }).on("p1_red", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                System.out.println("we are here woop");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img = (ImageView) findViewById(R.id.img_P1);
                        img.setBackgroundResource(R.color.my_red);
                        // Start without a delay
                        // Each element alternates between vibrate, sleep, vibrate, sleep...
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Vibrator v = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
                        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                        long[] pattern = {0, 1000, 100, 1000, 100, 1000, 100};
                        v.vibrate(pattern, -1);
                        // Vibrate for 1500 milliseconds
                        //v.vibrate(1500);
                        Toast.makeText(getBaseContext(),"Warning: Patient 1 needs immediate attention!", Toast.LENGTH_LONG).show();
                    }
                });
                System.out.println("@p1_red");
            }
        }).on("p2_green", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P2);
                        img.setBackgroundResource(R.color.my_green);
                    }
                });
                System.out.println("@p2_green");
            }
        }).on("p2_yellow", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P2);
                        img.setBackgroundResource(R.color.my_yellow);
                    }
                });
                System.out.println("@p2_yellow");
            }
        }).on("p2_red", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        ImageView img = (ImageView) findViewById(R.id.img_P2);
                        img.setBackgroundResource(R.color.my_red);
                        // Start without a delay, Each element then alternates between vibrate, sleep, vibrate, sleep...
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Vibrator v = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
                        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                        long[] pattern = {0, 1000, 100, 1000, 100, 1000, 100};
                        v.vibrate(pattern, -1);
                        // Vibrate for 1500 milliseconds
                        //v.vibrate(1500);
                        Toast.makeText(getBaseContext(),"Warning: Patient 2 needs immediate attention!", Toast.LENGTH_LONG).show();
                    }
                });
                System.out.println("@p2_red");
            }
        }).on("p3_green", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                System.out.println("we are here woop");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img = (ImageView) findViewById(R.id.img_P3);
                        img.setBackgroundResource(R.color.my_green);
                    }
                });
                System.out.println("@p3_green");
            }
        }).on("p3_yellow", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                System.out.println("we are here woop");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P3);
                        img.setBackgroundResource(R.color.my_yellow);
                    }
                });
                System.out.println("@p3_yellow");
            }
        }).on("p3_red", new Emitter.Listener() //- Receiving
        {
            @Override
            public void call(Object... args) {
                // runOnUiThread is needed if you want to change something in the UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // do something
                        ImageView img= (ImageView) findViewById(R.id.img_P3);
                        img.setBackgroundResource(R.color.my_red);
                        // Start without a delay, Each element alternates between vibrate, sleep, vibrate, sleep...
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Vibrator v = (Vibrator) getBaseContext().getSystemService(getBaseContext().VIBRATOR_SERVICE);
                        // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                        long[] pattern = {0, 1000, 100, 1000, 100, 1000, 100};
                        v.vibrate(pattern, -1);
                        // Vibrate for 1500 milliseconds
                        //v.vibrate(1500);
                        Toast.makeText(getBaseContext(),"Warning: Patient 3 needs immediate attention!", Toast.LENGTH_LONG).show();
                    }
                });
                System.out.println("@p3_red");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("@disconnect");
            }
        }).on(Socket.EVENT_ERROR,new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("@error");
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("@connection Timeout");
            }
        }).on(Socket.EVENT_CONNECT_ERROR,new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("@connection error");
                System.out.print(args[0]);
            }
        });
        if (!socket.connected()){
            System.out.println("try to connect.. to " + socketUrl);
            socket.connect();
        }
        return socket;
    }
}
