package omr.symbol_recogntion;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TestGUI extends JPanel{

    private BufferedImage image;

    public TestGUI() {
       try {                
          image = ImageIO.read(new File("//Users//buster//Stuff//Academia//II//DISSERTATION//test_images//2notes.png"));
       } catch (IOException ex) {
            // handle exception...
       }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters

    }

}


