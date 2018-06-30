package voluta.familyst.Activities;

import android.content.Intent;
import android.os.Bundle;

import voluta.familyst.FamilystApplication;
import voluta.familyst.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getSupportActionBar().hide();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (((FamilystApplication)getApplication()).getLoginAutomatico()) {
                    Intent intent = new Intent(getApplicationContext(), LoadingDataActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }, 600);
    }
}
