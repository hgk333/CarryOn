package com.example.tonykwon.carryon;

/**
 * Created by TonyKwon on 2015-07-21.
 */
public class LastConnectDeviceAddress {
    private String name;
    private String address;

    public LastConnectDeviceAddress(){
        name = "BlunoV1.8";
        address = "F4:B8:5E:41:2A:13"; // blue tooth address

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