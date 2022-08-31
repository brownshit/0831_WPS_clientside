package com.practice.wpsactivity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ScanResult> items;

    //add
    private Context mcontext;

    //버튼이 동적으로 연결되어야 하는데 지금 자바에만 선언이 된게 문제이다.
    private Button btn_connect;
    private Button btn_disconnect;
    //버튼을 누르면 와이파이에 연결하도록 하는

    //아래의 코드 주석을 풀면 앱이 중단된다.

    /**
     static final int PERMISSIONS_REQUEST = 0x0000001;
     private ConnectivityManager connectivityManager;
     private ConnectivityManager.NetworkCallback networkCallback;


     private WifiManager wifiManager = (WifiManager) mcontext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


          // 현제 SSID 를 받는 코드
          public String getWiFiSSID() {
              WifiInfo connectionInfo = wifiManager.getConnectionInfo();
              String sSSID = connectionInfo.getSSID();
              String s = sSSID.substring(1, sSSID.length() - 1);
              return s;
          }
     * **/


    public MyAdapter(List<ScanResult> items){
        this.items=items;
    }
    //생성자가 아이템들을 나열을 한다.

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        //mcontext = mcontext.getApplicationContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item , parent, false);
        return new MyViewHolder(itemView);

        //여기엔 버튼이 없어도 되나,,!??
    }


    //버튼을 만들어서 바인딩 해주었다~
    //이제 어떤 동작을 실행할 것인지 일단은 토스트로 만들어볼까??!
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        ScanResult item=items.get(position);
        holder.setItem(items.get(position));

        /**추가한 부분**/
        holder.btncon.findViewById(R.id.btn_connect);
        holder.btndiscon.findViewById(R.id.btn_disconnect);
    }



    //아이템리스트 개수 가져옴
    @Override
    public int getItemCount() {
        return items.size();
    }


    //여기서 동작이 가능한가?
    //사실상 메인 클래스
    //inner class
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWifiName;

        public Button btncon;
        public Button btndiscon;

        //NetworkCallback
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                //Log.d(tag, "onAvailable");
            }

            @Override
            public void onUnavailable() {
                //Log.d(tag, "onUnavailable");
            }
        };



        public MyViewHolder(View itemView) {

            super(itemView);
            tvWifiName=itemView.findViewById(R.id.tv_wifiName);

            /**추가구문**/
            btncon = itemView.findViewById(R.id.btn_connect);
            btndiscon = itemView.findViewById(R.id.btn_disconnect);


            //itemview는 전체 아이템들을 의미 (리사이클러뷰의 아이템들)
            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {
                    int currentpos = getAdapterPosition();
                    //list배열의 인덱스넘버
                    ScanResult scanResult = items.get(currentpos);
                    /**이부분에 동적인 부분을 넣어주기??!**/
                    Toast.makeText(mcontext,scanResult.SSID,Toast.LENGTH_SHORT).show();

                    WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                            .setSsid(scanResult.SSID) //SSID 이름
                            .setWpa2Passphrase("") //비밀번호, 보안설정 WPA2
                            .build();
                    NetworkRequest networkRequest = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //연결 Type
                            .setNetworkSpecifier(wifiNetworkSpecifier)
                            .build();

                    ConnectivityManager connectivityManager = (ConnectivityManager) mcontext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    connectivityManager.requestNetwork(networkRequest, networkCallback);

                    //unregisterNetworkCallback
                    connectivityManager.unregisterNetworkCallback(networkCallback);

                    wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                            .setIsEnhancedOpen(true)
                            .build();
                }
            });


            /**아래도 추가해준 구문이다.**/
            btncon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentpos = getAdapterPosition();
                    ScanResult scanResult = items.get(currentpos);

                    Toast.makeText(mcontext,scanResult.SSID,Toast.LENGTH_SHORT).show();
                    Toast.makeText(mcontext.getApplicationContext(), "connect complete\n"+currentpos,Toast.LENGTH_SHORT).show();

                    //connectWifi();
                }

            });

            btndiscon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Disconnect();
                }
            });


            //이제 와이파이 연결하기 위해서 오픈소스로 가져온것 변형을 잘 시켜보자구~
        }
        public void setItem(ScanResult item){

            tvWifiName.setText(item.SSID);

        }



/**
 *
 void connectWifi() {
 try {
 if (!wifiManager.isWifiEnabled()) {
 wifiManager.setWifiEnabled(true);
 }

 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
 WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
 builder.setSsid("WIFI 이름"); // 연결하고자 하는 SSID
 builder.setWpa2Passphrase("비밀번호"); // 비밀번호

 WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

 final NetworkRequest.Builder networkRequestBuilder1 = new NetworkRequest.Builder();
 networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
 networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier);

 NetworkRequest networkRequest = networkRequestBuilder1.build();
 networkCallback = new ConnectivityManager.NetworkCallback() {
@Override
public void onAvailable(@NonNull Network network) {
super.onAvailable(network);
connectivityManager.bindProcessToNetwork(network);
Toast.makeText(mcontext.getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
}
};

 connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
 connectivityManager.requestNetwork(networkRequest, networkCallback);

 } else {
 WifiConfiguration wifiConfiguration = new WifiConfiguration();
 wifiConfiguration.SSID = String.format("\"%s\"", "wifi 이름"); // 연결하고자 하는 SSID
 wifiConfiguration.preSharedKey = String.format("\"%s\"", "비밂번호"); // 비밀번호
 int wifiId = wifiManager.addNetwork(wifiConfiguration);
 wifiManager.enableNetwork(wifiId, true);
 Toast.makeText(mcontext.getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
 }

 } catch (Exception e) {
 e.printStackTrace();
 Toast.makeText(mcontext.getApplicationContext(), "연결 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
 }

 }
 void Disconnect() {
 try {
 if (wifiManager.isWifiEnabled()) {

 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
 connectivityManager.unregisterNetworkCallback(networkCallback);
 Toast.makeText(mcontext.getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();

 } else {
 if (wifiManager.getConnectionInfo().getNetworkId() == -1) {
 Toast.makeText(mcontext.getApplicationContext(), "연결", Toast.LENGTH_SHORT).show();

 } else {
 int networkId = wifiManager.getConnectionInfo().getNetworkId();
 wifiManager.removeNetwork(networkId);
 wifiManager.saveConfiguration();
 wifiManager.disconnect();
 Toast.makeText(mcontext.getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();
 }
 }

 } else
 Toast.makeText(mcontext.getApplicationContext(), "Wifi 꺼짐", Toast.LENGTH_SHORT).show();

 } catch (Exception e) {
 e.printStackTrace();
 Toast.makeText(mcontext.getApplicationContext(), "연결 해제 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
 }
 }
 * **/
    }
}

    /**
     * add part _for connect wifi for conntecting button
     * 08_18
     *
     * 일단 permission에 관한것은 무시해줬다
     * setting을 통한 permission allow 해주도록하자.
     *
     * 추가적으로
     *
     * failed
     * 1. 연결이 잘못됐거나
     * 2. adapter에연결하는게 아니거나
     * 위의 2가지 경우중 하나이다.
     *
     * **/

