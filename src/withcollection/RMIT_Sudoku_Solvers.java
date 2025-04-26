package withcollection;

import data_structure.MyEntry;
import data_structure.MyMap;
import data_structure.MyList;
import data_structure.MyStack;

public class RMIT_Sudoku_Solvers{
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
    }

    public int[][] solve(int[][] puzzle) throws Exception {
        startTime = System.nanoTime();

        MyMap<Cell, MyList<Integer>> domains = initializeDomains(puzzle);

        if (!backtrack(puzzle, domains, new MyStack<>())) {
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

    private boolean backtrack(int[][] board, MyMap<Cell, MyList<Integer>> domains, MyStack<MyEntry<Cell, Integer>> trail) {
        Cell cell = selectMRV(domains);

        if (cell == null)
            return true; // Solved

        MyList<Integer> values = orderLCV(board, cell, domains.get(cell));

        for (int i = 0; i < values.size(); i++) {
            int num = values.get(i);
            if (isValid(board, cell.row, cell.col, num)) {
                board[cell.row][cell.col] = num;
                steps++;

                MyMap<Cell, MyList<Integer>> backup = deepCopy(domains);

                if (forwardCheck(domains, cell, num)) {
                    if (backtrack(board, domains, trail))
                        return true;
                }

                board[cell.row][cell.col] = 0; // Undo
                domains.clear();
                domains.putAll(backup);
            }
        }
        return false;
    }

    private boolean forwardCheck(MyMap<Cell, MyList<Integer>> domains, Cell current, int num) {
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

    private boolean eliminate(MyMap<Cell, MyList<Integer>> domains, Cell cell, int num) {
        if (domains.containsKey(cell)) {
            MyList<Integer> domain = domains.get(cell);
            domain.remove(num);
            if (domain.size() == 0)
                return false;
        }
        return true;
    }

    private MyList<Integer> orderLCV(int[][] board, Cell cell, MyList<Integer> domain) {
        MyMap<Integer, Integer> constraintCount = new MyMap<>();

        for (int i = 0; i < domain.size(); i++) {
            int val = domain.get(i);
            int count = 0;

            for (int j = 0; j < 9; j++) {
                if (board[j][cell.col] == 0 && getLegalValues(board, j, cell.col).contains(val))
                    count++;
                if (board[cell.row][j] == 0 && getLegalValues(board, cell.row, j).contains(val))
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

        MyList<Integer> sorted = new MyList<>();
        for (int i = 0; i < domain.size(); i++) {
            sorted.add(domain.get(i));
        }

        sorted.sort((a, b) -> constraintCount.get(a) - constraintCount.get(b));
        return sorted;
    }

    private Cell selectMRV(MyMap<Cell, MyList<Integer>> domains) {
        int min = 10;
        Cell best = null;

        MyList<MyEntry<Cell, MyList<Integer>>> entries = domains.entrySet();
        for (int i = 0; i < entries.size(); i++) {
            MyEntry<Cell, MyList<Integer>> entry = entries.get(i);
            int size = entry.value.size();
            if (size < min) {
                min = size;
                best = entry.key;
            }
        }
        return best;
    }

    private MyMap<Cell, MyList<Integer>> initializeDomains(int[][] board) {
        MyMap<Cell, MyList<Integer>> domains = new MyMap<>();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] == 0)
                    domains.put(new Cell(r, c), getLegalValues(board, r, c));
        return domains;
    }

    private MyMap<Cell, MyList<Integer>> deepCopy(MyMap<Cell, MyList<Integer>> original) {
        MyMap<Cell, MyList<Integer>> copy = new MyMap<>();
        MyList<MyEntry<Cell, MyList<Integer>>> entries = original.entrySet();
        for (int i = 0; i < entries.size(); i++) {
            MyEntry<Cell, MyList<Integer>> entry = entries.get(i);
            MyList<Integer> newList = new MyList<>();
            for (int j = 0; j < entry.value.size(); j++) {
                newList.add(entry.value.get(j));
            }
            copy.put(entry.key, newList);
        }
        return copy;
    }

    private MyList<Integer> getLegalValues(int[][] board, int row, int col) {
        boolean[] used = new boolean[10];
        for (int i = 0; i < 9; i++) {
            used[board[row][i]] = true;
            used[board[i][col]] = true;
            used[board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3]] = true;
        }

        MyList<Integer> values = new MyList<>();
        for (int i = 1; i <= 9; i++) {
            if (!used[i])
                values.add(i);
        }
        return values;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
            int boxRow = 3 * (row / 3) + i / 3;
            int boxCol = 3 * (col / 3) + i % 3;
            if (board[boxRow][boxCol] == num)
                return false;
        }
        return true;
    }
}
