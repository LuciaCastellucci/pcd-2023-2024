package pcd.ass02pt01.simengine_conc.cli;

import pcd.ass02pt01.simengine_conc.AbstractAgent;
import pcd.ass02pt01.simengine_conc.AbstractEnvironment;
import pcd.ass02pt01.simengine_conc.SimulationListener;
import pcd.ass02pt01.simtraffic_conc.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoadMassiveSimView extends JFrame implements SimulationListener {

	private RoadSimViewPanel panel;
	private static final int CAR_DRAW_SIZE = 2;
	
	public RoadMassiveSimView() {
		super("RoadSim View");
		setSize(1500,600);
			
		panel = new RoadSimViewPanel(1500,600); 
		panel.setSize(1500, 600);

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);
		cp.add(BorderLayout.CENTER,panel);
		setContentPane(cp);		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
			
	}
	
	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	@Override
	public void notifyInit(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStepDone(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		var e = ((RoadsEnv) env);
		panel.update(e.getRoads(), e.getCars(), e.getTrafficLights());
	}
	
	
	class RoadSimViewPanel extends JPanel {
		
		List<AbstractCar> cars;
		List<Road> roads;
		List<TrafficLight> sems;
		
		public RoadSimViewPanel(int w, int h){
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);   
	        Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.clearRect(0,0,this.getWidth(),this.getHeight());
			
			g.setColor(new Color(50, 50, 50, 255));

			if (roads != null) {
				for (var r: roads) {
					g2.drawLine((int)r.getFrom().x(), (int)r.getFrom().y(), (int)r.getTo().x(), (int)r.getTo().y());
				}
			}
			
			if (sems != null) {
				for (var s: sems) {
					if (s.isGreen()) {
						g.setColor(new Color(0, 255, 0, 255));
					} else if (s.isRed()) {
						g.setColor(new Color(255, 0, 0, 255));
					} else {
						g.setColor(new Color(255, 255, 0, 255));
					}
					g2.fillRect((int)(s.getPos().x()-5), (int)(s.getPos().y()-5), 10, 10);
				}
			}
			
			g.setColor(new Color(0, 0, 0, 255));

			double factor = 0.01;
			if (cars != null) {
				for (var c: cars) {
					double pos = c.getPos();
					Road r = c.getRoad();
					V2d dir = V2d.makeV2d(r.getFrom(), r.getTo()).getNormalized().mul(pos*factor);
					g2.drawOval((int)(r.getFrom().x() + dir.x() - CAR_DRAW_SIZE/2), (int)(r.getFrom().y() + dir.y() - CAR_DRAW_SIZE/2), CAR_DRAW_SIZE , CAR_DRAW_SIZE);
				}
			}
  	   }
	
	   public void update(List<Road> roads, List<AbstractCar> cars, List<TrafficLight> sems) {
		   this.roads = roads;
		   this.cars = cars;
		   this.sems = sems;
		   repaint();
	   }
	}
}
