package projekt.client;

import javax.swing.*;
import java.util.*;


/**
 * trida MoveCalc - move calculator
 * @author Igor
 */
public class MoveCalc {
    protected int player;
    protected int moveType;
    protected ArrayList<Field> pole;// field, start, end
    protected int newVal;
    protected int moveable = 0;
    protected int fieldSize;
    protected int finishindex; // konec pole
    /**
     * konstruktor tridy
     * @param player hrac
     * @param moveType typ tahu 
     * @param newVal nova hodnota
     * @param pole herni pole
     * @param fieldSize velikost herniho pole
     */
    public MoveCalc(int player,int moveType, int newVal, ArrayList<Field> pole, int fieldSize)
    {
        this.player = player;
        this.moveType = moveType;
        this.pole = pole;
        this.newVal = newVal;
        this.fieldSize = fieldSize;
        this.finishindex = (pole.size() - fieldSize) / 2 + fieldSize; // vypocet zacatku cilovych domecku
    }

    /**
     * metoda pro vypocet tahu
     * @param player id hrace 
     * @param moveType typ tahu
     * @param newVal nova hodnota
     * @param pole herni pole
     * @return vraci 1/0 podle moznosti hry
     */
    public int calculate(int player,int moveType, int newVal, ArrayList<Field> pole)
    {
        this.moveable = 0;
        this.player = player;
        this.moveType = moveType;
        this.pole = pole;
        this.newVal = newVal;
        moveEnable(pole,player,newVal); // vypocet moznosti tahu
        
        if(moveable == 0) 
            return this.moveable;
      switch(moveType)
      {
          case 0:
              defRun();
              break;
          case 1:
              longRun();
              break;
          case 2:
              if(!newRun())
              {
                  longRun();
              }
              break;
          case 3:
              if(!killRun())
              {
                  longRun();
              }
              break;
          case 4:
              if(!homeRun())
              {
                  longRun();
              }
              break;
      }
      return this.moveable;
    }

    /**
     * metoda pro vypocet moznosti tahu
     * @param pole herni pole
     * @param player id aktualniho hrace
     * @param val nova hodnota
     * @return vraci 1/0 moznosti tahu 
     */
    public int moveEnable(ArrayList<Field> pole, int player, int val) // overeni moznosti posunu na dalsi polozku
    {
       this.moveable = 0;
       this.pole = pole;
       this.player = player;
       this.newVal = val;
       Field f, f2;
       boolean found = false;
       if(val == 0)
       {
           for(int i = fieldSize; i < finishindex; i++)
           {
               f = pole.get(i);
               if(f.playerColor == player && f.figure != null)
               {
                   f2 = pole.get(f.figure.startFieldId);
                   if(f2.figure == null || f2.figure.playerId != player)
                   {
                       //System.out.println("1");
                       this.moveable = 1;
                       return 1;
                   }
               }
           }
       }
       for(int i = 0; i < fieldSize; i++)
       {
           f = pole.get(i);
           if(i == fieldSize - 1 && f.figure != null && f.figure.playerId == player)
           {
               for(int j = finishindex; j < pole.size(); j++)
               {
                   f2 = pole.get(j);
                   if(f2.playerColor == player && f2.figure == null && f2.imageId == val)
                   {
                       //System.out.println("2");
                       this.moveable = 1;
                       return 1;
                   }
               }
           }
           if(f.figure != null && f.figure.playerId == player)
           {
               for(int j = (i + 1) % fieldSize, k = 1; k + f.figure.position < fieldSize; j = (j+1)%fieldSize, k++ )
               {
                   
                   f2 = pole.get(j);
                   if(f2.imageId == val)
                   {found = true;
                       if (f2.figure == null || f2.figure.playerId != player)
                       {
                           //System.out.println("3");
                           this.moveable = 1;
                           return 1;
                       }
                   }
               }
           }//System.out.println("find 1");
           if(!found && f.figure != null && f.figure.playerId == player)
           {//System.out.println("find2");
               for(int j = finishindex; j < pole.size(); j++)
               {//System.out.println("find3");
                   f2 = pole.get(j);
                   if(f2.playerColor == player && f2.figure == null && f2.imageId == val)
                   {
                       //System.out.println("4");
                       this.moveable = 1;
                       return 1;
                   }
               }
           }
       }
       return 0;
    }
    
    /**
     * metoda pro beh do domecku
     * @return true/false pri uspechu/neuspechu
     */
    public boolean homeRun()
    {
        Field f, f2;
        for(int i = 0; i < this.fieldSize; i++)
        {
            f = this.pole.get(i);
            if(f.figure != null && f.figure.playerId == player && f.figure.position == fieldSize - 1)
            {
                for(int k = this.finishindex; k < this.pole.size(); k++)
                {
                    f2 = this.pole.get(k);
                    if(f2.playerColor == player && f2.figure == null && newVal == f2.imageId)
                    {
                        Icon icon;
                        icon = f2.figureLabel.getIcon();
                        f2.figureLabel.setIcon(f.figureLabel.getIcon());
                        f.figureLabel.setIcon(icon);
                        f2.figure = f.figure;
                        f2.figure.position = this.fieldSize + 1;
                        f.figure = null;
                        this.pole.set(f2.id, f2);
                        this.pole.set(f.id,f);
                        return true;
                    }
                }
            }
            if(f.figure != null && f.figure.playerId == this.player)
            {
                for(int j = (i + 1) % this.fieldSize, l = 1; l + f.figure.position < this.fieldSize; j = (j + 1)%fieldSize, l++)
                {
                    f2 = this.pole.get(j);
                    if(f2.imageId == newVal)
                    {
                        break;
                    }
                    if(fieldSize-1 == (f.figure.position + l) && f2.imageId != this.newVal)
                    {
                        for(int k = this.finishindex; k < this.pole.size(); k++)
                        {
                            f2 = this.pole.get(k);
                            if(f2.playerColor == player && f2.figure == null)
                            {
                                Icon icon;
                                icon = f2.figureLabel.getIcon();
                                f2.figureLabel.setIcon(f.figureLabel.getIcon());
                                f.figureLabel.setIcon(icon);
                                f2.figure = f.figure;
                                f2.figure.position = this.fieldSize + 1;
                                f.figure = null;
                                this.pole.set(f2.id, f2);
                                this.pole.set(f.id,f);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * metoda pro nejdelsi skok
     * @return true/false pri uspechu/neuspechu
     */
    public boolean longRun()
    {
        Field f, f2, f3;
        int max = 0;
        int start = 0;
        int end = 0;
        for(int i = 0; i < fieldSize; i++)
        {
            f = pole.get(i);
            if(f.figure != null && f.figure.position == fieldSize - 1 && newVal == 0)
                break;
            if(f.figure != null && f.figure.playerId == player)
            {               
                for(int j = (i + 1) % fieldSize,k = 1; k + f.figure.position < fieldSize; k++, j = (j + 1) % fieldSize)
                {
                    f2 = pole.get(j);
                    if(f2.id == (f.figure.startFieldId - 1)%fieldSize)
                        break;
                    if(f2.imageId == newVal)
                    {
                        if(f2.figure == null || f.figure.playerId != player)
                        {
                            if(max < k)
                            {
                                start = i;
                                end = j;
                                max = k;
                            }
                        }
                        break;
                    }
                    if(j == f.figure.startFieldId)
                        break;
                }
            }
            
        }
        if(max == 0 && newVal == 0)
        {
            if(newRun())
                return true;
        }
        if(max == 0 && newVal != 0)
        {
            if(homeRun())
                return true;
        }
        if(max != 0)
        {
            f = pole.get(start);
            if(f.figure.position == fieldSize - 1)
                return false;
            f2 = pole.get(end);
            if(f2.figure != null)
            {
                f3 = pole.get(f2.figure.start);
                f3.figure = f2.figure;
                f3.figure.position = -1;
                Icon icon = f3.figureLabel.getIcon();
                f3.figureLabel.setIcon(f2.figureLabel.getIcon());
                f2.figure = f.figure;
                f2.figure.position += max;
                f2.figureLabel.setIcon(f.figureLabel.getIcon());
                f.figure = null;
                f.figureLabel.setIcon(icon);
                pole.set(f.id, f);
                pole.set(f2.id, f2);
                pole.set(f3.id, f3);
                return true;
            }
            else
            {
                Icon icon = f2.figureLabel.getIcon();
                f2.figureLabel.setIcon(f.figureLabel.getIcon());
                f2.figure = f.figure;
                f2.figure.position += max;
                f.figure = null;
                f.figureLabel.setIcon(icon);
                pole.set(f.id, f);
                pole.set(f2.id, f2); 
            }
        }
        return false;
    }

    /**
     * metoda pro uprednostneni nasazeni
     * @return true/false pri uspechu/neuspechu
     */
    public boolean newRun()
    {
       Field f, f2;
       
       
       if(this.newVal > 0)
       {
           return false;
       }
       else{
       for(int i = this.fieldSize; i < this.finishindex; i++)
       {
           f = pole.get(i);
           if(f.playerColor == player && f.figure != null)
           {
               f2 = pole.get(f.figure.startFieldId);
               if(f2.figure != null && f2.figure.playerId == this.player)
               {
                   return false;
               }
               if(f2.figure == null || f2.figure.playerId != this.player)
               {
                   if(f2.figure != null)
                   {
                       Field f3;
                       f3 = pole.get(f2.figure.startFieldId);
                       f3.figure = f2.figure;
                       f3.figure.position = -1;
                       Icon icon = f3.figureLabel.getIcon();
                       f3.figureLabel.setIcon(f2.figureLabel.getIcon());
                       f2.figure = f.figure;
                       f2.figure.position = 0;
                       f2.figureLabel.setIcon(f.figureLabel.getIcon());
                       f.figure = null;
                       f.figureLabel.setIcon(icon);
                       this.pole.set(f.id,f);
                       this.pole.set(f2.id,f2);
                       this.pole.set(f3.id,f3);
                       return true;
                   }
                   else
                   {
                       f2.figure = f.figure;
                       f2.figure.position = 0;
                       Icon icon;
                       icon = f2.figureLabel.getIcon();
                       f2.figureLabel.setIcon(f.figureLabel.getIcon());
                       f.figure = null;
                       f.figureLabel.setIcon(icon);
                       this.pole.set(f.id,f);
                       this.pole.set(f2.id,f2);
                       return true;
                   }
               }
           }
       }
     }
       return false;
    }

    /**
     * metoda pro uprednostneni vyhozeni
     * @return true/false pri uspechu/neuspechu
     */
    public boolean killRun()
    {
        Field f, f2;
        for(int i = 0; i < this.fieldSize; i++)
        {
            f = this.pole.get(i);
            if(f.figure != null && f.figure.playerId == this.player)
            {
                for(int j = (i + 1) % this.fieldSize, k = 1; k + f.figure.position < this.fieldSize ; j = (j + 1) % this.fieldSize, k++)
                {
                    f2 = this.pole.get(j);
                    if(f2.imageId == newVal && f2.figure != null && f2.figure.playerId != player)
                    {
                        Field f3;
                        f3 = this.pole.get(f2.figure.start);
                        f3.figure = f2.figure;
                        f3.figure.position = -1;
                        Icon icon;
                        icon = f3.figureLabel.getIcon();
                        f3.figureLabel.setIcon(f2.figureLabel.getIcon());
                        this.pole.set(f3.id, f3);
                        f2.figure = f.figure;
                        f2.figure.position = f2.figure.position + k;
                        f2.figureLabel.setIcon(f.figureLabel.getIcon());
                        this.pole.set(f2.id, f2);
                        f.figure = null;
                        f.figureLabel.setIcon(icon);
                        this.pole.set(f.id,f);
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * metoda pro vyber tahu pri defaultni hre
     */
    public void defRun()
    {
        if(!homeRun()){
            if(!killRun()){
                if(!newRun()){
                    longRun();
                }
            }
        }
    }
    
    /**
     * metoda pro tah cloveka
     * @return true/false pri uspechu/neuspechu
     */
    public boolean playerMove(int player,int fieldId, int fieldSize, int val,ArrayList<Field> pole)
    {
        Field f, f2, f3;
        f = pole.get(fieldId);
        boolean found = false;
        finishindex = (pole.size() - fieldSize) / 2 + fieldSize;
        if(f.type == 1)
        {
           if(val == 0 && f.playerColor == player)
           {
               f2 = pole.get(f.figure.startFieldId);
               if(f2.figure == null)
               {
                   Icon icon = f2.figureLabel.getIcon();
                   f2.figure = f.figure;
                   f2.figure.position = 0;
                   f.figure = null;
                   f2.figureLabel.setIcon(f.figureLabel.getIcon());
                   f.figureLabel.setIcon(icon);
                   pole.set(f.id,f);
                   pole.set(f2.id, f2);
                   return true;
               }
               else if(f2.figure.playerId != player)
               {
                   f3 = pole.get(f2.figure.start);
                   Icon icon = f3.figureLabel.getIcon();
                   f3.figure = f2.figure;
                   f3.figure.position = -1;
                   f2.figure = f.figure;
                   f2.figure.position = 0;
                   f.figure = null;
                   f3.figureLabel.setIcon(f2.figureLabel.getIcon());
                   f2.figureLabel.setIcon(f.figureLabel.getIcon());
                   f.figureLabel.setIcon(icon);
                   pole.set(f.id,f);
                   pole.set(f2.id, f2);
                   pole.set(f3.id, f3);
                   return true;
               }
               else
               {
                   return false;
               }
           }
        }
        if(f.type == 2 || f.type == 3)
        {
            if(f.figure.position == fieldSize-1)
            {
                for(int i = finishindex; i < pole.size(); i++)
                {
                    f2 = pole.get(i);
                    if(f2.playerColor == player && f2.figure == null && f2.imageId == val)
                    {
                        Icon icon = f2.figureLabel.getIcon();
                        f2.figure = f.figure;
                        f2.figure.position = fieldSize + 1;
                        f.figure = null;
                        f2.figureLabel.setIcon(f.figureLabel.getIcon());
                        f.figureLabel.setIcon(icon);
                        pole.set(f.id,f);
                        pole.set(f2.id, f2);
                        return true;
                    }
                }
                return false;
            }
            for(int j = (fieldId+1)%fieldSize, k = 1; k + f.figure.position < fieldSize; k++, j=(j+1)%fieldSize)
            {
                f2 = pole.get(j);
                if(val == f2.imageId)
                {
                    found = true;
                    if(f2.figure == null)
                    {
                        Icon icon = f2.figureLabel.getIcon();
                        f2.figure = f.figure;
                        f2.figure.position += k;
                        f.figure = null;
                        f2.figureLabel.setIcon(f.figureLabel.getIcon());
                        f.figureLabel.setIcon(icon);
                        pole.set(f.id,f);
                        pole.set(f2.id, f2);
                        return true;
                    }
                    else if(f2.figure.playerId != player)
                    {
                        f3 = pole.get(f2.figure.start);
                        Icon icon = f3.figureLabel.getIcon();
                        f3.figure = f2.figure;
                        f3.figure.position = -1;
                        f3.figureLabel.setIcon(f2.figureLabel.getIcon());
                        f2.figureLabel.setIcon(f.figureLabel.getIcon());
                        f2.figure = f.figure;
                        f2.figure.position += k;
                        f.figure = null;
                        f.figureLabel.setIcon(icon);
                        pole.set(f.id, f);
                        pole.set(f2.id, f2);
                        pole.set(f3.id, f3);
                        return true;
                    }
                    
                }
            }
                if(!found)
                {
                    for(int i = finishindex; i < pole.size();i++)
                    {
                        f2 = pole.get(i);
                        if(f2.playerColor == player && f2.figure == null && f2.imageId == val)
                        {
                            Icon icon = f2.figureLabel.getIcon();
                            f2.figure = f.figure;
                            f.figure = null;
                            f2.figure.position = fieldSize + 1;
                            f2.figureLabel.setIcon(f.figureLabel.getIcon());
                            f.figureLabel.setIcon(icon);
                            return true;
                        }
                    }
                }
                
            }
  
        return false;
    }
}
