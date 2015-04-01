package document.structure;

import java.util.function.Function;

public class SlopeFunctions {
	public Function<Integer, Integer> yInFunction;
	public Function<Integer, Integer> xInFunction;

	public SlopeFunctions(Function<Integer, Integer> yIn, Function<Integer, Integer> xIn) {
		yInFunction = yIn;
		xInFunction = xIn;
	}
	
	public int getXInputY(int y) {
		return yInFunction.apply(y);
	}
	public int getYInputX(int x) {
		return xInFunction.apply(x);
	}
	
	public static class SlopeFunctionsBuilder {
		private Function<Integer, Integer> yIn;
		private Function<Integer, Integer> xIn;
		
		public SlopeFunctionsBuilder setXInputYFunction(Function<Integer, Integer> func) {
			this.xIn = func;
			System.out.println("gg");
			return this;
		}
		public SlopeFunctionsBuilder setYInputXFunction(Function<Integer, Integer> func) {
			this.yIn = func;
			return this;
		}
		public SlopeFunctions build() {
			return new SlopeFunctions(yIn, xIn);
		}
	}
	
}
