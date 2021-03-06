package gestureinterpreter;

import java.util.Map;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

import javafx.application.Platform;
import javafx.scene.Group;

/**
 * Class handling hand tracking, extends the Leap Motion listener class.
 */
public class LeapListener extends Listener {
    private Group handGroup;
    private Map<Integer, HandFX> hands;
    private Menu app;

    /**
     * Creates a new instance of a leap listener.
     * 
     * @param hands2 The hash map to store handFX objects.
     * @param app The application associated with this listener.
     */
    public LeapListener(Map<Integer, HandFX> hands2, Menu app) {
        this.hands = hands2;
        this.app = app;
        handGroup = new Group();
        app.get3D().getChildren().add(handGroup);
    }

    /**
     * Called when this listener is added to a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onConnect(Controller controller) {
        System.out.println("connected leap");
    }

    /**
     * Called when this listener is disconnected from a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onExit(Controller controller) {
        System.out.println("disconnected leap");
    }

    /**
     * Called when a new frame of tracking data is available.
     * 
     * @param controller The leap motion controller to poll.
     */
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        Platform.runLater(() -> {
            if (!frame.hands().isEmpty()) {
                for (Hand leapHand : frame.hands()) {
                    int handId = leapHand.id();
                    HandFX hand = hands.get(handId);

                    // check hashmap to avoid recreation of hands on every frame
                    if (!hands.containsKey(handId)) {
                        hand = new HandFX(app);
                        hands.put(leapHand.id(), hand);
                        handGroup.getChildren().add(hand);
                    }
                    if (hand != null) {
                        hand.update(frame.hand(leapHand.id()));
                    }
                }
            }
            // refresh frame if there are less hands in this frame than in the last one,
            // or rendered hand count and current hand count differ
            if (frame.hands().count() < controller.frame(1).hands().count() 
                    || handGroup.getChildren().size() != frame.hands().count()) {
                hands.clear();
                handGroup.getChildren().clear();
            }
        });
    }

}