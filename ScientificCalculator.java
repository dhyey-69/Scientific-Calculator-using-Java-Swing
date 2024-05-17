import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator implements ActionListener {
    JFrame f;
    JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b0, badd, bsub, bmul, bdiv, beq, bdel, bdec, bclr;
    JButton bsin, bcos, btan, blog, bln, bexp, bsqrt, bpow, bfac;
    JTextField t;

    ScientificCalculator() {
        f = new JFrame("Scientific Calculator");
        t = new JTextField();
    
        Font f0 = new Font("Arial", Font.BOLD, 20);
        f.setSize(350, 400);
        f.setLayout(new GridBagLayout());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
    
        t.setFont(f0);
        t.setBackground(Color.WHITE);
        t.setForeground(Color.BLACK);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setEditable(false);
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 20;
        f.add(t, gbc);
    
        // Initialize buttons
        b1 = createButton("1");
        b2 = createButton("2");
        b3 = createButton("3");
        b4 = createButton("4");
        b5 = createButton("5");
        b6 = createButton("6");
        b7 = createButton("7");
        b8 = createButton("8");
        b9 = createButton("9");
        b0 = createButton("0");
        badd = createButton("+");
        bsub = createButton("-");
        bmul = createButton("*");
        bdiv = createButton("/");
        beq = createButton("=");
        bdel = createButton("Del");
        bdec = createButton(".");
        bclr = createButton("C");
        bsin = createButton("sin");
        bcos = createButton("cos");
        btan = createButton("tan");
        blog = createButton("log");
        bln = createButton("ln");
        bexp = createButton("exp");
        bsqrt = createButton("√");
        bpow = createButton("^");
        bfac = createButton("!");
    
        // Arrange buttons
        gbc.gridwidth = 1;
        gbc.ipady = 0;
        JButton[] buttons = {b7, b8, b9, bdiv, bsin,
                             b4, b5, b6, bmul, bcos,
                             b1, b2, b3, bsub, btan,
                             bdec, b0, beq, badd, blog,
                             bln, bexp, bsqrt, bpow, bfac};
    
        int gridx = 0, gridy = 1;
        for (JButton button : buttons) {
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            f.add(button, gbc);
            gridx++;
            if (gridx == 5) {
                gridx = 0;
                gridy++;
            }
        }
    
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = gridy;
        f.add(bdel, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 3;
        f.add(bclr, gbc);
    
        f.setVisible(true);
    }
    

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        return button;
    }

    public static void main(String[] args) {
        new ScientificCalculator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.charAt(0) == 'C') {
            t.setText("");
        } else if (command.charAt(0) == '=') {
            t.setText("" + evaluate(t.getText()));
        } else if (command.equals("Del")) {
            String text = t.getText();
            if (text.length() > 0) {
                t.setText(text.substring(0, text.length() - 1));
            }
        } else {
            t.setText(t.getText() + command);
        }
    }

    public static double evaluate(String expression) {
        try {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (; ; ) {
                        if (eat('+')) x += parseTerm(); // addition
                        else if (eat('-')) x -= parseTerm(); // subtraction
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (; ; ) {
                        if (eat('*')) x *= parseFactor(); // multiplication
                        else if (eat('/')) x /= parseFactor(); // division
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor(); // unary plus
                    if (eat('-')) return -parseFactor(); // unary minus

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) { // parentheses
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') { // functions
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = expression.substring(startPos, this.pos);
                        x = parseFactor();
                        if (func.equals("sqrt")) x = Math.sqrt(x);
                        else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                        else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                        else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                        else if (func.equals("log")) x = Math.log10(x);
                        else if (func.equals("ln")) x = Math.log(x);
                        else if (func.equals("exp")) x = Math.exp(x);
                        else if (func.equals("!")) x = factorial(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                    return x;
                }

                double factorial(double n) {
                    if (n == 0 || n == 1) return 1;
                    return n * factorial(n - 1);
                }
            }.parse();
        } catch (Exception e) {
            return 0;
        }
    }
}
