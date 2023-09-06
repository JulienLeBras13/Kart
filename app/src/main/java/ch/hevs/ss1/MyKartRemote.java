package ch.hevs.ss1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Timer;

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
    TextView tv_position;
    TextView tv_vitesse;
    TextView tv_distance;
    SeekBar seekBar_vitesse;
    SeekBar sb_position;
    ProgressBar pb_batterie;
    Switch switch_phares;
    Runnable runnable;

    int counter;
    Handler handler;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kart_remote);
        tv_battery = findViewById(R.id.tv_battery);
        tv_position = findViewById(R.id.tv_position);
        tv_vitesse = findViewById(R.id.tv_vitesse);
        pb_batterie = findViewById(R.id.pb_batterie);
        tv_distance = findViewById(R.id.tv_distance);
        switch_phares = findViewById(R.id.switch_phares);
        handler = new Handler();

        kart.setup().setSteeringCenterPosition(237);


        // Créez un Runnable pour gérer la mise à jour du compteur
        runnable = new Runnable() {
            @Override
            public void run() {
                // Incrémente le compteur
                counter++;

                // Répète l'exécution toutes les 100 ms
                kart.setLedState(2, false);
            }
        };


        switch_phares.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Les phares sont allumés
                    kart.setLedState(1, true);
                    kart.setLedState(4, true);
                    kart.setLedState(5, true);

                } else {
                    // Les phares sont éteints
                    kart.setLedState(1, false);
                    kart.setLedState(4, false);
                    kart.setLedState(5, false);
                }
            }
        });

        kart.addKartListener(new KartListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void batteryLevelChanged(@NonNull Kart kart, double level) {
                KartListener.super.batteryLevelChanged(kart, level);
                //updateBatteryLevelUI(level);
                tv_battery.setText(String.format("%.1f%%", level * 100));
                pb_batterie.setProgress((int) (level * 100));

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void ultrasonicRangerDistanceChanged(@NonNull Kart kart, double distance){
                KartListener.super.ultrasonicRangerDistanceChanged(kart, distance);
                tv_distance.setText(String.format("%.1f",distance*100));
                if(distance*100 >= 50){
                    kart.setDriveSpeed(0);
                    seekBar_vitesse.setProgress(15);
                }

            }
        });
        kart.addKartListener(new KartListener() {
            private float memory_count = 0.0F; // Variable pour enregistrer le comptage précédent
            private long previousTimestamp = System.currentTimeMillis(); // Enregistrer le moment où l'événement précédent s'est produit
            @SuppressLint("DefaultLocale")
            @Override
            public void hallSensorCountChanged(@NonNull Kart kart, int index, int value) {
                KartListener.super.hallSensorCountChanged(kart, index, value);
                Log.d("TAG_10", String.valueOf(value));
                // Obtenez le temps actuel
                long currentTimestamp = System.currentTimeMillis();

                // Calculer la différence de temps en millisecondes entre les événements de comptage
                long timeDifference = currentTimestamp - previousTimestamp;

                memory_count ++;

                if (timeDifference >= 200 && sb_position.getProgress() != 0) {
                    // Mettre à jour la variable de temps précédent pour la prochaine itération
                    previousTimestamp = currentTimestamp;
                    // Calculez la vitesse en utilisant la différence de temps
                    Log.d("TAG_11" , String.valueOf(timeDifference));
                    double vitesse_kart = (3.14159 *0.0007) / (timeDifference/(memory_count*2) / 3600000.0);
                    Log.d("TAG_12" , String.valueOf(timeDifference));

                    // Mettez à jour l'affichage de la vitesse
                    tv_vitesse.setText(String.format("%.1f", vitesse_kart));
                    memory_count = 0.0F;
                }
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
                if(seekBar_vitesse.getProgress() < 15){
                    kart.setLedState(3, true); // phares reculer
                }else{
                    kart.setLedState(3, false); // phares reculer
                }
                if (seekBar_vitesse.getProgress() != 15){
                    kart.setLedState(2, false); // phares rouge frein
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_position = findViewById(R.id.sb_position);
        // Ajoutez un écouteur de changement de valeur à la SeekBar
        //La postion va de -30 à 30° dans ce cas sb_postion va de 0 à 200 (0.3° par changement)
        sb_position.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // lorsque la valeur de la SeekBar change
                tv_position.setText("Position : "+ String.format("%.1f", (sb_position.getProgress()-sb_position.getMax()/2)*0.5925) + "°");
                kart.setSteeringTargetPositionNormalized((double)(sb_position.getProgress()-sb_position.getMax()/2)/-100);

                if (sb_position.getProgress() == 0){
                    // Mettez à jour l'affichage de la vitesse
                    tv_vitesse.setText("0");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //position_start_change = sb_position.getProgress()-sb_position.getMax()/2;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    public void Acc_onClick(View view) {
        int speed_param;
        speed_param = kart.getDriveSpeed() + 1;
        kart.setDriveSpeed(speed_param);
        seekBar_vitesse.setProgress(speed_param+15);
    }

    public void ralentir_onClick(View view){
        int speed_param = 0;
        if (kart.getDriveSpeed() > 0){
            speed_param = kart.getDriveSpeed() - 1;
        }
        if (kart.getDriveSpeed() < 0){
            speed_param = kart.getDriveSpeed() + 1;
        }

        kart.setDriveSpeed(speed_param);
        seekBar_vitesse.setProgress(speed_param+15);
    }

    @SuppressLint("SetTextI18n")
    public void freiner_onClick(View view){
        int speed_param = 0;
        kart.setLedState(2, true); // phares rouge frein
        handler.postDelayed(runnable, 3000); // Démarre le compteur

        kart.setDriveSpeed(speed_param);
        seekBar_vitesse.setProgress(speed_param+15);
        tv_vitesse.setText("0");
    }

    public void reculer_onClick(View view) {
        int speed_param;
        speed_param = kart.getDriveSpeed() - 1;
        if (speed_param >= 0) {
            speed_param = -1;
        }
        kart.setDriveSpeed(speed_param);
        seekBar_vitesse.setProgress(speed_param+15);
    }
    public void  goLeft_onClick(View view){
        if(sb_position.getProgress() >= 20){
            sb_position.setProgress(sb_position.getProgress()-20);
        }else{
            sb_position.setProgress(0);
        }
    }

    public void goRight_onClick(View view){
        if(sb_position.getProgress() <= (sb_position.getMax()-20)){
            Log.d("TAG_6", String.valueOf(kart.getSteeringPosition()));
            sb_position.setProgress(sb_position.getProgress()+20);
        }else{
            sb_position.setProgress(sb_position.getMax());

        }
    }
    public void goPos0(View view){
        sb_position.setProgress(sb_position.getMax()/2);
        //tv_hall_cunt.setText(kart.getHallSensorCount(1));
    }
/*
    int memory_cunt;
    double vitesse_kart;
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void vitesse_change(View view) {
        this.view = view;
        int temps = 0;
        Log.d("TAG_10", String.valueOf(kart.getHallSensorCount(1)));
        Log.d("TAG_11", String.valueOf(kart.getHallSensorCount(0)));

        if(memory_cunt < kart.getHallSensorCount(1) ){
            temps = kart.getHallSensorCount(1) - memory_cunt;
        }
        if (temps != 0) {
            vitesse_kart = (3.14159 / 2500) / (temps / 36000.0); // Utilisez 36000.0 pour une division en virgule flottante
            // Mettez à jour l'affichage de la vitesse
            tv_vitesse.setText(vitesse_kart + " km/h");
        }
        memory_cunt = kart.getHallSensorCount(1);
        tv_vitesse.setText(String.format("%.1f",vitesse_kart));
    }
*/
    public void onClick_setCenter(View view) {
        //setup

        Log.d("TAG_95", String.valueOf(kart.getSteeringTargetPosition()));
        Log.d("TAG_96", String.valueOf(kart.getSteeringPositionNormalized()));
        Log.d("TAG_97", String.valueOf(kart.getSteeringPosition()));
        Log.d("TAG_98", String.valueOf(kart.setup().getSteeringCenterPosition()));
        kart.setup().setSteeringCenterPosition(kart.getSteeringTargetPosition());
        Log.d("TAG_99", String.valueOf(kart.setup().getSteeringCenterPosition()));
    }




}