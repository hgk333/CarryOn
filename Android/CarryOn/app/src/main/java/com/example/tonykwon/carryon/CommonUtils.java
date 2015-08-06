package com.example.tonykwon.carryon;

public class CommonUtils {



    public static boolean isEmpty(String value){

        boolean isEmpty = false;

        if((value == null) || (value == ""))
            isEmpty = true;

        return isEmpty;
    }

}
