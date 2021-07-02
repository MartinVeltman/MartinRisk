package models;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class DiceModelTest {
    DiceModel diceModel= new DiceModel();

    private static boolean isSorted(ArrayList<Integer> arrayList) {
        for (int i = 0; i < arrayList.size() - 1; i++) {
            if (arrayList.get(i) < arrayList.get(i + 1)) {
                return false; // is dus niet sorted
            }
        }
        return true; // Als die hier is gekomen moet ie wel sorted zijn
    }

    @Test
    public void shouldSortDiceArray() {
        ArrayList<Integer> notSortedThrow= new ArrayList<>();
        notSortedThrow.add(4);
        notSortedThrow.add(3);
        notSortedThrow.add(5);
        ArrayList<Integer> worp = diceModel.roll(10);
        ArrayList<Integer> worp1 = diceModel.roll(1000);
        ArrayList<Integer> worp2 = diceModel.roll(2);

        assertTrue(isSorted(worp));
        assertTrue(isSorted(worp1));
        assertTrue(isSorted(worp2));
        assertFalse(isSorted(notSortedThrow));

    }


    @Test
    public void checkAmountofDice(){
        ArrayList<Integer> worp = diceModel.roll(10);
        ArrayList<Integer> worp1 = diceModel.roll(1000);
        ArrayList<Integer> worp2 = diceModel.roll(2);
        assertEquals(10, worp.size());
        assertEquals(1000, worp1.size());
        assertEquals(2, worp2.size());
        assertFalse(worp1.size() != 1000);

    }







}