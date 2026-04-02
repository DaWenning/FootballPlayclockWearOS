package de.recklessgreed.footballplayclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlayClockActivity extends AppCompatActivity {

    private static final String TAG = "PlayClockActivity";

    GestureDetector gestureDetector;

    TextView timeOfDay;
    TextView playclock;

    Handler dayoftimeHandler;
    Runnable dayoftimeRunnable;

    Handler playclockHandler;
    Runnable playclockRunnable;

    int currentPlayClock = 0;
    boolean isRunning = false;
    boolean fourty = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout setzen
        setContentView(R.layout.activity_playclock);

        // Keep screen on while app is open
        findViewById(R.id.playclockActivity).setKeepScreenOn(true);

        // TextViews aus dem Layout binden
        playclock = findViewById(R.id.playclock_indicator);
        timeOfDay = findViewById(R.id.playclock_time_of_day);

        dayoftimeHandler = new Handler(Looper.getMainLooper());
        dayoftimeRunnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
                String currentTime = sdf.format(new Date());
                if (timeOfDay != null) {
                    timeOfDay.setText(currentTime);
                }
                dayoftimeHandler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
            }
        };

        playclockHandler = new Handler(Looper.getMainLooper());
        playclockRunnable = getPlayclockRunnable();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "Recieved Fling");
                start25Clock();
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // do nothing
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "Recieved Long Press");
                try {
                    // Show a dialog fragment so the GameClockActivity stays resumed and the ticker keeps running
                    FragmentManager fm = getSupportFragmentManager();
                    OptionsFragment dialog = new OptionsFragment();
                    if (!dialog.isAdded()) {
                        dialog.show(fm, "playclock_options_dialog");
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to open PlayclockOptions dialog", ex);
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                start40Clock();
                return true;
            }
        });

        findViewById(R.id.playclockActivity).setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getApplicationContext(), "Zurück ist deaktiviert!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dayoftimeHandler.post(dayoftimeRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dayoftimeHandler.removeCallbacks(dayoftimeRunnable);
        playclockHandler.removeCallbacks(playclockRunnable);
    }

    public Runnable getPlayclockRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    currentPlayClock --;
                    updatePlayClockText();
                    long nextRunStep = SystemClock.uptimeMillis() + 100;
                    if (fourty && currentPlayClock == 25 * 10 ) {
                        rumble(new long[]{0, 200}, new int[]{0,255});
                    }
                    else if (currentPlayClock == 15 * 10) {
                        rumble(new long[]{0, 400}, new int[]{0,255});
                    }
                    else if (currentPlayClock == 10 * 10) {
                        rumble(new long[]{0, 400, 200, 400}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 5 * 10) {
                        rumble(new long[]{0, 200, 200, 200}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 4 * 10) {
                        rumble(new long[]{0, 200, 200, 200}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 3 * 10) {
                        rumble(new long[]{0, 200, 200, 200}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 2 * 10) {
                        rumble(new long[]{0, 200, 200, 200}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 1 * 10) {
                        rumble(new long[]{0, 200, 200, 200}, new int[]{0,255, 0, 255});
                    }
                    else if (currentPlayClock == 0) {
                        rumble(new long[]{0, 400, 200, 400, 200, 400}, new int[]{0,255, 0, 255, 0, 255});
                        stopPlayclock();
                        return;
                    }
                    playclockHandler.postAtTime(this, nextRunStep);

                }
            }
        };
    }

    private void start25Clock() {
        Log.d(TAG, "Starting 25s play clock");
        rumble(new long[]{0, 200, 200, 200, 200, 200}, new int[]{0,255, 0, 255, 0, 255});
        currentPlayClock = 25 * 10;
        fourty = false;
        startPlayclock();
    }
    private void start40Clock() {
        Log.d(TAG, "Starting 25s play clock");
        rumble(new long[]{0, 400, 200, 400}, new int[]{0,255, 0, 255});
        currentPlayClock = 40 * 10;
        fourty = true;
        startPlayclock();
    }

    private void startPlayclock() {
        if (!isRunning) {
            isRunning = true;
            playclockHandler.postAtTime(playclockRunnable, SystemClock.uptimeMillis() + 100);
        }
    }
    private void stopPlayclock() {
        if (isRunning) {
            isRunning = false;
            playclockHandler.removeCallbacks(playclockRunnable);
        }
    }

    private void updatePlayClockText() {
        int seconds = (int) Math.ceil(currentPlayClock / 10.0);
        String playClockString = String.format(Locale.GERMAN, "%02d", seconds);
        if (playclock != null) {
            playclock.setText(playClockString);
        }
    }

    /* Functional Methods */
    @SuppressLint("MissingPermission")
    protected void rumble(long[] pattern, int[] amplitudes) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator == null || !vibrator.hasVibrator()) {
            Log.w(TAG, "Device does not have a vibrator.");
            return;
        }

        VibrationEffect effect = VibrationEffect.createWaveform(pattern, amplitudes, -1);
        vibrator.vibrate(effect);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle common hardware buttons that might be present on watches or devices
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_1:
            case KeyEvent.KEYCODE_STEM_1:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // Button 1 -> 25s clock
                start25Clock();
                return true;

            case KeyEvent.KEYCODE_BUTTON_2:
            case KeyEvent.KEYCODE_STEM_2:
            case KeyEvent.KEYCODE_VOLUME_UP:
                // Button 2 -> 40s clock
                start40Clock();
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_1:
            case KeyEvent.KEYCODE_STEM_1:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_BUTTON_2:
            case KeyEvent.KEYCODE_STEM_2:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_SPACE:
                // Consume these key-ups so the system doesn't also handle them
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
