import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TextEditor extends JFrame implements ActionListener {


    static JTextArea textArea = new JTextArea();
    static JScrollPane scrollPane = new JScrollPane(textArea);
    static JSpinner fontSizeSpinner = new JSpinner();
    static JLabel fontLabel = new JLabel("Font: ");
    static JButton fontColorButton = new JButton("Color");
    static JButton clearBtn = new JButton("Clear");
    static JButton exitBtn = new JButton("Exit");
    static JComboBox fontBox;
    static JMenuBar menuBar = new JMenuBar();
    static JMenu fileMenu = new JMenu("File");
    static JMenuItem openItem = new JMenuItem("Open");
    static JMenuItem saveItem = new JMenuItem("Save");
    static JCheckBox bold = new JCheckBox("bold");
    static JCheckBox italic = new JCheckBox("italic");
    static JLabel chars = new JLabel("Chars: 0");
    static JLabel chars2 = new JLabel("Words: 0");
    static JLabel chars3 = new JLabel("Cursor: 0");
    static JLabel chars4 = new JLabel("Selected: 0");


    TextEditor(){

        Toolkit tlk=Toolkit.getDefaultToolkit();
        Dimension dms=tlk.getScreenSize();
        int w=1000, h=600;
        this.setBounds(dms.width/2-w/2, dms.height/2-h/2, w, h);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Text Editor");
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Times New Roman",Font.PLAIN,20));
        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                chars3.setText("Cursor: " + e.getDot());
                chars4.setText("Selected: " + Math.abs(e.getDot()-e.getMark()));
            }
        });
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                chars.setText("Chars: " + textArea.getText().toString().length());
                if (textArea.getText().toString().length() == 0){
                    chars2.setText("Words: 0");
                } else
                {
                    chars2.setText("Words: " + (textArea.getText().toString().trim().replaceAll("\\s+"," ").chars().filter(ch -> ch == ' ').count()+1));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        scrollPane.setPreferredSize(new Dimension(970,470));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        fontSizeSpinner.setPreferredSize(new Dimension(50,25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN, (Integer) fontSizeSpinner.getValue()));
            }
        });


        fontColorButton.addActionListener(this);
        clearBtn.addActionListener(this);
        exitBtn.addActionListener(this);

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        fontBox = new JComboBox(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Times New Roman");

        bold.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    textArea.setFont(new Font(textArea.getFont().getFamily(),Font.BOLD,(Integer) fontSizeSpinner.getValue()));
                 } else {
                    textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(Integer) fontSizeSpinner.getValue()));
                 }
            }
        });

        italic.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    textArea.setFont(new Font(textArea.getFont().getFamily(),Font.ITALIC,(Integer) fontSizeSpinner.getValue()));
                } else {
                    textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(Integer) fontSizeSpinner.getValue()));
                }
            }
        });

        // ------ menu bar ------
        openItem.addActionListener(this);
        saveItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        // ------ menu bar ------

        this.setJMenuBar(menuBar);
        this.add(fontBox);
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(bold);
        this.add(italic);
        this.add(fontColorButton);
        this.add(clearBtn);
        this.add(exitBtn);
        this.add(scrollPane);
        this.add(chars);
        this.add(chars2);
        this.add(chars3);
        this.add(chars4);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource() == clearBtn){
            textArea.setText("");
        }

        if(e.getSource() == fontColorButton){

            Color color = JColorChooser.showDialog(null,"Choose a color", Color.BLACK);

            textArea.setForeground(color);
        }

        if (e.getSource() == fontBox){
            textArea.setFont(new Font((String) fontBox.getSelectedItem(),Font.PLAIN,textArea.getFont().getSize()));
        }

        if (e.getSource() == openItem){

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files","txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

                try (Scanner fileIn = new Scanner(file)) {
                    if (file.isFile()) {
                        while (fileIn.hasNextLine()) {
                            String line = fileIn.nextLine() + "\n";
                            textArea.append(line);
                        }
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }

        }
        if (e.getSource() == saveItem){

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File file;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try (PrintWriter fileOut = new PrintWriter(file)) {
                    fileOut.println(textArea.getText());
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        }
        if (e.getSource() == exitBtn){
            System.exit(0);
        }
    }
}