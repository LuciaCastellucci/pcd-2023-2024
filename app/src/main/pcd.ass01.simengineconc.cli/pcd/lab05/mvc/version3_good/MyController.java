package pcd.lab05.mvc.version3_good;


public class MyController {
	
	private MyModel model;
	public MyController(MyModel model){
		this.model = model;
	}

	public void processEvent(String event) {
		// Questa versione è solo a scopo scolastico.
		// La procedura corretta prevederebbe di individuare un eventuale componente attivo per modellarlo ad hoc
		try {
			new Thread(() -> {
				try {
					System.out.println("[Controller] Processing the event "+event+" ...");
					Thread.sleep(1000);
					model.update();
					System.out.println("[Controller] Processing the event done.");
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}).start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}