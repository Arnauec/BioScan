package upc.hackupc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class product extends AppCompatActivity {
    String barcode;
    String[] dadesProducte = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcode = getIntent().getStringExtra("codiBarres");

        setContentView(R.layout.activity_product);

        NetworkCon nc = new NetworkCon();
        nc.execute();
        while(dadesProducte[9] == null){}
        setValues(dadesProducte);

    }

    public void sanoBut(View v) {
        TextView sano = (TextView) findViewById(R.id.sanoTxt);

        if(sano.getVisibility() != View.VISIBLE){
            sano.setVisibility(View.VISIBLE);
            sano.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            sano.setText(dadesProducte[4]);
        }else{
            sano.setVisibility(View.INVISIBLE);
            sano.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
            sano.setText("");
        }
    }

    public void ecoBut(View v) {
        TextView eco = (TextView) findViewById(R.id.ecoTxt);

        if(eco.getVisibility() != View.VISIBLE){
            eco.setVisibility(View.VISIBLE);
            eco.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            eco.setText(dadesProducte[6]);
        }else{
            eco.setVisibility(View.INVISIBLE);
            eco.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
            eco.setText("");
        }
    }

    public void comenBut(View v) {
        TextView com = (TextView) findViewById(R.id.comTxt);

        if(com.getVisibility() != View.VISIBLE){
            com.setVisibility(View.VISIBLE);
            com.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            com.setText(dadesProducte[8]);
        }else{
            com.setVisibility(View.INVISIBLE);
            com.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0);
            com.setText("");
        }
    }

    public void setValues(String[] dadesProducte){
        TextView nota = (TextView) findViewById(R.id.notaLab);
        System.out.println(dadesProducte[2]);
        nota.setText(dadesProducte[2]);

        ImageView img = (ImageView) findViewById(R.id.productImg);
        img.setImageURI(Uri.parse(dadesProducte[3]));

        new DownloadImageTask((ImageView) findViewById(R.id.productImg))
                .execute(dadesProducte[3]);

        TextView notaSano = (TextView) findViewById(R.id.notaSanoTxt);
        TextView notaEco = (TextView) findViewById(R.id.notaEcoTxt);
        TextView notaCom = (TextView) findViewById(R.id.notaComTxt);

        notaSano.setVisibility(View.VISIBLE);
        notaSano.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        notaSano.setText(dadesProducte[5]);
        notaSano.setZ(100);
        notaEco.setVisibility(View.VISIBLE);
        notaEco.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        notaEco.setText(dadesProducte[7]);
        notaEco.setZ(100);
        notaCom.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        notaCom.setVisibility(View.VISIBLE);
        notaCom.setText(dadesProducte[9]);
        notaCom.setZ(100);

    }

    private class NetworkCon extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                barcode = getIntent().getStringExtra("codiBarres");

                String link = "http://epicur.me/hackupc/hackupc.php?code=" + barcode;
                System.out.println(link);
                String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(link);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    if(i == 2) {
                        String[] parts = line.split(";");
                        for (int c = 0; c < 10; c++) {
                            dadesProducte[c] = parts[c];
                            System.out.println(dadesProducte[c]);
                        }
                    }
                    i++;
                }


                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return "Tot fet";
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}


