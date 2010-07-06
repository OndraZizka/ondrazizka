
package cz.dynawest.util;

/**
 *
 * @author Ondřej Žižka
 */
public class IteratorStringBuilder {

	public static String create( Iterable iterable, Stringer stringer ){

		if( null == iterable || !iterable.iterator().hasNext() )
			return "";

		String delimiter = ", ";
		StringBuilder sb = new StringBuilder();
		for( Object item : iterable ){
			sb.append( delimiter );
			sb.append( stringer.makeString( item ) );
		}
		String res = sb.toString().substring(2);
		return res;
	}

	public interface Stringer {

		public String makeString( Object toBeStringed );

		public static final Stringer className = new IteratorStringBuilder.Stringer() {
			public String makeString( Object toBeStringed ) {	return toBeStringed.getClass().getName();	}
		};

	}



}// class IteratorStringBuilder
