package br.ucs.android.gameofold.model;

public class TicTacToeGame {
    int[] board;
    public WinCondition winCondition;
    public TicTacToeGame() {
        clearBoard();
    }

    public void clearBoard()
    {
        this.board = new int[9];
    }

    public PlayState play(int position, int playerId) {
        if (board[position] != 0)
            return PlayState.Invalid;

        board[position] = playerId;

        if (verifyWin(playerId))
            return PlayState.Win;
        
        if (verifyDraw())
            return PlayState.Draw;

        return PlayState.Valid;
    }

    private boolean verifyDraw(){
        for (Integer item: board)
        {
            if (item == 0)
                return false;
        }
        return true;
    }
    
    private boolean verifyWin(int player) {
        return verifyDiagonal(player) || verifyHorizontal(player) || verifyVertical(player);
    }

    private Boolean verifyVertical(int player) {
        String firstColumn = makeString(board[0], board[3], board[6]);
        String secondColumn = makeString(board[1], board[4], board[7]);
        String thirdColumn = makeString(board[2], board[5], board[8]);

        String playerString = makeString(player);

        if (firstColumn.equals(playerString))
        {
            winCondition = WinCondition.FirstColumn;
            return true;
        }
        if (secondColumn.equals(playerString))
        {
            winCondition = WinCondition.SecondColumn;
            return true;
        }
        if (thirdColumn.equals(playerString))
        {
            winCondition = WinCondition.ThirdColumn;
            return true;
        }

        return false;
    }

    private Boolean verifyHorizontal(int player) {
        String firstLine = makeString(board[0], board[1], board[2]);
        String secondLine = makeString(board[3], board[4], board[5]);
        String thirdLine = makeString(board[6], board[7], board[8]);

        String playerString = makeString(player);

        if (firstLine.equals(playerString))
        {
            winCondition = WinCondition.FirstLine;
            return true;
        }
        if (secondLine.equals(playerString))
        {
            winCondition = WinCondition.SecondLine;
            return true;
        }
        if (thirdLine.equals(playerString))
        {
            winCondition = WinCondition.ThirdLine;
            return true;
        }

        return false;
    }

    private Boolean verifyDiagonal(int player) {
        String crescDiagonal = makeString(board[0], board[4], board[8]);
        String decrescDiagonal = makeString(board[2], board[4], board[6]);

        String playerString = makeString(player);

        if (crescDiagonal.equals(playerString))
        {
            winCondition = WinCondition.CrescDiagonal;
            return true;
        }
        if (decrescDiagonal.equals(playerString))
        {
            winCondition = WinCondition.DecrescDiagonal;
            return true;
        }

        return false;
    }

    private String makeString(int n) {
        return makeString(n, n, n);
    }

    private String makeString(int n1, int n2, int n3) {
        return String.format("%d%d%d", n1, n2, n3);
    }
}
