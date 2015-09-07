package com.example.tonykwon.carryon;

/**
 * Created by TonyKwon on 2015-07-21.
 */
//자동연결을 위한 클래스
public class LastConnectDeviceAddress {
    private String name;
    private String address;


    public LastConnectDeviceAddress(){
        name = "BlunoV1.8";
        address = "F4:B8:5E:41:25:21"; // blue tooth address

    }
    public void setName(String name){
        this.name = name;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
}
