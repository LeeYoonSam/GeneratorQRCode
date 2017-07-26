package qrcode.ys.com.generatorqrcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import qrcode.ys.com.generatorqrcode.password.PasswordDialog;

/**
 * Created by Albert-IM on 26/07/2017.
 */


public class MenuAct extends AppCompatActivity {

    PasswordDialog passwordDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
    }

    PasswordDialog.PasswordCorrectListener passwordCorrectListener = new PasswordDialog.PasswordCorrectListener() {
        @Override
        public void onCorrect() {
            Intent i = new Intent(MenuAct.this, GeneratorQRCode.class);
            startActivity(i);
        }
    };

    public void moveGeneratorQR(View view) {
        Intent i = new Intent(MenuAct.this, GeneratorQRCode.class);
        startActivity(i);
    }


    public void moveSave(View view) {
        passwordDialog = new PasswordDialog(this, PasswordDialog.PASSWORD_SAVE, passwordCorrectListener);
        passwordDialog.show();
    }

    public void moveChange(View view) {
        passwordDialog = new PasswordDialog(this, PasswordDialog.PASSWORD_CHANGE, passwordCorrectListener);
        passwordDialog.show();
    }

    public void moveCheck(View view) {
        passwordDialog = new PasswordDialog(this, PasswordDialog.PASSWORD_CHECK, passwordCorrectListener);
        passwordDialog.show();
    }

}
