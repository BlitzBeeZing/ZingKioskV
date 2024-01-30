package app.zingnow.zingkiosk.utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

public class TypeWriter extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence mText;
    private int mIndex;
    private long mDelay = 100; // in ms
    private long mPauseDelay = 1000; // 1 second in ms

    Boolean isBackspace = true;

    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if (mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                // If the text is complete, wait for a second before restarting
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIndex = 0;
                        setText("");
                        mHandler.postDelayed(characterAdder, mDelay);
                    }
                }, mPauseDelay);
            }
        }
    };

    public void animateText(CharSequence txt) {
        mText = txt;
        mIndex = 0;
        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long m) {
        mDelay = m;
    }

    public void setPauseDelay(long pauseDelay) {
        mPauseDelay = pauseDelay;
    }
}
