package projekt.client;

/**
 * Objekt reprezentujici hrace
 * @author nesty
 */
public class Player {

    final int type; // clovek / PC 0 / 1
    final int playerNum; // 0-7 podle poradi
    final int color; // barva figurek - pokud se nebude zadavat implicitne podle poradi
    final int startField; // startovni policko - ID
    final int AI; // nastaveni rozhodovoani jak posunovat figurky - musi byt pro kazdeho hrace zvlast
    
    /**
     * konstruktor objektu
     * @param type - type hrace (PC nebo HUMAN)
     * @param playerNum - cislo hrace
     * @param color - barva
     * @param startField - nasazovaci policko
     * @param AI - aktualni AI
     */
    public Player(int type, int playerNum, int color, int startField, int AI)
    {
        this.type = type;
        this.playerNum = playerNum;
        this.color = color;
        this.startField = startField;
        this.AI = AI;
    }

    /**
     * vrati typ hrace (PC nebo human)
     * @return typ 
     */
    public int getType() // pro rozeznani kdy vyuzit ai
    {
        return this.type;
    }

    /**
     * vrati cislo hrace
     * @return cislo hrace
     */
    public int getPlayerNum() // pro poradi hry mozna se da pryc
    {
        return this.playerNum;
    }

    /**
     * vrati barvu hrace
     * @return barva
     */
    public int getPlayerColor() // mozna se da pryc
    {
        return this.color;
    }

    /**
     * vrati id policko, ne ktere hrac nasazuje figurky
     * @return id policka 
     */
    public int getStartField()
    {
        return this.startField;
    }

    /**
     * vrati typ AI
     * @return typ AI
     */
    public int getAiType()
    {
        return this.AI;
    }

}
