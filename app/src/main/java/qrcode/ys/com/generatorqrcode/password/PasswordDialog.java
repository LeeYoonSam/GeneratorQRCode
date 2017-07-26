package qrcode.ys.com.generatorqrcode.password;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import qrcode.ys.com.generatorqrcode.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Albert-IM on 26/07/2017.
 */

public class PasswordDialog extends Dialog {

    public interface PasswordCorrectListener {
        void onCorrect();
    }

    public static final String TAG = "PinLockView";

    public static final int PASSWORD_LENGTH = 8;

    public static final int PASSWORD_CHECK = 0;
    public static final int PASSWORD_SAVE = 1;
    public static final int PASSWORD_CHANGE = 2;

    public static final String PREF_NAME = "PinLockView";
    public static final String PIN_CODE = "pinCode";

    PasswordCorrectListener passwordCorrectListener;

    // default
    int mViewType = PASSWORD_CHECK;

    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;

    String pinCode;
    String correctCode;
    boolean isCheckCorrect = false;
    boolean isComplete = false;
    boolean isChange = false;

    Button btAction;
    TextView tvTitle;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);

            isComplete = true;
            pinCode = pin;

            if(mViewType == PASSWORD_CHECK || isChange) {
                SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString(PIN_CODE, null);

                if (restoredText != null) {
                    if(restoredText.equals(pinCode)) {
                        if(isChange) {
                            btAction.setVisibility(View.VISIBLE);
                            btAction.setText("변경하기");
                            btAction.setOnClickListener(saveListener);

                            isChange = false;
                            tvTitle.setText("새로운 비밀번호 입력");

                            mPinLockView.resetPinLockView();

                        } else {
                            passwordCorrectListener.onCorrect();
                            dismiss();
                        }
                    } else {
                        Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.\n다시 입력해주세요.", Toast.LENGTH_SHORT).show();

                        // 화면 초기화
                        mPinLockView.resetPinLockView();
                    }
                }
            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");

            isComplete = false;
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);

            isComplete = false;
            pinCode = intermediatePin;

        }
    };

    public PasswordDialog(Context context, int viewType, PasswordCorrectListener listener) {
        // Dialog 배경을 투명 처리 해준다.
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mViewType = viewType;
        passwordCorrectListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_pinlock);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        btAction = (Button) findViewById(R.id.btAction);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        setInit(mViewType);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        mPinLockView.setPinLength(PASSWORD_LENGTH);
        mPinLockView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        // 비어있는 dot까지 한번에 보여줌(가독성 좋음)
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
    }

    // ViewType, 텍스트 및 버튼 초기화
    void setInit(int viewType) {

        switch (viewType) {
            case PASSWORD_CHECK:

                mViewType = PASSWORD_CHECK;
                btAction.setVisibility(View.GONE);

                break;

            case PASSWORD_SAVE:

                mViewType = PASSWORD_SAVE;
                btAction.setText("저장하기");
                btAction.setOnClickListener(saveListener);

                tvTitle.setText("새로운 비밀번호 입력");

                break;

            case PASSWORD_CHANGE:

                mViewType = PASSWORD_CHANGE;

                isChange = true;
                tvTitle.setText("기존 비밀번호 입력");
                btAction.setVisibility(View.GONE);

                break;
        }
    }

    // 비밀번호 입력  일치확인
    public boolean checkCorrect() {
        if(!isCheckCorrect) {

            tvTitle.setText("비밀번호 확인");

            isCheckCorrect = true;

            correctCode = pinCode;
            pinCode = "";

            mPinLockView.resetPinLockView();

            return false;
        } else {
            if(correctCode.equals(pinCode)) {
                return true;
            } else {

                Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.\n다시 입력해주세요.", Toast.LENGTH_SHORT).show();

                isCheckCorrect = false;
                pinCode = "";
                correctCode = "";

                tvTitle.setText("새로운 비밀번호 입력");

                mPinLockView.resetPinLockView();

                return false;
            }
        }
    }

    // 비밀번호 저장하는 클릭 리스너
    View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(isComplete) {


                if(checkCorrect()) {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                    editor.putString(PIN_CODE, pinCode);
                    editor.apply();

                    if(mViewType == PASSWORD_CHANGE) {
                        Toast.makeText(getContext(), "비밀번호가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "비밀번호가 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    passwordCorrectListener.onCorrect();

                    dismiss();
                }

            } else {
                Toast.makeText(getContext(), "비밀번호 " + PASSWORD_LENGTH + "자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };
}