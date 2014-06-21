package projekt.client;

/**
 * objekt reprezentujici figurku
 * @author nesty
 */
public class Figure {
   protected int playerId; // id hrace
   protected int id; // id figurky
   protected int position = -1; // aktualni pozice od startu (start = 0, sdomecek -1, cil = pocet poli+1)
   protected int start; // odkazuje se na pocatecni pozici pred nasazenim
   protected int color;
   protected int startFieldId;

   /**
    * konstruktor figurky
    * @param playerId - id hrace kteremu figurka patri
    * @param id - id figurky
    * @param startFieldId - pocatecni pozice pred nasazenim
    * @param start - policko na kterem startuje figurka
    * @param color - barva figurky
    */
   public Figure(int playerId, int id, int startFieldId, int start, int color) // int color
   {
      this.playerId = playerId;
      this.id = id;
      this.start = start;
      this.color = color;
      this.startFieldId = startFieldId;
   }

   /**
    * nastavi pozici figurky
    * @param position pozice k nastaveni
    */
   public void setPosition(int position)
   {
       this.position = position;
   }

   /**
    * vrati aktualni pozici figurky
    * @return pozice figurky
    */
   public int getPosition()
   {
       return this.position;
   }

   /**
    * vyresetuje pozici, neboli vyhodi figurku
    */
   public void resetPosition() // vyhozeni figurky
   {
       this.position = this.start;
   }

   /**
    * vrati id figurky
    * @return id figurky
    */
   public int getId()
   {
       return this.id;
   }

   /**
    * vrati id hrace kteremu figurka patri
    * @return id hrace
    */
   public int getPlayerId()
   {
       return this.playerId;
   }
}
