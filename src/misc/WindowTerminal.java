package com.almasb.java.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.almasb.common.parsing.InputParser;
import com.almasb.common.util.Out;
import com.almasb.java.io.Logger;

/**
 * Somewhat an emulator of standard terminal on any BasicWindow
 * with extended functionality for interrogating state of the current program
 *
 * @author Almas
 * @version 1.5
 *
 *          v 1.1 - supports general type applications
 *          v 1.2 - completely rewrote base, now natively works with javax.swing
 *          v 1.3 - significant changes to isolate each package within the library,
 *                  now UI package is independent (except for main)
 *          v 1.4 - fixed all problems with drawing/compatibility
 *                  added menu
 *          v 1.5 - using proper GroupLayout as opposed to manual layout,
 *                  better class design
 */
@SuppressWarnings("serial")
public class WindowTerminal extends JFrame {

    public static final int BUFFER_SIZE = 3;

    /**
     * Default font used
     */
    protected static final String DEFAULT_TERMINAL_FONT = "Courier";

    /**
     * Default key used to open the window terminal
     */
    protected static final int DEFAULT_TERMINAL_KEY = KeyEvent.VK_HOME;

    /**
     * Holds the singleton instance of WindowTerminal
     */
    private static WindowTerminal instance = new WindowTerminal();

    private ArrayList<String> cmdBuffer = new ArrayList<String>(BUFFER_SIZE);
    private int cmdBufferIndex = 0;

    private JTextField cmdLine = new JTextField();
    private JTextPane cmdLog = new JTextPane();
    private AbstractDocument cmdLogDoc = (AbstractDocument) cmdLog.getStyledDocument();
    private JScrollPane cmdLogScrollPane = new JScrollPane(cmdLog);

    private SimpleAttributeSet cmdLogTextAttr = new SimpleAttributeSet();

    private WindowTerminalPrintStream windowOut = new WindowTerminalPrintStream();
    private InputParser inputParser = null;

    public static WindowTerminal getInstance() {
        return instance;
    }

    /**
     * private ctor
     */
    private WindowTerminal() {
        super("Window Terminal v 1.5");
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setMinimumSize(new Dimension(300, 400));
        this.setJMenuBar(createMenuBar());

        initLayout();
        initContent();
        pack();

        // divert all print calls from Out to the window terminal
        Out.setPrintStream(windowOut);
        Out.i("WindowTerminal", "initialization complete: " + LocalTime.now().toString());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Console");
        menuBar.add(menu);

        JMenuItem item = new JMenuItem("Save Log");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] text = cmdLog.getText().split("\n");
                ArrayList<String> content = new ArrayList<String>();

                Collections.addAll(content, text);

                Logger.createLog("WindowTerminal", content);
            }
        });
        menu.add(item);

        menu.addSeparator();

        JMenuItem itemClose = new JMenuItem("Close");
        itemClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        menu.add(itemClose);

        return menuBar;
    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(cmdLogScrollPane).addComponent(cmdLine));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(cmdLogScrollPane).addComponent(cmdLine, 20, 20, 20));
    }

    private void initContent() {
        StyleConstants.setFontFamily(cmdLogTextAttr, DEFAULT_TERMINAL_FONT);

        cmdLine.requestFocusInWindow();
        cmdLog.setFocusable(false);
        cmdLog.setEditable(false);

        cmdLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String line = cmdLine.getText();
                if (cmdBuffer.size() >= BUFFER_SIZE) {
                    cmdBuffer.remove(0);
                }

                if (!line.isEmpty()) {
                    cmdBuffer.add(line);
                    cmdBufferIndex = cmdBuffer.size();
                }

                Out.println(line);
                if (inputParser != null)
                    inputParser.parse(line);

                cmdLine.setText("");
            }
        });

        cmdLine.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == DEFAULT_TERMINAL_KEY) {
                    setVisible(false);
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (cmdBufferIndex > 0) {
                        cmdBufferIndex--;
                        cmdLine.setText(cmdBuffer.get(cmdBufferIndex));
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (cmdBufferIndex < cmdBuffer.size()-1) {
                        cmdBufferIndex++;
                        cmdLine.setText(cmdBuffer.get(cmdBufferIndex));
                    }
                }
            }
        });
    }

    public PrintStream getPrintStream() {
        return windowOut;
    }

    private class WindowTerminalPrintStream extends PrintStream {

        private Color DARK_GREEN = new Color(0, 130, 0);

        public WindowTerminalPrintStream() {
            super(new ByteArrayOutputStream(), true);
        }

        @Override
        public void print(String text) {
            setVisible(true);
            if (text.startsWith("E:")) {
                print(text, Color.RED);
            }
            else if (text.startsWith("D:")) {
                print(text, DARK_GREEN);
            }
            else if (text.startsWith("I:")) {
                print(text, Color.BLUE);
            }
            else if (text.startsWith("C: ")) {
                clear();
            }
            else {
                print(text, Color.BLACK);
            }
        }

        @Override
        public void println(String text) {
            print(text + "\n");
        }

        public void print(String text, Color color) {
            StyleConstants.setForeground(cmdLogTextAttr, color);
            try {
                cmdLogDoc.insertString(cmdLogDoc.getLength(), text, cmdLogTextAttr);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cmdLogScrollPane.getVerticalScrollBar().setValue(cmdLogScrollPane.getVerticalScrollBar().getMaximum());
                    }
                });
            }
            catch (BadLocationException e) {
                Out.e("print()", "Couldn't insert string", this, e);
            }
        }

        public void clear() {
            try {
                cmdLogDoc.remove(0, cmdLogDoc.getLength());
            }
            catch (BadLocationException e) {
                Out.e("clear()", "Couldn't clear document", this, e);
            }
        }
    }

    public void setInputParser(InputParser parser) {
        inputParser = parser;
    }
}
