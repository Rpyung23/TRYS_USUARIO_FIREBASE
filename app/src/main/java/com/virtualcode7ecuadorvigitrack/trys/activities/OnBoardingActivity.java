package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.fragment.BlankFragment;
import com.virtualcode7ecuadorvigitrack.trys.provider.cProviderSharedPreferencesOnBoarding;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity
{
    private cProviderSharedPreferencesOnBoarding mProviderSharedPreferencesOnBoarding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        mProviderSharedPreferencesOnBoarding =
                new cProviderSharedPreferencesOnBoarding(OnBoardingActivity.this);
        if (mProviderSharedPreferencesOnBoarding.readSharedOnBoarding()!=0)
        {
            finish();
        }
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Escribe tu destino",
                "Proporciona tu ubicación de viaje o simplemente solicita un taxi",
                Color.WHITE, R.drawable.rutas_taxi_onboarding, R.drawable.onboarding_taxi);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Solicita un taxi",
                "Selecciona el conductor más cercano en un radio de 5 km",
                Color.WHITE, R.drawable.solicita_taxi_onboarding, R.drawable.onboarding_taxi);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Toma tu taxi",
                "Verificamos cuidadosamente todos los conductores antes de agregarlos a la aplicación",
                Color.WHITE, R.drawable.toma_taxi_onboarding, R.drawable.onboarding_taxi);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);

        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();
        onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut()
            {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        mProviderSharedPreferencesOnBoarding.writeSharedOnBoarding(1);
        Intent intent = new Intent(OnBoardingActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        super.onDestroy();
    }
}