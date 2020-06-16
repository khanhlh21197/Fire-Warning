package com.khanhlh.firewarning.main;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.khanhlh.firewarning.BR;
import com.khanhlh.firewarning.R;
import com.khanhlh.firewarning.base.BaseActivity;
import com.khanhlh.firewarning.databinding.ActivityMainBinding;
import com.khanhlh.firewarning.irview.IRPicture;
import com.khanhlh.firewarning.irview.IRView;
import com.khanhlh.firewarning.irview.OTC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements MainNavigator {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private IRView irView;
    private IRPicture irPicture = null;

    @Override
    public int getBindingVariable() {
        return BR.mainVM;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainViewModel getViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        return viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toggle = new ActionBarDrawerToggle(this,
                binding.drawer,
                toolbar,
                R.string.common_open_on_phone,
                R.string.demo_content);

        initIRView();
    }

    private void initIRView() {
        irView = findViewById(R.id.irView);
        irPicture = new IRPicture(OTC.IR_WIDTH, OTC.IR_HEIGHT);
        irView.setIRPicture(irPicture);
        generateTempImage();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void generateTempImage() {
        //khanhlh
        @SuppressLint("WrongViewCast") Queue<double[][]> tempData = new LinkedList<>();
        binding.btnGen.setOnClickListener(v -> {
            irView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    double d = 0;
                    Log.e("Start loop", getFormatTime());
                    for (int i = 0; i < OTC.IR_HEIGHT; i++) {
                        for (int j = 0; j < OTC.IR_WIDTH; j++) {
                            if (i > OTC.IR_WIDTH / 2) {
                                d = getRandomNumberInRange(28, 36);
                            } else {
                                d = getRandomNumberInRange(30, 40);
                            }
                            OTC.tempData2D[i][j] = d;
                        }
                    }
                    tempData.add(OTC.tempData2D);
                    Log.e("End loop", getFormatTime());

                    Log.e("Start update image", String.valueOf(getFormatTime()));
                    irPicture.updateTemperatureData(OTC.tempData2D);
                    irView.update();

                    Log.e("End update image", String.valueOf(getFormatTime()));
                    handler.postDelayed(this, 100 / 3);
                }
            }, 100 / 3);
        });
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private String getFormatTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
