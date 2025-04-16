import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreams extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path filePath;

    public DataStreams() {
        setTitle("Java Data Stream Filter");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text areas
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);

        JScrollPane scrollPaneLeft = new JScrollPane(originalTextArea);
        JScrollPane scrollPaneRight = new JScrollPane(filteredTextArea);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(scrollPaneLeft);
        centerPanel.add(scrollPaneRight);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        bottomPanel.add(new JLabel("Search:"));
        bottomPanel.add(searchField);
        bottomPanel.add(loadButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(quitButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePath = chooser.getSelectedFile().toPath();
            try (Stream<String> lines = Files.lines(filePath)) {
                String content = lines.collect(Collectors.joining("\n"));
                originalTextArea.setText(content);
                filteredTextArea.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private void searchFile(ActionEvent e) {
        if (filePath == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first.");
            return;
        }
        String search = searchField.getText().toLowerCase();
        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> filtered = lines
                    .filter(line -> line.toLowerCase().contains(" " + search+ " "))
                    .collect(Collectors.toList());
            if (filtered.isEmpty()){
                filteredTextArea.setText(String.join("\n","THIS DOESN'T EXIST IN THE TEXT FILE"));
                return;
            }
            filteredTextArea.setText(String.join("\n\n", filtered));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error searching file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataStreams().setVisible(true));
    }
}
