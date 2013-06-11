import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Window extends JFrame {


    CoffeeMakerAnimation animation;
    CoffeeMakerAnimation.coffee coffee;
    ArrayList<String> messages = new ArrayList<String>();

    JLabel labels;
    JPanel coffeePanel;

    public Window() throws HeadlessException {
        super();


        Container pane = getContentPane();

        final Box main = Box.createHorizontalBox();
        pane.add(main);

        Box buttons = Box.createVerticalBox();
        main.add(buttons);

        final JComboBox coffees = new JComboBox(CoffeeMakerAnimation.coffee.values());
        buttons.add(coffees);


        JButton newCoffee = new JButton("New Coffee");

        newCoffee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                animation = new CoffeeMakerAnimation((CoffeeMakerAnimation.coffee) coffees.getSelectedItem()) {
                    @Override
                    void onTick() {
                        updateUI();
                    }
                };
                animation.startAnimation();
            }
        });
        buttons.add(newCoffee);

        buttons.add(Box.createVerticalGlue());

        Box left = Box.createVerticalBox();
        main.add(left);

        coffeePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {

                super.paintComponent(g);


                if (animation != null) {


                    int width = getWidth();
                    int height = getHeight();


                    int milkEnd = (int) (animation.milkAmount * height);


                    int coffeeEnd =  (int) ((animation.coffeeAmount + animation.waterAmount) * height);

                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, width, milkEnd);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, milkEnd, width, coffeeEnd);


                }
                //To change body of overridden methods use File | Settings | File Templates.
            }
        };
        coffeePanel.setPreferredSize(new Dimension(100, 400));
        left.add(coffeePanel);


        labels = new JLabel("");
        labels.setPreferredSize(new Dimension(400, 100));
        left.add(labels);

        left.add(new Label("$$$$$$$$$$$$$$$"));
    }

    private void updateUI() {

        coffeePanel.getParent().repaint();
    }


    public static void main(String[] args) {


        Window w = new Window();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.pack();
        w.setVisible(true);

    }
}
