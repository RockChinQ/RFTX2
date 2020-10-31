package com.rftx.util;

public class Debugger {
    public static boolean debug=false;
    public static void say(String msg){
        if(debug){
            System.out.println(msg);
        }
    }
    
}
