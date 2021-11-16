import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DisksApp extends JFrame {
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DisksApp app = new DisksApp();
				DisksPanel panel = new DisksPanel();
				app.getContentPane().add(panel, BorderLayout.CENTER);
				app.pack();
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				app.setVisible(true);
			}
		});
	}
}
