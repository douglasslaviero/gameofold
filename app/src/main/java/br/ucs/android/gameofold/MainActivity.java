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

        winnerShape = new ShapeDrawable();
        winnerShape.setShape(new RectShape());
        winnerShape.getPaint().setColor(Color.GREEN);
        winnerShape.getPaint().setStrokeWidth(30f);
        winnerShape.getPaint().setStyle(Paint.Style.STROKE);

        game = new TicTacToeGame();
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
        currentPlayerId = -1;

        game.clearBoard();
        clearButtons();
    }

    public void takeASelfie1(View view) {
        if(selfieFile1 == null)
            this.takeASelfie(view, selfieFile1, SELFIE1);
        else if(currentPlayerId == 0)
            currentPlayerId = 1;
    }

    public void takeASelfie2(View view) {
        if(selfieFile2 == null)
            this.takeASelfie(view, selfieFile2, SELFIE2);
        else if(currentPlayerId == 0)
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

    private void firstLineWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton0);
        ImageButton ib2 = findViewById(R.id.imageButton1);
        ImageButton ib3 = findViewById(R.id.imageButton2);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);

    }

    private void secondLineWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton3);
        ImageButton ib2 = findViewById(R.id.imageButton4);
        ImageButton ib3 = findViewById(R.id.imageButton5);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void thirdLineWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton6);
        ImageButton ib2 = findViewById(R.id.imageButton7);
        ImageButton ib3 = findViewById(R.id.imageButton8);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void firstColumnWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton0);
        ImageButton ib2 = findViewById(R.id.imageButton3);
        ImageButton ib3 = findViewById(R.id.imageButton6);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void secondColumnWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton1);
        ImageButton ib2 = findViewById(R.id.imageButton4);
        ImageButton ib3 = findViewById(R.id.imageButton7);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void thirdColumnWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton2);
        ImageButton ib2 = findViewById(R.id.imageButton5);
        ImageButton ib3 = findViewById(R.id.imageButton8);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void crescDiagonalWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton0);
        ImageButton ib2 = findViewById(R.id.imageButton4);
        ImageButton ib3 = findViewById(R.id.imageButton8);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    private void decrescDiagonalWinner(){
        ImageButton ib1 = findViewById(R.id.imageButton2);
        ImageButton ib2 = findViewById(R.id.imageButton4);
        ImageButton ib3 = findViewById(R.id.imageButton6);
        ib1.setForeground(winnerShape);
        ib2.setForeground(winnerShape);
        ib3.setForeground(winnerShape);
    }

    public void play0(View view){
        ImageButton ib = findViewById(R.id.imageButton0);
        this.play(view, 0, ib);
    }

    public void play1(View view){
        ImageButton ib = findViewById(R.id.imageButton1);
        this.play(view, 1, ib);
    }

    public void play2(View view){
        ImageButton ib = findViewById(R.id.imageButton2);
        this.play(view, 2, ib);
    }

    public void play3(View view){
        ImageButton ib = findViewById(R.id.imageButton3);
        this.play(view, 3, ib);
    }

    public void play4(View view){
        ImageButton ib = findViewById(R.id.imageButton4);
        this.play(view, 4, ib);
    }

    public void play5(View view){
        ImageButton ib = findViewById(R.id.imageButton5);
        this.play(view, 5, ib);
    }

    public void play6(View view){
        ImageButton ib = findViewById(R.id.imageButton6);
        this.play(view, 6, ib);
    }

    public void play7(View view){
        ImageButton ib = findViewById(R.id.imageButton7);
        this.play(view, 7, ib);
    }

    public void play8(View view){
        ImageButton ib = findViewById(R.id.imageButton8);
        this.play(view, 8, ib);
    }

    private void play(View view, int position, ImageButton ib){

        if(selfieFile1 == null)
        {
            alert("Dai não né man", "Oh jogador 1, tem que tirar a foto antes po");
            return;
        }
        else if(selfieFile2 == null)
        {
            alert("Dai não né man", "Oh jogador 2, tem que tirar a foto antes po");
            return;
        }

        PlayState play = game.play(position, currentPlayerId % 2 + 1);

        if(play == PlayState.Invalid)
            return;

        if(currentPlayerId % 2 == 0)
            ib.setImageBitmap(BitmapFactory.decodeFile(selfieFile2.getAbsolutePath()));
        else
            ib.setImageBitmap(BitmapFactory.decodeFile(selfieFile1.getAbsolutePath()));

        if(play == PlayState.Win){
            showWinner(game.winCondition);
            anounceWinner();
            //(new Handler()).postDelayed(this::anounceWinner, 3000);
            //clear(view);
        }
        else if (play == PlayState.Draw){
            alert("Empate", "O jogo empatou.");
            clear(view);
        }

        currentPlayerId++;
    }

    private void anounceWinner(){
        int playerId = 1;
        if (currentPlayerId % 2 == 0){
            playerId = 2;
        }
        alert("Parabéns", String.format("Jogador "+ playerId +" venceu!"));
    }

    private void showWinner(WinCondition winCondition){
    switch (winCondition){
        case FirstLine: firstLineWinner();
        break;
        case SecondLine: secondLineWinner();
        break;
        case ThirdLine: thirdLineWinner();
        break;
        case FirstColumn: firstColumnWinner();
        break;
        case SecondColumn: secondColumnWinner();
        break;
        case ThirdColumn: thirdColumnWinner();
        break;
        case CrescDiagonal: crescDiagonalWinner();
        break;
        case DecrescDiagonal: decrescDiagonalWinner();
        break;
    }


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

    private void clearButtons()
    {
        ImageButton ib0 = findViewById(R.id.imageButton0);
        ib0.setImageBitmap(null);
        ib0.setForeground(null);
        ImageButton ib1 = findViewById(R.id.imageButton1);
        ib1.setImageBitmap(null);
        ib1.setForeground(null);
        ImageButton ib2 = findViewById(R.id.imageButton2);
        ib2.setImageBitmap(null);
        ib2.setForeground(null);
        ImageButton ib3 = findViewById(R.id.imageButton3);
        ib3.setImageBitmap(null);
        ib3.setForeground(null);
        ImageButton ib4 = findViewById(R.id.imageButton4);
        ib4.setImageBitmap(null);
        ib4.setForeground(null);
        ImageButton ib5 = findViewById(R.id.imageButton5);
        ib5.setImageBitmap(null);
        ib5.setForeground(null);
        ImageButton ib6 = findViewById(R.id.imageButton6);
        ib6.setImageBitmap(null);
        ib6.setForeground(null);
        ImageButton ib7 = findViewById(R.id.imageButton7);
        ib7.setImageBitmap(null);
        ib7.setForeground(null);
        ImageButton ib8 = findViewById(R.id.imageButton8);
        ib8.setImageBitmap(null);
        ib8.setForeground(null);
    }
}