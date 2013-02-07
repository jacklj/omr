package omr.symbol_recogntion.stave_detection;

public class StaveSkeletonWHYdidntitwork {
	
	
	public static void main(String[] args) {
		
	}
		
	
	
	
	
	private final int width;
	
//	private int[] staveLine1;
//	private int[] staveLine2;
//	private int[] staveLine3;
//	private int[] staveLine4;
//	private int[] staveLine5;
	
	//private int[][] stave = {staveLine1, staveLine2, staveLine3, staveLine4, staveLine5};
	private int[][] stave;
	
	public StaveSkeletonWHYdidntitwork(int stave_width) {
		this.width = stave_width;
//		staveLine1 = new int[width]; // automatically fills with 0's
//		staveLine2 = new int[width];
//		staveLine3 = new int[width];
//		staveLine4 = new int[width];
//		staveLine5 = new int[width];
		
		stave = new int[5][width];
	}
	
	public void setStaveLinePoint(int staveLineNumber, int x_column, int y_staveLineRunCentre) {
//		 switch (staveLineNumber) {
//         case 1:  staveLine1[x_column] = y_staveLineRunCentre;
//                  break;
//         case 2:  staveLine2[x_column] = y_staveLineRunCentre;
//         		  break;
//         case 3:  staveLine3[x_column] = y_staveLineRunCentre;
//         		  break;
//         case 4:  staveLine4[x_column] = y_staveLineRunCentre;
//		  		  break;
//         case 5:  staveLine5[x_column] = y_staveLineRunCentre;
//         		  break;
//         default: System.out.println("[ERROR] setStaveLinePoint (int staveLineNumber, int x_column, "
//        		 + "int y_staveLineRunCentre)  staveLineNumber must be between 1 and 5 innclusive");
//		 }
		stave[staveLineNumber - 1][x_column] = y_staveLineRunCentre;
	}
	
	public int getStaveLinePoint(int staveLineNumber, int x_column) {
		return stave[staveLineNumber - 1][x_column];
	}
}
