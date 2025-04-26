package nocollection;


public class RMIT_Sudoku_Solverss {
     static final int SIZE = 9;
    static int steps = 0;
 
    public static void main(String[] args) {
        int[][] board = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
 
        long start = System.nanoTime();
        if (solve(board)) {
            long end = System.nanoTime();
            print(board);
            System.out.println("\nSteps: " + steps);
            System.out.println("Time: " + ((end - start) / 1_000_000.0) + " ms");
        } else {
            System.out.println("No solution found.");
        }
    }
 
    // In bảng Sudoku
    static void print(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            if (i % 3 == 0 && i != 0) System.out.println("------+-------+------");
            for (int j = 0; j < SIZE; j++) {
                if (j % 3 == 0 && j != 0) System.out.print("| ");
                System.out.print((board[i][j] == 0 ? ". " : board[i][j] + " "));
            }
            System.out.println();
        }
    }
 
    // Giải Sudoku sử dụng backtracking + MRV + LCV + forward checking
    static boolean solve(int[][] board) {

        // find the next best cell to fill using MRV
        int[] cell = findMRV(board);

        
        if (cell == null) return true;
 
        int row = cell[0];
        int col = cell[1];

        // figures out the order of values to try using LCV
        int[] values = getLCV(board, row, col);
        for (int i = 0; i < values.length; i++) {
            int val = values[i];
            if (val == 0) break;
            if (isValid(board, row, col, val)) {
                board[row][col] = val;
                steps++;

                // tries placing numbers, checks forward validity
                if (forwardCheck(board, row, col)) {
                    
                    if (solve(board)) return true;
                }
                board[row][col] = 0; // backtrack if no solution found and tries next value
            }
        }
        return false;
    }
 
    // MRV: chọn ô trống có ít giá trị hợp lệ nhất
    static int[] findMRV(int[][] board) {
        int min = 10;
        int[] best = null;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    int count = countOptions(board, row, col);
                    if (count < min) {
                        min = count;
                        best = new int[]{row, col};
                        if (min == 1) return best;
                    }
                }
            }
        }
        return best;
    }
 
    // LCV: trả về mảng giá trị hợp lệ sắp xếp theo độ gây xung đột tăng dần
    static int[] getLCV(int[][] board, int row, int col) {
        int[] scores = new int[10];
        int[] result = new int[9];
        int idx = 0;
 
        for (int val = 1; val <= 9; val++) {
            if (isValid(board, row, col, val)) {
                scores[val] = countConstraints(board, row, col, val);
                result[idx++] = val;
            }
        }
 
        // Sắp xếp theo số lượng xung đột tăng dần
        for (int i = 0; i < idx - 1; i++) {
            for (int j = i + 1; j < idx; j++) {
                if (scores[result[i]] > scores[result[j]]) {
                    int temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                }
            }
        }
 
        return result;
    }
 
    // Forward checking: kiểm tra xem sau khi gán, các ô còn lại vẫn còn lựa chọn không
    static boolean forwardCheck(int[][] board, int row, int col) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0 && countOptions(board, i, j) == 0)
                    return false;
            }
        }
        return true;
    }
 
    // Đếm số giá trị hợp lệ cho ô (row, col)
    static int countOptions(int[][] board, int row, int col) {
        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) count++;
        }
        return count;
    }
 
    // Tính số lượng ô bị ảnh hưởng nếu gán giá trị này (để dùng trong LCV)
    static int countConstraints(int[][] board, int row, int col, int num) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == 0 && isValid(board, row, i, num)) count++;
            if (board[i][col] == 0 && isValid(board, i, col, num)) count++;
        }
 
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == 0 && isValid(board, i, j, num)) count++;
            }
        }
 
        return count;
    }
 
    // Kiểm tra gán số có hợp lệ không
    static boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
        }
 
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == num) return false;
            }
        }
 
        return true;
    }
}