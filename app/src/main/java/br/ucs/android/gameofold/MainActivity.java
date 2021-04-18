package br.ucs.android.gameofold;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;
    private final int SELFIE1 = 3;
    private final int SELFIE2 = 4;

    private File selfieFile1 = null;
    private File selfieFile2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pede permissão para acessar as mídias gravadas no dispositivo
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        // Pede permissão para escrever arquivos no dispositivo
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELFIE1) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(selfieFile1)
            ));
            showSelfie(selfieFile1.getAbsolutePath(), R.id.player1ImgButton);
        }

        if (resultCode == RESULT_OK && requestCode == SELFIE2) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(selfieFile2)
            ));
            showSelfie(selfieFile2.getAbsolutePath(), R.id.player2ImgButton);
        }
    }

    private void showSelfie(String path, int viewId) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ImageButton ib = findViewById(viewId);
        ib.setImageBitmap(bitmap);
    }

    public void clear(View view) {
        ImageButton ib1 = findViewById(R.id.player1ImgButton);
        ib1.setImageBitmap(null);
        ImageButton ib2 = findViewById(R.id.player2ImgButton);
        ib2.setImageBitmap(null);
    }

    public void takeASelfie1(View view) {
        this.takeASelfie(view, selfieFile1, SELFIE1);
    }

    public void takeASelfie2(View view) {
        this.takeASelfie(view, selfieFile2, SELFIE2);
    }

    public void takeASelfie(View view, File selfieFile, int selfieCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                selfieFile = createFile();
            } catch (IOException ex) {
                //mostraAlerta(getString(R.string.erro), getString(R.string.erro_salvando_foto));
            }

            if (selfieCode == SELFIE1)
                selfieFile1 = selfieFile;
            else
                selfieFile2 = selfieFile;

            if (selfieFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        getBaseContext(),
                        getBaseContext().getApplicationContext().getPackageName() + ".provider",
                        selfieFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, selfieCode);
            }
        }
    }

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_Hhmmss").format(new Date());
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(folder.getPath() + File.separator + "JPG_" + timeStamp + ".jpg");
        return image;
    }
}