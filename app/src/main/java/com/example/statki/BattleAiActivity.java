package com.example.statki;

import static com.example.statki.R.string.*;
import static com.example.statki.R.color.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class BattleAiActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable;

    private final char water = '~';
//    private final char water = (char) R.string.gamefieldDefault;
    private final char ship = '+';
//    private final char ship = (char) R.string.gamefieldShip;

    private final char hit = 'X';
//    private final char hit = (char) R.string.gamefieldHitShip;
    private final char miss = 'O';
//    private final char miss = (char) R.string.gamefieldMiss;

    @SuppressWarnings("unused")
    private final char sink = '#';
//    private final char sink = (char) R.string.gamefieldSinkShip;


    private final int boardSize = 4;
    private final int numRows = boardSize;
    private final int numCols = boardSize;

    public static int carrierNumber = 0;
    public static int carrierSize = 5;
    public static int battleshipNumber = 0;
    public static int battleshipSize = 4;
    public static int cruiserNumber = 0;
    public static int cruiserSize = 3;
    public static int destroyerNumber = 0;
    public static int destroyerSize = 2;
    public static int patrolNumber = 3;
    public static int patrolSize = 1;

    private static final int shipNumber = carrierNumber+battleshipNumber+cruiserNumber+destroyerNumber+patrolNumber;
    private static final int occupiedFields =
            carrierSize*carrierNumber+
            battleshipSize*battleshipNumber+
            cruiserSize*cruiserNumber+
            destroyerSize*destroyerNumber+
            patrolSize*patrolNumber;

    private static int enemyFields = occupiedFields;
    private static int playerFields = occupiedFields;
    private int[] coords = new int[2];
    private char[][] aiBoard = new char[numRows][numCols];
    private char[][] playerBoard = new char[numRows][numCols];

    public BattleAiActivity(){
//        Intent i = getIntent();
//        patrolNumber = i.getIntExtra("EXTRA_PATROLNUMBER", 3);
//        patrolSize = i.getIntExtra("EXTRA_PATROLSIZE", 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_ai);

        generateAiBoard(aiBoard);
        generatePlayerBoard(playerBoard);
    }

    private void generateAiBoard(char[][] board) {
        Log.d("ai","AI positions");
        generateBoard(
                board,
                boardSize,
                carrierNumber,
                carrierSize,
                battleshipNumber,
                battleshipSize,
                cruiserNumber,
                cruiserSize,
                destroyerNumber,
                destroyerSize,
                patrolNumber,
                patrolSize
        );
        printBoard(board, ship);
        Log.d("ai", "AI board generated");
    }

    private void generatePlayerBoard(char[][] board) {
        Log.d("pl","Player positions");
        generateBoard(
                board,
                boardSize,
                carrierNumber,
                carrierSize,
                battleshipNumber,
                battleshipSize,
                cruiserNumber,
                cruiserSize,
                destroyerNumber,
                destroyerSize,
                patrolNumber,
                patrolSize
        );
        printBoard(board, ship);
        Log.d("pl", "Player board generated");
    }

    private void generateBoard(char[][] board, int boardSize, int carrierNumber, int carrierSize, int battleshipNumber, int battleshipSize, int cruiserNumber, int cruiserSize, int destroyerNumber, int destroyerSize, int patrolNumber, int patrolSize) {

        emptyBoard(board);

        int[] shipNumber = {carrierNumber,battleshipNumber,cruiserNumber,destroyerNumber,patrolNumber};
        int[] shipSize = {carrierSize,battleshipSize,cruiserSize,destroyerSize,patrolSize};
        final char candidate = '?';

//        iterating over ship types
        for(int type = 0; type < shipNumber.length; type++){

//            iterating over ship quantity
            for (int i = 0; i < shipNumber[type]; i++){
                coords = generateRandomCoords(boardSize);

//                if field not eligible, seek new field
                if(!surroundingsEmpty(board, coords, water)){
                    i--;
                    continue;
                }

//                if eligible, set orientation
                String orientation = generateOrientation();

//                if ship size is 1, pick random position
//                if it is larger, iterate over size
                if(shipSize[type] == 1){
                    if (board[coords[0]][coords[1]] == water) {
                        board[coords[0]][coords[1]] = ship;
                        Log.d("ptr", "patrol placed");
                    } else
                        i--;
                } else {
                    for(int j = 0; j < shipSize[type]; j++){
                        if (board[coords[0]][coords[1]] == water) {
                            board[coords[0]][coords[1]] = candidate;
                            switch (orientation){
                                case "north": coords[1]--; break;
                                case "east": coords[0]++; break;
                                case "south": coords[1]++; break;
                                case "west": coords[0]--; break;
                                default: break;
                            }

//                        if ship is obstructed, delete candidates and seek new field
                            if(!surroundingsEmpty(board, coords, water, orientation)){
                                if(orientation.equals("north") || orientation.equals("south")){
                                    for(int k = 0; k < boardSize; k++) {
                                        if (board[coords[0]][k] == candidate)
                                            board[coords[0]][k] = water;
                                    }
                                } else {
                                    for(int k = 0; k < boardSize; k++) {
                                        if (board[k][coords[1]] == candidate)
                                            board[k][coords[1]] = water;
                                    }
                                }

                                i--;
                                break;
                            }
                        } else break;
                    }

//                if position is viable, replace candidates with ship fields
                    if(orientation.equals("north") || orientation.equals("south")){
                        for(int k = 0; k < boardSize; k++) {
                            if (board[coords[0]][k] == candidate)
                                board[coords[0]][k] = ship;
                        }
                    } else {
                        for(int k = 0; k < boardSize; k++) {
                            if (board[k][coords[1]] == candidate)
                                board[k][coords[1]] = ship;
                        }
                    }
                }
            }
        }
    }

    //    placeholder for regular board generator
    private void generateBoardRandom(char[][] board, int shipNumber) {

        emptyBoard(board);

        for(int i=0; i<shipNumber;) {
            coords = generateRandomCoords(boardSize);
            if (board[coords[0]][coords[1]] == water) {
                board[coords[0]][coords[1]] = ship;
//                Log.d("pos", coords[0] + " " + coords[1]);
                i++;
            }
        }
    }

    private void emptyBoard(@NonNull char[][] board){
        for (char[] row :
                board) {
            Arrays.fill(row, water);
        }
    }

    private void printBoard(char[][] board, char ship){
        System.out.print("  ");
        for(int i = 0; i < boardSize; i++){
            System.out.print(i + 1 + " ");
        }
        System.out.println();
        for(int row = 0; row < boardSize; row++){
            if(row != 9)
                System.out.print(row + 1 + " ");
            else
                System.out.print(0 + " ");
            for (int col = 0; col < boardSize; col++){
                char value = board[row][col];
                if(value == ship)
                    System.out.print(ship + " ");
                else
                    System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private int[] generateRandomCoords(int boardSize){
        for (int i=0; i<coords.length; i++){
            coords[i] = new Random().nextInt(boardSize);
        }
        return coords;
    }

    private String generateOrientation() {
        String[] orientations = {"north","east","south","west"};
        int rnd = new Random().nextInt(orientations.length);
        return orientations[rnd];
    }

    private boolean surroundingsEmpty(char[][] board, int[] coords, char water, String orientation){
        try {
            switch (orientation){
                case "north": return (
                        board[coords[0] + 1][coords[1]] == water // east
                                || board[coords[0]][coords[1] + 1] == water // south
                                || board[coords[0] - 1][coords[1]] == water // west
                );
                case "east": return (
                        board[coords[0]][coords[1] - 1] == water // north
                                || board[coords[0]][coords[1] + 1] == water // south
                                || board[coords[0] - 1][coords[1]] == water // west
                );
                case "south": return (
                        board[coords[0]][coords[1] - 1] == water // north
                                || board[coords[0] + 1][coords[1]] == water // east
                                || board[coords[0] - 1][coords[1]] == water // west
                );
                case "west": return (
                        board[coords[0]][coords[1] - 1] == water // north
                                || board[coords[0] + 1][coords[1]] == water // east
                                || board[coords[0]][coords[1] + 1] == water // south
                );
                default: return false;
            }
        } catch (ArrayIndexOutOfBoundsException e){
//            e.printStackTrace();
            return false;
        }

    }

    private boolean surroundingsEmpty(char[][] board, int[] coords, char water){
        try {
            return (
                    board[coords[0]][coords[1] - 1] == water // north
                            || board[coords[0] + 1][coords[1]] == water // east
                            || board[coords[0]][coords[1] + 1] == water // south
                            || board[coords[0] - 1][coords[1]] == water // west
            );
        } catch (ArrayIndexOutOfBoundsException e){
//            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("ResourceAsColor")
    public void onClick(View v){
        Button b = (Button) v;
        TextView gameInfoText = findViewById(R.id.gameInfoText);
        b.setEnabled(false);

        coords = getFieldCoordinates(v);
        int row = coords[0] + 1;
        int col = coords[1] + 1;
        Log.d("pl", gameAimingAt + " " + textRow + " " + row + ", " + textColumn + " " + col);

//        gameInfoText.setText(gameAimingAt + " " + textRow + " " + row + ", " + textColumn + " " + col);

        if(isHit(coords, aiBoard))
            gameInfoText.setText(gamePlayerHit);
        else
            gameInfoText.setText(gamePlayerMiss);


        runnable = () -> {

            if (isHit(coords, aiBoard)) {
                b.setText(String.valueOf(hit));
                aiBoard[coords[0]][coords[1]] = hit;
                b.setBackgroundColor(gamefield_hit);
                if(--enemyFields == 0 || Arrays.asList(aiBoard).contains(ship)) gameOver(true);
            } else {
                b.setText(String.valueOf(miss));
                aiBoard[coords[0]][coords[1]] = miss;
                b.setBackgroundColor(gamefield_miss);

            }

            findViewById(R.id.player_layout).setVisibility(View.GONE);
            aiTurn();

        }; handler.postDelayed(runnable, 2000);
    }

    private void aiTurn() {
        TextView gameInfoText = findViewById(R.id.gameInfoText);
        TextView enemyTurnText = findViewById(R.id.enemyTurnText);
        TextView enemyTurnResult = findViewById(R.id.enemyTurnResult);
        Random r = new Random();

        coords[0] = r.nextInt(boardSize);
        coords[1] = r.nextInt(boardSize);
        int row = coords[0] + 1;
        int col = coords[1] + 1;

        Log.d("ai", gameAimingAt + " " + textRow + " " + row + ", " + textColumn + " " + col);

        gameInfoText.setText(gameEnemyTurnInfo);
        enemyTurnText.setText(gameEnemyTurnInfo + " " + textRow + " " + row + ", " + textColumn + " " + col);

        if(isHit(coords, playerBoard))
            enemyTurnResult.setText(gameEnemyHit);
        else
            enemyTurnResult.setText(gameEnemyMiss);

        runnable = () -> {

            if (isHit(coords, playerBoard)) {
                aiBoard[coords[0]][coords[1]] = hit;
                if(--playerFields == 0 || Arrays.asList(playerBoard).contains(ship)) gameOver(false);
            } else {
                aiBoard[coords[0]][coords[1]] = miss;
            }

            enemyTurnText.setText("");
            enemyTurnResult.setText("");
            gameInfoText.setText(gamePlayerTurn);
            findViewById(R.id.player_layout).setVisibility(View.VISIBLE);

        }; handler.postDelayed(runnable, 2000);
    }

    private boolean isHit(int[] coords, char[][] enemyBoard) {
        return enemyBoard[coords[0]][coords[1]] == ship;
    }

    private void gameOver(boolean win) {
        if(win) Toast.makeText(this, textWin, Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, textLose, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private int[] getFieldCoordinates(@NonNull View v){

        int[] coords = new int[2];

        if (v.getId() == R.id.button00) {
            coords[0] = 0; coords[1] = 0;
        } else if (v.getId() == R.id.button01) {
            coords[0] = 0; coords[1] = 1;
        } else if (v.getId() == R.id.button02) {
            coords[0] = 0; coords[1] = 2;
        } else if (v.getId() == R.id.button03) {
            coords[0] = 0; coords[1] = 3;
        } else if (v.getId() == R.id.button04) {
            coords[0] = 1; coords[1] = 0;
        } else if (v.getId() == R.id.button05) {
            coords[0] = 1; coords[1] = 1;
        } else if (v.getId() == R.id.button06) {
            coords[0] = 1; coords[1] = 2;
        } else if (v.getId() == R.id.button07) {
            coords[0] = 1; coords[1] = 3;
        } else if (v.getId() == R.id.button08) {
            coords[0] = 2; coords[1] = 0;
        } else if (v.getId() == R.id.button09) {
            coords[0] = 2; coords[1] = 1;
        } else if (v.getId() == R.id.button10) {
            coords[0] = 2; coords[1] = 2;
        } else if (v.getId() == R.id.button11) {
            coords[0] = 2; coords[1] = 3;
        } else if (v.getId() == R.id.button12) {
            coords[0] = 3; coords[1] = 0;
        } else if (v.getId() == R.id.button13) {
            coords[0] = 3; coords[1] = 1;
        } else if (v.getId() == R.id.button14) {
            coords[0] = 3; coords[1] = 2;
        } else if (v.getId() == R.id.button15) {
            coords[0] = 3; coords[1] = 3;
        }

        /*
        if (v.getId() == R.id.button00) {
            coords[0] = 0; coords[1] = 0;
        } else if (v.getId() == R.id.button01) {
            coords[0] = 0; coords[1] = 1;
        } else if (v.getId() == R.id.button02) {
            coords[0] = 0; coords[1] = 2;
        } else if (v.getId() == R.id.button03) {
            coords[0] = 0; coords[1] = 3;
        } else if (v.getId() == R.id.button04) {
            coords[0] = 0; coords[1] = 4;
        } else if (v.getId() == R.id.button05) {
            coords[0] = 0; coords[1] = 5;
        } else if (v.getId() == R.id.button06) {
            coords[0] = 0; coords[1] = 6;
        } else if (v.getId() == R.id.button07) {
            coords[0] = 0; coords[1] = 7;
        } else if (v.getId() == R.id.button08) {
            coords[0] = 0; coords[1] = 8;
        } else if (v.getId() == R.id.button09) {
            coords[0] = 0; coords[1] = 9;

        } else if (v.getId() == R.id.button10) {
            coords[0] = 1; coords[1] = 0;
        } else if (v.getId() == R.id.button11) {
            coords[0] = 1; coords[1] = 1;
        } else if (v.getId() == R.id.button12) {
            coords[0] = 1; coords[1] = 2;
        } else if (v.getId() == R.id.button13) {
            coords[0] = 1; coords[1] = 3;
        } else if (v.getId() == R.id.button14) {
            coords[0] = 1; coords[1] = 4;
        } else if (v.getId() == R.id.button15) {
            coords[0] = 1; coords[1] = 5;
        } else if (v.getId() == R.id.button16) {
            coords[0] = 1; coords[1] = 6;
        } else if (v.getId() == R.id.button17) {
            coords[0] = 1; coords[1] = 7;
        } else if (v.getId() == R.id.button18) {
            coords[0] = 1; coords[1] = 8;
        } else if (v.getId() == R.id.button19) {
            coords[0] = 1; coords[1] = 9;
        }

        else if (v.getId() == R.id.button20) {
            coords[0] = 2; coords[1] = 0;
        } else if (v.getId() == R.id.button21) {
            coords[0] = 2; coords[1] = 1;
        } else if (v.getId() == R.id.button22) {
            coords[0] = 2; coords[1] = 2;
        } else if (v.getId() == R.id.button23) {
            coords[0] = 2; coords[1] = 3;
        } else if (v.getId() == R.id.button24) {
            coords[0] = 2; coords[1] = 4;
        } else if (v.getId() == R.id.button25) {
            coords[0] = 2; coords[1] = 5;
        } else if (v.getId() == R.id.button26) {
            coords[0] = 2; coords[1] = 6;
        } else if (v.getId() == R.id.button27) {
            coords[0] = 2; coords[1] = 7;
        } else if (v.getId() == R.id.button28) {
            coords[0] = 2; coords[1] = 8;
        } else if (v.getId() == R.id.button29) {
            coords[0] = 2; coords[1] = 9;

        } else if (v.getId() == R.id.button30) {
            coords[0] = 3; coords[1] = 0;
        } else if (v.getId() == R.id.button31) {
            coords[0] = 3; coords[1] = 1;
        } else if (v.getId() == R.id.button32) {
            coords[0] = 3; coords[1] = 2;
        } else if (v.getId() == R.id.button33) {
            coords[0] = 3; coords[1] = 3;
        } else if (v.getId() == R.id.button34) {
            coords[0] = 3; coords[1] = 4;
        } else if (v.getId() == R.id.button35) {
            coords[0] = 3; coords[1] = 5;
        } else if (v.getId() == R.id.button36) {
            coords[0] = 3; coords[1] = 6;
        } else if (v.getId() == R.id.button37) {
            coords[0] = 3; coords[1] = 7;
        } else if (v.getId() == R.id.button38) {
            coords[0] = 3; coords[1] = 8;
        } else if (v.getId() == R.id.button39) {
            coords[0] = 3; coords[1] = 9;

        } else if (v.getId() == R.id.button40) {
            coords[0] = 4; coords[1] = 0;
        } else if (v.getId() == R.id.button41) {
            coords[0] = 4; coords[1] = 1;
        } else if (v.getId() == R.id.button42) {
            coords[0] = 4; coords[1] = 2;
        } else if (v.getId() == R.id.button43) {
            coords[0] = 4; coords[1] = 3;
        } else if (v.getId() == R.id.button44) {
            coords[0] = 4; coords[1] = 4;
        } else if (v.getId() == R.id.button45) {
            coords[0] = 4; coords[1] = 5;
        } else if (v.getId() == R.id.button46) {
            coords[0] = 4; coords[1] = 6;
        } else if (v.getId() == R.id.button47) {
            coords[0] = 4; coords[1] = 7;
        } else if (v.getId() == R.id.button48) {
            coords[0] = 4; coords[1] = 8;
        } else if (v.getId() == R.id.button49) {
            coords[0] = 4; coords[1] = 9;

        } else if (v.getId() == R.id.button50) {
            coords[0] = 5; coords[1] = 0;
        } else if (v.getId() == R.id.button51) {
            coords[0] = 5; coords[1] = 1;
        } else if (v.getId() == R.id.button52) {
            coords[0] = 5; coords[1] = 2;
        } else if (v.getId() == R.id.button53) {
            coords[0] = 5; coords[1] = 3;
        } else if (v.getId() == R.id.button54) {
            coords[0] = 5; coords[1] = 4;
        } else if (v.getId() == R.id.button55) {
            coords[0] = 5; coords[1] = 5;
        } else if (v.getId() == R.id.button56) {
            coords[0] = 5; coords[1] = 6;
        } else if (v.getId() == R.id.button57) {
            coords[0] = 5; coords[1] = 7;
        } else if (v.getId() == R.id.button58) {
            coords[0] = 5; coords[1] = 8;
        } else if (v.getId() == R.id.button59) {
            coords[0] = 5; coords[1] = 9;

        } else if (v.getId() == R.id.button60) {
            coords[0] = 6; coords[1] = 0;
        } else if (v.getId() == R.id.button61) {
            coords[0] = 6; coords[1] = 1;
        } else if (v.getId() == R.id.button62) {
            coords[0] = 6; coords[1] = 2;
        } else if (v.getId() == R.id.button63) {
            coords[0] = 6; coords[1] = 3;
        } else if (v.getId() == R.id.button64) {
            coords[0] = 6; coords[1] = 4;
        } else if (v.getId() == R.id.button65) {
            coords[0] = 6; coords[1] = 5;
        } else if (v.getId() == R.id.button66) {
            coords[0] = 6; coords[1] = 6;
        } else if (v.getId() == R.id.button67) {
            coords[0] = 6; coords[1] = 7;
        } else if (v.getId() == R.id.button68) {
            coords[0] = 6; coords[1] = 8;
        } else if (v.getId() == R.id.button69) {
            coords[0] = 6; coords[1] = 9;

        } else if (v.getId() == R.id.button70) {
            coords[0] = 7; coords[1] = 0;
        } else if (v.getId() == R.id.button71) {
            coords[0] = 7; coords[1] = 1;
        } else if (v.getId() == R.id.button72) {
            coords[0] = 7; coords[1] = 2;
        } else if (v.getId() == R.id.button73) {
            coords[0] = 7; coords[1] = 3;
        } else if (v.getId() == R.id.button74) {
            coords[0] = 7; coords[1] = 4;
        } else if (v.getId() == R.id.button75) {
            coords[0] = 7; coords[1] = 5;
        } else if (v.getId() == R.id.button76) {
            coords[0] = 7; coords[1] = 6;
        } else if (v.getId() == R.id.button77) {
            coords[0] = 7; coords[1] = 7;
        } else if (v.getId() == R.id.button78) {
            coords[0] = 7; coords[1] = 8;
        } else if (v.getId() == R.id.button79) {
            coords[0] = 7; coords[1] = 9;

        } else if (v.getId() == R.id.button80) {
            coords[0] = 8; coords[1] = 0;
        } else if (v.getId() == R.id.button81) {
            coords[0] = 8; coords[1] = 1;
        } else if (v.getId() == R.id.button82) {
            coords[0] = 8; coords[1] = 2;
        } else if (v.getId() == R.id.button83) {
            coords[0] = 8; coords[1] = 3;
        } else if (v.getId() == R.id.button84) {
            coords[0] = 8; coords[1] = 4;
        } else if (v.getId() == R.id.button85) {
            coords[0] = 8; coords[1] = 5;
        } else if (v.getId() == R.id.button86) {
            coords[0] = 8; coords[1] = 6;
        } else if (v.getId() == R.id.button87) {
            coords[0] = 8; coords[1] = 7;
        } else if (v.getId() == R.id.button88) {
            coords[0] = 8; coords[1] = 8;
        } else if (v.getId() == R.id.button89) {
            coords[0] = 8; coords[1] = 9;

        } else if (v.getId() == R.id.button90) {
            coords[0] = 9; coords[1] = 0;
        } else if (v.getId() == R.id.button91) {
            coords[0] = 9; coords[1] = 1;
        } else if (v.getId() == R.id.button92) {
            coords[0] = 9; coords[1] = 2;
        } else if (v.getId() == R.id.button93) {
            coords[0] = 9; coords[1] = 3;
        } else if (v.getId() == R.id.button94) {
            coords[0] = 9; coords[1] = 4;
        } else if (v.getId() == R.id.button95) {
            coords[0] = 9; coords[1] = 5;
        } else if (v.getId() == R.id.button96) {
            coords[0] = 9; coords[1] = 6;
        } else if (v.getId() == R.id.button97) {
            coords[0] = 9; coords[1] = 7;
        } else if (v.getId() == R.id.button98) {
            coords[0] = 9; coords[1] = 8;
        } else if (v.getId() == R.id.button99) {
            coords[0] = 9; coords[1] = 9;
        }
 */
        return coords;
    }

}