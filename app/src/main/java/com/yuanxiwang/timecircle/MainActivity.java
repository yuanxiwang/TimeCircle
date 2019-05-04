package com.yuanxiwang.timecircle;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0x1911;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TimeCircleView timeCircleView = findViewById(R.id.view);
        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WallpaperManager instance = WallpaperManager.getInstance(getApplicationContext());
                if (instance != null && instance.getWallpaperInfo() != null) {
                    Log.e("yxw", instance.getWallpaperInfo().getServiceName());
                    if (instance.getWallpaperInfo().getServiceName().contains(MainWallpaper.class.getSimpleName())) {
                        Toast.makeText(MainActivity.this,"已设置壁纸", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getApplicationContext().getPackageName()
                                , MainWallpaper.class.getCanonicalName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        findViewById(R.id.btn_bg_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(MainActivity.this)
                        .initialColor(Color.WHITE)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("确定")
                        .cancelTitle("取消")
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                timeCircleView.setBgColor(color);
                                Intent intent = new Intent("UpdateReceiver");
                                intent.putExtra("bgColor", color);
                                sendBroadcast(intent);
                            }
                        });
            }
        });
        findViewById(R.id.btn_normal_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(MainActivity.this)
                        .initialColor(Color.BLUE)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("确定")
                        .cancelTitle("取消")
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                timeCircleView.setNormalColor(color);
                                Intent intent = new Intent("UpdateReceiver");
                                intent.putExtra("normalColor", color);
                                sendBroadcast(intent);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        }
    }
}
