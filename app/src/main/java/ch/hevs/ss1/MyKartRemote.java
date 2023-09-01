package ch.hevs.ss1;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import ch.hevs.kart.AbstractKartControlActivity;
import ch.hevs.kart.Kart;
import ch.hevs.kart.KartListener;

public class MyKartRemote extends AbstractKartControlActivity {
    /*
    private Timer timer_batterie = new Timer() {
        @Override
        public void onTimeout() {
            getBatterie();
            kart.toggleLed(0);
        }
    };
    */
    TextView tv_battery;
    SeekBar seekBar_vitesse;
    SeekBar seekBar_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kart_remote);

        //tv_battery = findViewById(R.id.tv_battery);

        kart.addKartListener(new KartListener() {
            @Override
            public void batteryLevelChanged(Kart kart, double level) {
                updateBatteryLevelUI(level);
            }
        });

        // Référencez la SeekBar par son ID
        seekBar_vitesse = findViewById(R.id.seekBar_vitesse);

        // Ajoutez un écouteur de changement de valeur à la SeekBar
        seekBar_vitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // lorsque la valeur de la SeekBar change

                kart.setDriveSpeed(seekBar_vitesse.getProgress()-15);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_position = findViewById(R.id.seekBar_position);
        // Ajoutez un écouteur de changement de valeur à la SeekBar
        seekBar_vitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // lorsque la valeur de la SeekBar change
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void updateBatteryLevelUI(double level) {
        tv_battery.setText((int) (level*100));
    }


    public void Acc_onClick(View view) {
        int speed_param;
        speed_param = kart.getDriveSpeed() + 1;
        kart.setDriveSpeed(speed_param);
    }

    public void freiner_onClick(View view){
        int speed_param = 0;
        if (kart.getDriveSpeed() > 0){
            speed_param = kart.getDriveSpeed() - 1;
        }
        if (kart.getDriveSpeed() < 0){
            speed_param = kart.getDriveSpeed() + 1;
        }

        kart.setDriveSpeed(speed_param);

    }
    public void reculer_onClick(View view) {
        int speed_param;
        speed_param = kart.getDriveSpeed() - 1;
        if (speed_param >= 0) {
            speed_param = -1;
        }
        kart.setDriveSpeed(speed_param);
    }


    public void getBatterie(View view){
        ProgressBar pb_batterie = findViewById(R.id.pb_batterie);
        float battery_pourcent;
        //timer_batterie.schedulePeriodically(500);
        battery_pourcent = (float) (kart.getBatteryLevel())*100;
        // Utilisation de Log pour afficher un message dans le Logcat
        Log.d("TAG_1", "Ceci est un message de débogage");
        Log.d("TAG_2", String.valueOf(kart.getBatteryLevel()));
        Log.d("TAG_3", String.valueOf(battery_pourcent));
        Log.d("TAG_31", String.valueOf(kart.getBatteryVoltage()));
        pb_batterie.setProgress(10);
    }

    public void goLeft_onClick(View view){
        int value_turn;
        Log.d("TAG_4", String.valueOf(kart.getSteeringPosition()));
        value_turn = kart.getSteeringPosition()+40; //40 pas = 3°
        Log.d("TAG_5", String.valueOf(value_turn));
        kart.turnSteeringTargetPositionBy(value_turn);
    }

    public void goRight_onClick(View view){
        int value_turn;
        Log.d("TAG_6", String.valueOf(kart.getSteeringPosition()));
        value_turn = kart.getSteeringPosition()-40;
        Log.d("TAG_7", String.valueOf(value_turn));
        kart.turnSteeringTargetPositionBy(value_turn);
    }


}