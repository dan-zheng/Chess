package edu.xwei12.chess.gui;

import org.junit.After;
import org.junit.Before;

/**
 * Test GUI
 * @author Xinran Wei
 */
public class GUITest {

    AppController controller;

//    @BeforeClass
//    public static void setUpClass() throws InterruptedException {
//        Thread t = new Thread("JavaFX_init") {
//            public void run() {
//                AppController.launch(AppController.class);
//            }
//        };
//        t.setDaemon(true);
//        t.start();
//        Thread.sleep(500);
//    }

    @Before
    public void setUp() throws Exception {
        controller = new AppController();
    }

    @After
    public void tearDown() throws Exception {

    }

    /*
    @Test
    public void testStart() throws Exception {
        Assert.assertTrue(validateSnapshot(controller.snapshot(), "start-snapshot.png"));
    }

    private boolean validateSnapshot(Image snapshot, String referenceFilename) {
        Image reference = new Image(getClass().getResourceAsStream("resources/snapshots/" + referenceFilename));
        PixelReader refPixels = reference.getPixelReader();
        PixelReader shotPixels = snapshot.getPixelReader();
        for (int i = 0; i < reference.getWidth(); i++)
            for (int j = 0; j < reference.getHeight(); j++)
                if (!refPixels.getColor(i, j).equals(shotPixels.getColor(i, j))) return false;
        return true;
    }

    private void generateSnapshot() throws IOException {
        Image snapshot = controller.snapshot();
        File file = new File("start-snapshot.png");
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
    }
    */
}