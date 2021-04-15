package br.ucs.android.gameofold;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int SELFIE1 = 3;
    private final int SELFIE2 = 4;

    private File selfieFile1 = null;
    private File selfieFile2 = null;

    private ImageView selfie1;
    private ImageView selfie2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == SELFIE1){
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(selfieFile1)
            ));
            showSelfie1(selfieFile1.getAbsolutePath());
        }

        if(resultCode == RESULT_OK && requestCode == SELFIE2){
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(selfieFile2)
            ));
            showSelfie1(selfieFile2.getAbsolutePath());
        }
    }

    private void showSelfie1(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        selfie1.setImageBitmap(bitmap);
    }

    private void showSelfie2(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        selfie1.setImageBitmap(bitmap);
    }

    public void takeASelfie1(View view)
    {
        this.takeASelfie(view, selfieFile1, SELFIE1);
    }

    public void takeASelfie2(View view)
    {
        this.takeASelfie(view, selfieFile2, SELFIE2);
    }

    public void takeASelfie(View view, File selfieFile, int selfieCode)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            try
            {
                selfieFile = createFile();
            }
            catch (IOException ex)
            {
                //mostraAlerta(getString(R.string.erro), getString(R.string.erro_salvando_foto));
            }

            if (selfieFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(
                        getBaseContext(),
                        getBaseContext().getApplicationContext().getPackageName() + ".provider",
                        selfieFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, selfieCode);
            }
        }
    }

    private File createFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_Hhmmss").format(new Date());
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(folder.getPath() + File.separator + "JPG_" + timeStamp + ".jpg");
        return image;
    }
}