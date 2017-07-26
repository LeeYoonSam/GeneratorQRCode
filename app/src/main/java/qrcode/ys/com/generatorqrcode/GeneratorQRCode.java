package qrcode.ys.com.generatorqrcode;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONObject;


public class GeneratorQRCode extends Activity {

    JSONObject json = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_generatorqr);

        try {

            json.put("deviceName", BluetoothAdapter.getDefaultAdapter().getName());
            json.put("deviceAddress", BluetoothAdapter.getDefaultAdapter().getAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

        generatorQRCode();
    }

    public void generatorQRCode() {
        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;

        // 화면을 3/4 비율로 사이즈 계산
        smallerDimension = smallerDimension * 3/4;


        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(json.toString(),
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            // 이미지뷰에 인코딩된 QR 이미지를 세팅
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView ivQRCode = (ImageView) findViewById(R.id.ivQRCode);
            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
