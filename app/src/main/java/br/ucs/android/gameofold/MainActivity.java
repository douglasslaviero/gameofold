package br.ucs.android.gameofold;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import br.ucs.android.gameofold.model.PlayState;
import br.ucs.android.gameofold.model.TicTacToeGame;
import br.ucs.android.gameofold.model.WinCondition;

public class MainActivity extends AppCompatActivity {

    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;
    private final int SELFIE1 = 3;
    private final int SELFIE2 = 4;
    private ShapeDrawable winnerShape;

    private File selfieFile1 = null;
    private File selfieFile2 = null;
    private TicTacToeGame game;
    private int currentPlayerId = 0;

    private HashMap<Integer, Integer> boardMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pede permissão para acessar as mídias gravadas no dispositivo
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        // Pede permissão para escrever arquivos no dispositivo
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }

        winnerShape = new ShapeDrawable();
        winnerShape.setShape(new RectShape());
        winnerShape.getPaint().setColor(Color.GREEN);
        winnerShape.getPaint().setStrokeWidth(30f);
        winnerShape.getPaint().setStyle(Paint.Style.STROKE);

        game = new TicTacToeGame();

        boardMap.put(0, R.id.imageButton0);
        boardMap.put(1, R.id.imageButton1);
        boardMap.put(2, R.id.imageButton2);
        boardMap.put(3, R.id.imageButton3);
        boardMap.put(4, R.id.imageButton4);
        boardMap.put(5, R.id.imageButton5);
        boardMap.put(6, R.id.imageButton6);
        boardMap.put(7, R.id.imageButton7);
        boardMap.put(8, R.id.imageButton8);
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

        selfieFile1 = selfieFile2 = null;
        clearGame();
    }

    public void takeASelfie1(View view) {
        if (selfieFile1 == null)
            this.takeASelfie(view, selfieFile1, SELFIE1);
        else if (currentPlayerId == 0)
            currentPlayerId = 1;
    }

    public void takeASelfie2(View view) {
        if (selfieFile2 == null)
            this.takeASelfie(view, selfieFile2, SELFIE2);
        else if (currentPlayerId == 0)
            currentPlayerId = 2;
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

    public void play0(View view) {
        ImageButton ib = findViewById(R.id.imageButton0);
        this.play(view, 0, ib);
    }

    public void play1(View view) {
        ImageButton ib = findViewById(R.id.imageButton1);
        this.play(view, 1, ib);
    }

    public void play2(View view) {
        ImageButton ib = findViewById(R.id.imageButton2);
        this.play(view, 2, ib);
    }

    public void play3(View view) {
        ImageButton ib = findViewById(R.id.imageButton3);
        this.play(view, 3, ib);
    }

    public void play4(View view) {
        ImageButton ib = findViewById(R.id.imageButton4);
        this.play(view, 4, ib);
    }

    public void play5(View view) {
        ImageButton ib = findViewById(R.id.imageButton5);
        this.play(view, 5, ib);
    }

    public void play6(View view) {
        ImageButton ib = findViewById(R.id.imageButton6);
        this.play(view, 6, ib);
    }

    public void play7(View view) {
        ImageButton ib = findViewById(R.id.imageButton7);
        this.play(view, 7, ib);
    }

    public void play8(View view) {
        ImageButton ib = findViewById(R.id.imageButton8);
        this.play(view, 8, ib);
    }

    private void play(View view, int position, ImageButton ib) {

        if (selfieFile1 == null) {
            alert("Selfie não tirada", "Jogador 1, por favor tire sua selfie antes de jogar!");
            return;
        } else if (selfieFile2 == null) {
            alert("Selfie não tirada", "Jogador 2, por favor tire sua selfie antes de jogar!");
            return;
        }

        PlayState play = game.play(position, currentPlayerId % 2 + 1);

        if (play == PlayState.Invalid)
            return;

        if (currentPlayerId % 2 == 0)
            ib.setImageBitmap(BitmapFactory.decodeFile(selfieFile2.getAbsolutePath()));
        else
            ib.setImageBitmap(BitmapFactory.decodeFile(selfieFile1.getAbsolutePath()));

        if (play == PlayState.Win) {
            showWinner(game.winCondition);
            //(new Handler()).postDelayed(this::anounceWinner, 3000);

            try
            {
                Thread.sleep(3000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }

            anounceWinner();
            clearGame();
        } else if (play == PlayState.Draw) {
            alert("Empate", "O jogo empatou.");
            clearGame();
        }

        currentPlayerId++;
    }

    private void anounceWinner() {
        int playerId = 1;
        if (currentPlayerId % 2 == 0) {
            playerId = 2;
        }
        alert("Parabéns", String.format("Jogador " + playerId + " venceu!"));
    }

    private void showWinner(WinCondition winCondition) {
        switch (winCondition) {
            case FirstLine:
                showWinner(0, 1, 2);
                break;
            case SecondLine:
                showWinner(3, 4, 5);
                break;
            case ThirdLine:
                showWinner(6, 7, 8);
                break;
            case FirstColumn:
                showWinner(0, 3, 6);
                break;
            case SecondColumn:
                showWinner(1, 4, 7);
                break;
            case ThirdColumn:
                showWinner(2, 5, 8);
                break;
            case CrescDiagonal:
                showWinner(0, 4, 8);
                break;
            case DecrescDiagonal:
                showWinner(2, 4, 6);
                break;
        }
    }

    private void showWinner(int n1, int n2, int n3)
    {
        ImageButton ib1 = findViewById(boardMap.get(n1));
        ImageButton ib2 = findViewById(boardMap.get(n2));
        ImageButton ib3 = findViewById(boardMap.get(n3));
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void alert(String titulo, String mensagem) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_Hhmmss").format(new Date());
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(folder.getPath() + File.separator + "JPG_" + timeStamp + ".jpg");
    }

    private void clearButtons() {
        for(int i = 0; i < 9; i++)
        {
            ImageButton ib = findViewById(boardMap.get(i));
            ib.setImageBitmap(null);
            ib.setForeground(null);
        }
    }

    private void clearGame() {
        currentPlayerId = 0;
        game.clearBoard();
        clearButtons();
    }
}