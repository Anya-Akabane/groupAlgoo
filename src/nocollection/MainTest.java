// package nocollection;

// public class MainTest {
//     public static void main(String[] args) {
//        int[][] puzzle = {
//             {0, 0, 0, 2, 6, 0, 7, 0, 1},
//             {6, 8, 0, 0, 7, 0, 0, 9, 0},
//             {1, 9, 0, 0, 0, 4, 5, 0, 0},
//             {8, 2, 0, 1, 0, 0, 0, 4, 0},
//             {0, 0, 4, 6, 0, 2, 9, 0, 0},
//             {0, 5, 0, 0, 0, 3, 0, 2, 8},
//             {0, 0, 9, 3, 0, 0, 0, 7, 4},
//             {0, 4, 0, 0, 5, 0, 0, 3, 6},
//             {7, 0, 3, 0, 1, 8, 0, 0, 0}
//         };

//         RMIT_Sudoku_Solver_NoCollections solver = new RMIT_Sudoku_Solver_NoCollections();




//         try {

            
//             int[][] result = solver.solve(puzzle);

//             System.out.println("Solved Sudoku:");
//             for (int r = 0; r < 9; r++) {
//                 for (int c = 0; c < 9; c++) {
//                     System.out.print(result[r][c] + " ");
//                     if ((c + 1) % 3 == 0 && c != 8) {
//                         System.out.print("| ");
//                     }
//                 }
//                 System.out.println();
//                 if ((r + 1) % 3 == 0 && r != 8) {
//                     System.out.println("------+-------+------");
//                 }
//             }


//         } catch (Exception e) {
//             System.out.println("Failed to solve: " + e.getMessage());
//         }
//     }
// }
