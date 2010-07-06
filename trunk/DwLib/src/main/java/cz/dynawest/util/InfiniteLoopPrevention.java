
package cz.dynawest.util;

/**
 *
 * @author Ondřej Žižka
 */
public class InfiniteLoopPrevention {

	private long maxLoops;

	public InfiniteLoopPrevention( long maxLoops ) {
		this.maxLoops = maxLoops;
	}

	public void doStep() throws LimitReached {
		if( this.maxLoops <= 0 )
			throw new LimitReached("Reached the limit of maximum loops: "+this.maxLoops+"." +
							" This means you probably programmed an infinite loop or it had inexpectedly big number of iterations.");
		this.maxLoops--;
	}




	// ---

	public class LimitReached extends RuntimeException {

		public LimitReached( String message ) {
			super( message );
		}
		
	}


}// class InfiniteLoopPrevention
