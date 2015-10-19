package com.example.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by bensonkuo on 2015/10/19.
 */

//  獨立出一個類別來做，因為有的時候不一定會經過main但是需要parse service
public class SimpleUIApplication extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "R1iEoTLPPHzpxF4OuLKZQcy5OVEonqBqMUad4zaS", "770fv893TsEb8rBYft7vWZ9aNXETrkBrVH82iRyD");

    }



}
