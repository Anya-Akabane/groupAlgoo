package bmlf;
import java.util.*;

public class RMIT_Sudoku_Solver {

    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private static final int TIMEOUT = 120000; // 2 minutes in ms
    private long startTime;

    public int[][] solve(int[][] puzzle) {
        startTime = System.currentTimeMillis();

        int[][] board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[i] = Arrays.copyOf(puzzle[i], SIZE);
        }

        if (!backtrack(board)) {
            throw new RuntimeException("No solution found within time limit.");
        }

        return board;
    }

    private boolean backtrack(int[][] board) {
        if (System.currentTimeMillis() - startTime > TIMEOUT) {
            return false;
        }

        int[] cell = selectUnassignedCell(board); // MRV
        if (cell == null) return true; // Done

        int row = cell[0];
        int col = cell[1];

        List<Integer> values = orderDomainValues(row, col, board); // LCV
        for (int value : values) {
            if (isValid(value, row, col, board)) {
                board[row][col] = value;

                if (forwardCheck(board)) { // forward checking
                    if (backtrack(board)) {
                        return true;
                    }
                }

                board[row][col] = 0; // Backtrack
            }
        }

        return false;
    }

    private int[] selectUnassignedCell(int[][] board) {
        int minOptions = Integer.MAX_VALUE;
        int[] cell = null;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    int options = getPossibleValues(i, j, board).size();
                    if (options < minOptions) {
                        minOptions = options;
                        cell = new int[]{i, j};
                    }
                }
            }
        }
        return cell;
    }

    private List<Integer> orderDomainValues(int row, int col, int[][] board) {
        Map<Integer, Integer> constraintMap = new HashMap<>();
        for (int val : getPossibleValues(row, col, board)) {
            constraintMap.put(val, countConstraints(val, row, col, board));
        }
        List<Integer> values = new ArrayList<>(constraintMap.keySet());
        values.sort(Comparator.comparingInt(constraintMap::get)); // Least Constraining Value
        return values;
    }

    private int countConstraints(int val, int row, int col, int[][] board) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == 0 && isValid(val, row, i, board)) count++;
            if (board[i][col] == 0 && isValid(val, i, col, board)) count++;
        }

        int boxRow = row / SUBGRID * SUBGRID;
        int boxCol = col / SUBGRID * SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                int r = boxRow + i;
                int c = boxCol + j;
                if (board[r][c] == 0 && isValid(val, r, c, board)) count++;
            }
        }

        return count;
    }

    private boolean forwardCheck(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0 && getPossibleValues(i, j, board).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Set<Integer> getPossibleValues(int row, int col, int[][] board) {
        Set<Integer> possible = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            if (isValid(i, row, col, board)) {
                possible.add(i);
            }
        }
        return possible;
    }

    private boolean isValid(int value, int row, int col, int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == value || board[i][col] == value) return false;
        }

        int boxRow = row / SUBGRID * SUBGRID;
        int boxCol = col / SUBGRID * SUBGRID;

        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (board[boxRow + i][boxCol + j] == value) return false;
            }
        }

        return true;
    }
}
