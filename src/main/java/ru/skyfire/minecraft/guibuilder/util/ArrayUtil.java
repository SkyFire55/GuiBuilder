package ru.skyfire.minecraft.guibuilder.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil {
    public static List<Integer> arrayCardToList(int[][] arrayCard){
        List<Integer> cardList = new ArrayList<>();
        int x = 0;
        int y = 0;
        while(x<5){
            while(y<5){
                cardList.add(arrayCard[x][y]);
                y++;
            }
            y = 0;
            x++;
        }
        return cardList;
    }

    public static int[][] listToArrayCard(List<Integer> cardList){
        int[][] arrayCard = new int[5][5];
        int x = 0;
        int y = 0;
        int z = 0;
        while(x<5){
            while(y<5){
                arrayCard[x][y] = cardList.get(z);
                z++;
                y++;
            }
            y = 0;
            x++;
        }
        return arrayCard;
    }
}
