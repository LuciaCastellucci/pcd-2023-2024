package pcd.lab08.rx;

import java.util.Arrays;

import io.reactivex.rxjava3.core.*;

public class Test01_basic {

	public static void main(String[] args){

		// In questa versione base l'esecuzione della lambda con cui osserviamo mano a mano
		// che arrivano gli elementi del flusso è fatta dal flusso di controllo chiamante
		// subscribe è dunque chiamata dal main
				
		log("creating with just.");
		
	    Observable.just("Hello world - obs").subscribe(s -> {
	    		log(s);    		
	    });
	    
	    // with inline method
	    
	    Flowable.just("Hello world - flw")
	    	.subscribe(System.out::println);
	    
		// creating a flow (an observable stream) from a static collection

	    // simple subscription 
	    
		String[] words = { "Hello", " ", "World", "!" }; 
		
		Flowable.fromArray(words)
			.subscribe((String s) -> {
				log(s);
			});
		
		// full subscription: onNext(), onError(), onCompleted()
		
		log("Full subscription...");
		
		Observable.fromArray(words)
			.subscribe((String s) -> {
				log("> " + s);
			},(Throwable t) -> {
				// cosa viene eseguito quando si genera un errore
				log("error  " + t);
			},() -> {
				// cosa viene eseguito quando il flusso termina
				// in questo caso, in cui il flusso parte da un array
				log("completed");
			});
		
		// operators

		log("simple application of operators");
		
		Flowable<Integer> flow = Flowable.range(1, 20)
			.map(v -> v * v)
			.filter(v -> v % 3 == 0);
		
		log("first subscription #1");
		flow.subscribe(System.out::println);

		log("first subscription #2");
		flow.subscribe((v) -> {
			log("" + v);
		});

		// doOnNext for debugging...
		
		log("showing the flow...");
		
		Flowable.range(1, 20)
			.doOnNext(v -> log("1> " + v))
			.map(v -> v * v)
			.doOnNext(v -> log("2> " + v))
			.filter(v -> v % 3 == 0)
			.doOnNext(v -> log("3> " + v))
			.subscribe(System.out::println);
						
		
		// simple composition
		
		log("simple composition");
		
		Observable<String> src1 = Observable.fromIterable(Arrays.asList(
				 "the",
				 "quick",
				 "brown",
				 "fox",
				 "jumped",
				 "over",
				 "the",
				 "lazy",
				 "dog"
				));

		Observable<Integer> src2 = Observable.range(1, 5);
		
		src1
			.zipWith(src2, (string, count) -> String.format("%2d. %s", count, string))
			.subscribe(System.out::println);
		
	}
	
	private static void log(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
	}
}
