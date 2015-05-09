package com.blackstar.math4brain;


import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


@ReportsCrashes(formKey = "", 
	mode = ReportingInteractionMode.TOAST,                 
	forceCloseDialogAfterToast = false, // optional, default false                 
	resToastText = R.string.crash_toast_text)  
public class CrashDetect extends Application {
	@Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}

