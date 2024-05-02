package pcd.lab08.rx;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Test03a_sched_subscribeon {

	public static void main(String[] args) throws Exception {

		System.out.println("\n=== TEST No schedulers ===\n");
		
		/*
		 * Without using schedulers, by default all the computation 
		 * is done by the calling thread.
		 * 
		 */
		Observable.just(100)	
			.map(v -> { log("map 1 " + v); return v * v; })
			.map(v -> { log("map 2 " + v); return v + 1; })
			.subscribe(v -> {						
				log("sub " + v);
			});
		
		System.out.println("\n=== TEST subscribeOn ===\n");

		/* 
		 * subscribeOn:
		 * 
		 * move the computational work of a flow on a specified scheduler
		 */
		Observable<Integer> src = Observable.just(100)	
			.map(v -> { log("map 1 " + v); return v * v; })		
			.map(v -> { log("map 2 " + v); return v + 1; });		

		src
			.subscribeOn(Schedulers.computation()) 	
			.subscribe(v -> {									
				log("sub 1 " + v);
			});

		src
			.subscribeOn(Schedulers.computation()) 	
			.subscribe(v -> {									
				log("sub 2 " + v);
			});

		Thread.sleep(100);
		
		System.out.println("\n=== TEST parallelism  ===\n");

		/* 
		 * Running independent flows on a different scheduler 
		 * and merging their results back into a single flow 
		 * warning: flatMap => no order in merging
		 */

		/*
		* Parametro della flatmap: lamdba che dato un valore resituisce un flusso che è dato da:
		* 	una concatenazione per cui abbiamo un valore e una computazione da eseguire su un pool di thread
		* Vengono dunque generati 1000 flussi ognuno col suo pull di thread e poi con flatMap tutti i flussi
		* 	vengono mergiati nello stesso flusso
		* Il risultato della flatmap è un flusso di interi ognuno dei quali contiene il quadrato
		* 	di un elemento dei flussi originali: in parallelo vengono eseguiti 1000 elevamenti al quadrato
		* 	che vengono poi inseiriti nello stesso flusso, senza alcuna garanzia in merito all'ordinamento
		* Con blockingSubscribe voglio osservare i valori sequenzialmente con il thread chiamante
		*/
		Flowable.range(1, 1000)
		  .flatMap(v ->
		      Flowable.just(v)
		        .subscribeOn(Schedulers.computation()) //viene fatto getire ad un pool di thread diversi
				.map(w -> { log("map " + w); return w * w; })		// by the RX comp thread;
		  )
		  .blockingSubscribe(v -> {
			 log("sub > " + v); 
		  });

		//Viene tutto eseguito dal main
		Flowable.range(1, 1000)
			.flatMap(v ->
				Flowable.just(v)
					//.subscribeOn(Schedulers.computation())
					.map(w -> { log("map " + w); return w * w; })		// by the RX comp thread;
			)
			.subscribe(v -> {
				log("sub > " + v);
			});

		//La computazione viene eseguita da un thread diverso ma la subscribe viene eseguita dal main
		Flowable.range(1, 1000)
			.flatMap(v ->
				Flowable.just(v)
					.map(w -> { log("map " + w); return w * w; })		// by the RX comp thread;
			).subscribeOn(Schedulers.computation()) //viene fatto getire ad un pool di thread diversi
			.blockingSubscribe(v -> {
				log("sub > " + v);
			});

		
	}
		
	static private void log(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
	}
	
}
