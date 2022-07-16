package swingJpg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

public class JpgFrame extends JFrame {

	JpgFileVisitor visitor = new JpgFileVisitor();
	Path path = Path.of("");

	JButton btnDownload = new JButton("Download");
	JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
	JTextField statusField = new JTextField();
	JLabel picture = new JLabel();
	JTextField searchField = new JTextField();
	JLabel lblSearch = new JLabel("Enter the path you want to visit:");

	public JpgFrame() throws IOException {
		setLayout();
		setFunctionality();
	}

	public void setFunctionality() throws IOException {
		btnDownload.addActionListener((e) -> {
			String searchString = searchField.getText();
			Path searchPath = Path.of(searchString);
			if (!searchPath.toFile().exists()) {
				statusField.setText("You've entered the wrong path");
			} else {
				reset();
				path = searchPath;
				JpgWorker worker = new JpgWorker();
				btnDownload.setEnabled(false);
				worker.execute();
			}
		});
	}

	public void reset() {
		progressBar.setValue(0);
		statusField.setText("");
	}

	public void setLayout() {
		// North panel
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));
		searchField.setColumns(30);
		northPanel.add(lblSearch);
		northPanel.add(searchField);
		northPanel.add(btnDownload);
		add(northPanel, BorderLayout.NORTH);

		// South panel
		JPanel southPanel = new JPanel(new BorderLayout());
		statusField.setEditable(false);
		progressBar.setStringPainted(true);
		southPanel.add(progressBar, BorderLayout.NORTH);
		southPanel.add(statusField, BorderLayout.SOUTH);
		add(southPanel, BorderLayout.SOUTH);

		// Center
		picture.setPreferredSize(new Dimension(600, 340));
		JPanel midPanel = new JPanel();
		midPanel.add(picture);
		add(midPanel, BorderLayout.CENTER);
	}

	class JpgWorker extends SwingWorker<String, String> {
		int counter = 0;
		int size = 0;

		@Override
		protected String doInBackground() throws Exception {
			Files.walkFileTree(path, visitor);
			Map<Path, String> data = visitor.getData();
			size = data.keySet().size();

			progressBar.setMaximum(size);

			for (Path p : data.keySet()) {
				ImageIcon icon = loadImage(p);
				picture.setIcon(icon);
				publish(data.get(p));
			}
			return "Download completed!";
		}

		@Override
		protected void process(List<String> chunks) {
			for (String s : chunks) {
				statusField.setText(s);
				progressBar.setValue(++counter);
			}
		}

		@Override
		protected void done() {
			try {
				statusField.setText(this.get());
			} catch (InterruptedException | ExecutionException e) {
				System.err.println("Something went wrong");
			}
			progressBar.setValue(0);
			btnDownload.setEnabled(true);
		}

	}

	public ImageIcon loadImage(Path p) throws InterruptedException {
		ImageIcon icon = new ImageIcon(p.toString());
		Image image = icon.getImage().getScaledInstance(600, 340, Image.SCALE_DEFAULT);
		Thread.sleep(2500);
		return new ImageIcon(image);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JpgFrame frame;
			try {
				frame = new JpgFrame();
				frame.setTitle("JPG Loader");
				frame.setLocation(150, 50);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setSize(720, 500);
				frame.setVisible(true);
			} catch (IOException e) {
				System.err.println("Something went wrong");
			}

		});
	}
}
