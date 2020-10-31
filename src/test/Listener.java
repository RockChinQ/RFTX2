package test;

import java.beans.ExceptionListener;

import com.rftx.util.BasicInfo;
import com.rftx.util.Debugger;

public class Listener implements ExceptionListener{

    @Override
    public void exceptionThrown(Exception e) {
        Debugger.say("fuck exception!");
        e.printStackTrace();
    }
    
}
