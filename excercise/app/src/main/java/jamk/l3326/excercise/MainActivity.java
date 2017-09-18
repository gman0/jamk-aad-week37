package jamk.l3326.excercise;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private CountDownTimer mTimer;

    private NumberPicker mHours;
    private NumberPicker mMinutes;
    private NumberPicker mSeconds;
    private EditText mMessage;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
        initNotifications();
    }

    private void initNotifications() {
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);

        mNotificationBuilder.setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(getText(R.string.notification_title))
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC);
    }

    private void initControls() {
        mHours = (NumberPicker)findViewById(R.id.nHours);
        mMinutes = (NumberPicker)findViewById(R.id.nMinutes);
        mSeconds = (NumberPicker)findViewById(R.id.nSeconds);

        mHours.setMinValue(0);
        mHours.setMaxValue(23);

        mMinutes.setMinValue(0);
        mMinutes.setMaxValue(59);

        mSeconds.setMinValue(1);
        mSeconds.setMaxValue(59);

        mMessage = (EditText)findViewById(R.id.tMessage);
        mButton = (Button)findViewById(R.id.btnSetTimer);
    }

    private void controlsEnabled(boolean enabled) {
        mHours.setEnabled(enabled);
        mMinutes.setEnabled(enabled);
        mSeconds.setEnabled(enabled);
        mMessage.setEnabled(enabled);

        mButton.setText(enabled ? getText(R.string.button_set) : getText(R.string.button_cancel));
    }

    private long getRemainingTime() {
        return ((((mHours.getValue() * 60) + mMinutes.getValue()) * 60) + mSeconds.getValue()) * 1000;
    }

    public void onClick(View view) {
        if (mTimer == null) {
            controlsEnabled(false);

            mTimer = new CountDownTimer(getRemainingTime(), 1000) {
                @Override
                public void onTick(long l) {
                    int hrs = (int)TimeUnit.MILLISECONDS.toHours(l);
                    l -= TimeUnit.HOURS.toMillis(hrs);

                    int min = (int)TimeUnit.MILLISECONDS.toMinutes(l);
                    l -= TimeUnit.MINUTES.toMillis(min);

                    int sec = (int)TimeUnit.MILLISECONDS.toSeconds(l);

                    mHours.setValue(hrs);
                    mMinutes.setValue(min);
                    mSeconds.setValue(sec);
                }

                @Override
                public void onFinish() {
                    CharSequence notifyMsg = getText(R.string.default_message);
                    if (!mMessage.getText().toString().isEmpty()) {
                        notifyMsg = mMessage.getText();
                    }

                    mNotificationBuilder.setContentText(notifyMsg);
                    mNotificationManager.notify(1, mNotificationBuilder.build());

                    resetState();
                }
            };

            mTimer.start();
        } else {
            mTimer.cancel();
            resetState();
        }
    }

    private void resetState() {
        mTimer = null;
        controlsEnabled(true);
        ((EditText)findViewById(R.id.tMessage)).setText("");
    }
}
