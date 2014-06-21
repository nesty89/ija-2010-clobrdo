package projekt.client;
import java.util.*;

/**
 * Trida Cube - generuje hazeni kostkou
 * @author now
 */
public class Cube {
    protected int value;

    /**
     * vraci hodnotu kostky
     * @return int hodnota hozena na kostce
     */
    public int getValue() {
        return this.value;
    }

    /**
     * vygeneruje nahodnou hodnotu hozenou na kostce
     */
    public void setValue() {
        Random rand = new Random();
        this.value = Math.abs(rand.nextInt() % 6);
        //return this.value;
    }

}
