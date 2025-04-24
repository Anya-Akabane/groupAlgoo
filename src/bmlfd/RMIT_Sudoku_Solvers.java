package bmlfd;

import java.util.*;

public class RMIT_Sudoku_Solvers {
    private int steps = 0;
    private long startTime;

    static class Cell {
        int row, col;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell)) return false;
            Cell cell = (Cell) o;
            return row == cell.row && col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    public int[][] solve(int[][] puzzle) throws Exception {
        startTime = System.nanoTime();

        // list of valid values each variable
        Map<Cell, List<Integer>> domains = initializeDomains(puzzle);

        if (!backtrack(puzzle, domains, new Stack<>())) {
            throw new Exception("No solution found within constraints.");
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        System.out.println("Solved in steps: " + steps);
        System.out.println("Time taken (ms): " + duration / 1_000_000);
        System.out.println("Memory used (KB): " +
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);

        return puzzle;
    }

    private boolean backtrack(int[][] board, Map<Cell, List<Integer>> domains, Stack<Map.Entry<Cell, Integer>> trail) {
        //trail: history of moves made (cell, value)
        
        Cell cell = selectMRV(domains);
        
        if (cell == null) 
            return true; // Solved

        List<Integer> values = orderLCV(board, cell, domains.get(cell));

        for (int num : values) {
            if (isValid(board, cell.row, cell.col, num)) {
                board[cell.row][cell.col] = num;
                steps++;


                // Save the current domain map in case we need to undo this move
                Map<Cell, List<Integer>> backup = deepCopy(domains);


                // removes num from the domains of all affected cells (same row, col, box)
                if (forwardCheck(domains, cell, num)) {

                    // solving the next cell
                    if (backtrack(board, domains, trail)) 
                        return true;
                }

                board[cell.row][cell.col] = 0; // Undo the move
                domains.clear(); // Restore the original domains
                domains.putAll(backup); // Restore the domains to the previous state
            }
        }
        return false;
    }


    // forward checking: remove num from the domains of all affected cells (same row, col, box)
    private boolean forwardCheck(Map<Cell, List<Integer>> domains, Cell current, int num) {
        
        // You don't need a domain list for a cell you've already filled.
        domains.remove(current);

        for (int i = 0; i < 9; i++) {
            if (!eliminate(domains, new Cell(i, current.col), num)) 
                return false;

            if (!eliminate(domains, new Cell(current.row, i), num)) 
                return false;
        }

        int boxRowStart = 3 * (current.row / 3);
        int boxColStart = 3 * (current.col / 3);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (!eliminate(domains, new Cell(boxRowStart + r, boxColStart + c), num)) 
                    return false;

        return true;
    }

    private boolean eliminate(Map<Cell, List<Integer>> domains, Cell cell, int num) {
        
        // if still unassigned
        if (domains.containsKey(cell)) {

            //Get the current list of valid numbers for this cell
            List<Integer> domain = domains.get(cell);
            
            //  Try to remove the number we just placed somewhere else (in a peer cell)
            domain.remove(Integer.valueOf(num));
            
            if (domain.isEmpty()) 
                return false;
        }
        return true;
    }

    private List<Integer> orderLCV(int[][] board, Cell cell, List<Integer> domain) {
        Map<Integer, Integer> constraintCount = new HashMap<>();
        for (int val : domain) {
            int count = 0;
            for (int i = 0; i < 9; i++) {
                if (board[i][cell.col] == 0 && getLegalValues(board, i, cell.col).contains(val)) 
                    count++;
                if (board[cell.row][i] == 0 && getLegalValues(board, cell.row, i).contains(val)) 
                    count++;
            }
            int boxRowStart = 3 * (cell.row / 3);
            int boxColStart = 3 * (cell.col / 3);
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++) {
                    int rr = boxRowStart + r;
                    int cc = boxColStart + c;
                    if (board[rr][cc] == 0 && getLegalValues(board, rr, cc).contains(val)) 
                        count++;
                }
            constraintCount.put(val, count);
        }
        List<Integer> sorted = new ArrayList<>(domain);
        sorted.sort(Comparator.comparingInt(constraintCount::get));
        return sorted;
    }

    private Cell selectMRV(Map<Cell, List<Integer>> domains) {
        int min = 10;
        Cell best = null;
        for (Map.Entry<Cell, List<Integer>> entry : domains.entrySet()) {
            int size = entry.getValue().size();
            if (size < min) {
                min = size;
                best = entry.getKey();
            }
        }
        return best;
    }

    private Map<Cell, List<Integer>> initializeDomains(int[][] board) {
        Map<Cell, List<Integer>> domains = new HashMap<>();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    domains.put(new Cell(r, c), getLegalValues(board, r, c));
        return domains;
    }

    private Map<Cell, List<Integer>> deepCopy(Map<Cell, List<Integer>> original) {
        Map<Cell, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<Cell, List<Integer>> entry : original.entrySet())
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        return copy;
    }

    private List<Integer> getLegalValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[10];
        for (int i = 0; i < 9; i++) {
            used[board[row][i]] = true;
            used[board[i][col]] = true;
            used[board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3]] = true;
        }
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= 9; i++)
            if (!used[i]) values.add(i);
        return values;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
            int boxRow = 3 * (row / 3) + i / 3;
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == num) return false;
        }
        return true;
    }
}
