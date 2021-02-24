package com.edgeuserapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.edgeuserapp.R;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {
    private NavController navController;
    MotionLayout braceletMotionLayout;
    MotionLayout monitorMotionLayout;
    MotionLayout riskMotionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Map<Integer, MotionLayout> destinationMap = new HashMap<>();
        braceletMotionLayout = findViewById(R.id.braceletMotionLayout);
        monitorMotionLayout = findViewById(R.id.monitorMotionLayout);
        riskMotionLayout = findViewById(R.id.riskMotionLayout);
        destinationMap.put(R.id.braceletFragment, braceletMotionLayout);
        destinationMap.put(R.id.monitorFragment, monitorMotionLayout);
        destinationMap.put(R.id.riskFragment, riskMotionLayout);
        navController = Navigation.findNavController(this, R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(destinationMap.keySet()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        for (Integer key: destinationMap.keySet()) {
            destinationMap.get(key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(key);
                }
            });
        }


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                // 禁止控制器存储栈
                controller.popBackStack();
                // 每次点击检测到改变时，每一个MotionLayout动画重置，实现单选
               for(MotionLayout motionLayout: destinationMap.values()) {
                   motionLayout.setProgress(0f);
               }
                // 目标MotionLayout进行动画播放直至结束
                int destinationId = destination.getId();
                destinationMap.get(destinationId).transitionToEnd();
            }
        });
    }


}