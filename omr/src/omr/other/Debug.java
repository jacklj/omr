package omr.other;

public class Debug {

		public static void debug(int debugLevel, String debugMessage) {
			String thisClassName = "NotationReconstructor_Simple";
			
			if(debugLevel <= this.debugLevel) {
				String indent = "";
				for(int i = debugLevel; i > 1; i++) {
					indent = indent + "\t";
				}
				
				System.out.println("DEBUG(" + thisClassName + ", l" + debugLevel + "): " + indent + debugMessage);
			}
		
	}
}
