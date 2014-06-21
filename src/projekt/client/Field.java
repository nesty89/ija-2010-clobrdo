package projekt.client;
import javax.swing.*;

/**
 * Objekt reprezentujici policko
 * @author now
 */
public class Field {
    protected int id;
    protected int x;
    protected int y;
    protected int type;     // 1-startHome, 2-normalField, 3-startingField, 4-finishHome
    protected int imageId;
    protected Figure figure;
    protected JLabel figureLabel;
   // protected int startFieldPlayer;  // nastavi se sem ID hrace, pro ktereho je to startovni policko
    protected int playerColor;

    /**
     * konstruktor policka
     * @param id - id policka pro jednoznacnou identifikaci
     * @param x - x-ova souradnice v okne
     * @param y - y-ova souradnice v okne
     * @param type - typ policka - startovni domecek, normalni policko, koncovy domecek
     * @param imageId - id obrazku ktery se na policku vyskytuje
     * @param figure - objekt figurky
     * @param figureLabel - label s obrazkem figurky umistene na policku
     * @param playerColor - barva hrace ktery je na policku
     */
    public Field(int id, int x, int y, int type, int imageId, Figure figure, JLabel figureLabel, int playerColor) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.imageId = imageId;
        this.playerColor = playerColor;
        this.figure = figure;
        this.figureLabel = figureLabel;
       // this.startFieldPlayer = startFieldPLayer;
    }

    /**
     * vrati x-ovou souradnici
     * @return x-ovou souradnici
     */
    public int getX()
    {
        return this.x;
    }

    /**
     * vrati y-ovou souradnici
     * @return y-ova souradnice
     */
    public int getY()
    {
        return this.y;
    }

    /**
     * vrati id policka
     * @return id policka
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * vrati typ policka
     * @return typ policka
     */
    public int getType()
    {
        return this.type;
    }

    /**
     * vrati obrazek ktery je na policku
     * @return obrazek na policku
     */
    public int getImageId()
    {
        return this.imageId;
    }
}
