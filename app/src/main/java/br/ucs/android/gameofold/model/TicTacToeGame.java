package br.ucs.android.gameofold.model;

enum PlayState {
    Valid,
    Invalid,
    Win
}

public class TicTacToeGame {
    int[] board;

    public TicTacToeGame() {
        this.board = new int[9];
    }

    public PlayState play(int position, int playerId) {
        if (board[position] != 0)
            return PlayState.Invalid;

        board[position] = playerId;

        if (verifyWin(playerId))
            return PlayState.Win;

        return PlayState.Valid;
    }

    private boolean verifyWin(int player) {
        return verifyDiagonal(player) || verifyHorizontal(player) || verifyVertical(player);
    }

    private Boolean verifyVertical(int player) {
        int firstColumn = board[0] + board[3] + board[6];
        int secondColumn = board[1] + board[4] + board[7];
        int thirdColumn =  board[2] + board[5] + board[8];

        return firstColumn / 3 == player || secondColumn / 3 == player || thirdColumn / 3 == player;
    }

    private Boolean verifyHorizontal(int player) {
        int firstLine = board[0] + board[1] + board[2];
        int secondLine = board[3] + board[4] + board[5];
        int thirdLine =  board[6] + board[7] + board[8];

        return firstLine / 3 == player || secondLine / 3 == player || thirdLine / 3 == player;
    }

    private Boolean verifyDiagonal(int player) {
        int crescDiagonal = board[0] + board[1] + board[2];
        int decrescDiagonal = board[2] + board[4] + board[6];

        return crescDiagonal / 3 == player || decrescDiagonal / 3 == player;
    }
}
