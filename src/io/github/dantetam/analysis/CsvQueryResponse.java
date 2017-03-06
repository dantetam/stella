package io.github.dantetam.analysis;

import java.util.List;

import io.github.dantetam.data.CsvAssociation;

/*
 * This is a generic class for an extendable query object which
 * for an input of class T and an output of class R,
 * with some response data processing function f: T -> R,
 * extends CsvQuery<T,R> and feedResponse(CsvQueryResponse<R>, ...)
 */

public abstract class CsvQueryResponse<T,R> {
	
	public CsvQueryResponse() {
		
	}
	
	public abstract void feedResource(T input, CsvAssociation association);
	
	public R singleResponseComputation() {return null;}
	
	public List<R> listResponseComputation() {return null;}
	
}
