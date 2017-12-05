package com.moadd.serverandroidsocketexample;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Server extends AppCompatActivity {
    Button openHotspot;
    TextView info, infoip, msg;
    EditText hotname,hotpassword,passwordneed;
    String message = "";
    ServerSocket serverSocket;
    wifiHotSpots hotutil;
    SharedPreferences sp;
    String msgReply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Permissions for switching on/off wifi access :
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
            }
            else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        sp=getSharedPreferences("Credentials",MODE_PRIVATE);
        hotutil=new wifiHotSpots(getApplicationContext());
        info =findViewById(R.id.info);
        infoip =findViewById(R.id.infoip);
        msg =  findViewById(R.id.msg);
        hotname=findViewById( R.id.hotname);
        hotpassword=findViewById(R.id.hotpw);
        passwordneed=findViewById(R.id.passwordneed);
        openHotspot=findViewById(R.id.hotspot);
       // inviteFriend(hotutil);
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
     /*   sendTOCLient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataOutputStream1 = new DataOutputStream(
                            socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataOutputStream1.writeUTF("Love you");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
     openHotspot.setOnClickListener(new View.OnClickListener() {


         @Override
         public void onClick(View view) {
             if (!hotname.getText().toString().trim().equals("") && !hotpassword.getText().toString().trim().equals("")&& (passwordneed.getText().toString().trim().equalsIgnoreCase("yes")||passwordneed.getText().toString().trim().equalsIgnoreCase("no")))
             {
                 hotutil.startHotSpot(true);
             }
             else
             {
                 Toast.makeText(Server.this,"Set proper values in the fields",Toast.LENGTH_LONG).show();
             }
             //Toast.makeText(Server.this,hotutil.getProfiles().toString(),Toast.LENGTH_LONG).show();
            // hotutil.setAndStartHotSpot(true, sp.getString("SSID","333"));
         }
     });
    }
    public void inviteFriend(wifiHotSpots hotutil)
    {
        //hotutil.setHotSpot(sp.getString("SSID","333"),"userIdhawe");
        //hotutil.setAndStartHotSpot(true, sp.getString("SSID","333"));
        hotutil.startHotSpot(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 5000;
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                    }
                });

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    String messageFromClient = "";

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();
                    if (messageFromClient.equals(sp.getString("SSID","333")))
                    {
                        //hotutil.shredAllWifi();
                        //static as of now
                        msgReply = "Hotspot Name : "+hotname.getText().toString()+"\n" + "Hotspot Password : "+hotpassword.getText().toString()+"\n"+"Password Requirement : "+passwordneed.getText().toString()+"\n";
                    }
                    else if (messageFromClient.equals("success"))
                    {
                        msgReply="Disconnect";
                    }
                    else
                    {
                        msgReply="wrong";
                    }
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"
                            + "Message from client: " + messageFromClient + "\n";

                    Server.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });

                    /*String msgReply = "Hotspot Name : "+" Umar "+"\n" + "Hotspot Password : "+" 1234ab "+"\n"+"Password Requirement : "+" Yes "+"\n";*/
                    dataOutputStream.writeUTF(msgReply);

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                final String errMsg = e.toString();
                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}