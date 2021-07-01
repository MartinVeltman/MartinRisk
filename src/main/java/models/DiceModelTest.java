package models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceModelTest {

    public static boolean isSorted(ArrayList<Integer> arrayList) {
        for (int i = 0; i < arrayList.size() - 1; i++) {
            if (arrayList.get(i) < arrayList.get(i + 1)) {
                return false; // is dus niet sorted
            }
        }
        return true; // Als die hier is gekomen moet ie wel sorted zijn
    }

    @Test
    public void should_sort_throw_array() {
        DiceModel diceModel= new DiceModel();
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






}