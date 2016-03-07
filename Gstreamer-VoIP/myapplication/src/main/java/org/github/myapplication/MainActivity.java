package org.github.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import org.github.audiostreamer.AudioCallManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private EditText remoteIpEditText;
    private EditText portEditText;
    private Button buttonCall;
    private Button buttonEndCall;
    private RadioButton rbSpeex;
    private RadioButton rbPCM;
    private AudioCallManager audioCallManager;
    private CheckBox cbSpeakesOn;
    private CheckBox cbMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        audioCallManager = new AudioCallManager(getApplicationContext());
        remoteIpEditText = (EditText) findViewById(R.id.et_remote_ip);
        portEditText = (EditText) findViewById(R.id.et_remote_port);
        buttonCall = (Button) findViewById(R.id.bt_call);
        buttonEndCall = (Button) findViewById(R.id.bt_end_call);
        rbSpeex = (RadioButton) findViewById(R.id.rb_speex_codec);
        rbPCM = (RadioButton) findViewById(R.id.rb_pcm);
        cbSpeakesOn = (CheckBox) findViewById(R.id.cb_speakers_on);
        cbMute = (CheckBox) findViewById(R.id.cb_mute);
        rbSpeex.setChecked(true);
        cbSpeakesOn.setChecked(false);

        rbSpeex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        rbPCM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

        cbMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioCallManager.mute(true);
                } else {
                    audioCallManager.mute(false);
                }
            }
        });

        cbSpeakesOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioCallManager.setSpeakersOn(true);
                } else {
                    audioCallManager.setSpeakersOn(false);
                }
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "onClick, participantAdded:");
                        int port = new Integer(portEditText.getText().toString());
                        String ip = remoteIpEditText.getText().toString();
                        audioCallManager.startVOIPStreaming(port, ip, port, 97);

                    }
                }).start();

            }
        });

        buttonEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioCallManager.stop();
            }
        });

    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface netWorkInterface : interfaces) {
                List<InetAddress> addressList = Collections.list(netWorkInterface.getInetAddresses());
                for (InetAddress address : addressList) {
                    if (!address.isLoopbackAddress()) {
                        String stringAddress = address.getHostAddress();
                        boolean isIPv4 = stringAddress.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return stringAddress;
                        } else {
                            if (!isIPv4) {
                                int delim = stringAddress.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? stringAddress.toUpperCase() : stringAddress.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
