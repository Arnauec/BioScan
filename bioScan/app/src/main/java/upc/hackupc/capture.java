package upc.hackupc;

/**
 * Created by ArnauEC on 03/03/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class capture extends Activity {
    TextView barcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    /* add click event to the scan barcode button */
    public void scanBarcode(View v) {
        Intent intent = new Intent(this, scanner.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    Intent intent = new Intent(this, product.class); //scanner.class
                    intent.putExtra("codiBarres", barcode.displayValue);
                    startActivityForResult(intent, 0);
                    //barcodeResult.setText("Barcode value : " + barcode.displayValue);
                } else {
                    barcodeResult.setText("No barcode found");
                }
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

