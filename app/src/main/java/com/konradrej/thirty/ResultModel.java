package com.konradrej.thirty;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores round results under the appropriate count option.
 *
 * @author Konrad Rej
 */
public class ResultModel implements Parcelable {
    Map<CharSequence, Integer> valuePerOption = new HashMap<>();

    public ResultModel() {

    }

    protected ResultModel(Parcel in) {
        in.readMap(valuePerOption, Integer.class.getClassLoader());
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel in) {
            return new ResultModel(in);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };

    /**
     * Calculates the max amount of possible non overlapping subsets from set that can be
     * constructed at once and whose sum equals the target value.
     *
     * @param set    set of digits to be used
     * @param target target value
     * @return max amount of sets possible
     */
    public static int calcMaxAmountOfPossibleSets(int[] set, int target) {
        List<List<Integer>> combinations = findLists(set, target, 0);
        List<List<List<Integer>>> combinedLists = combineLists(combinations, 0);

        int maxLength = 0;
        for (List<List<Integer>> list : combinedLists) {
            if (list.size() > maxLength) {
                maxLength = list.size();
            }
        }

        return maxLength;
    }

    /**
     * Creates list of all possible combinations of the digits in the set that sum to
     * the target value.
     *
     * @param set      set of available digits
     * @param target   target value
     * @param lastSpot last used spot in the set
     * @return list of number combinations equaling target value
     * (index in set, not value itself)
     */
    public static List<List<Integer>> findLists(int[] set, int target, int lastSpot) {
        List<List<Integer>> lists = new ArrayList<>();

        for (int i = lastSpot; i < set.length; i++) {
            int value = set[i];

            if (value > target) {
                continue;
            }

            if (value == target) {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                lists.add(list);
            }

            if (value < target) {
                List<List<Integer>> receivedLists = findLists(set, target - value, i + 1);

                for (List<Integer> receivedList : receivedLists) {
                    receivedList.add(i);
                    lists.add(receivedList);
                }
            }
        }

        return lists;
    }

    /**
     * Generates a list of all possible list combinations where the values in the lists
     * do not overlap.
     *
     * @param inputLists list containing lists of numbers
     * @param lastSpot   last used spot in the set
     * @return list of possible list combinations where the lists do not overlap
     */
    public static List<List<List<Integer>>> combineLists(List<List<Integer>> inputLists, int lastSpot) {
        List<List<List<Integer>>> returnList = new ArrayList<>();

        for (int i = lastSpot; i < inputLists.size(); i++) {
            List<List<Integer>> addList = new ArrayList<>();
            List<Integer> workList = inputLists.get(i);

            addList.add(workList);

            for (int j = lastSpot + 1; j < inputLists.size(); j++) {
                boolean isDisjoint = true;

                for (int k = 0; k < addList.size(); k++) {
                    if (!Collections.disjoint(inputLists.get(j), addList.get(k))) {
                        isDisjoint = false;
                    }
                }

                if (isDisjoint) {
                    addList.add(inputLists.get(j));
                }
            }

            returnList.add(addList);
        }

        return returnList;
    }

    /**
     * Calculates score for round and stores it under the correct count option.
     *
     * @param countOption which count option to store result as
     * @param diceValues  array of dice values
     */
    public void addResult(CharSequence countOption, int[] diceValues) {
        int totalScore = 0;
        if (countOption.equals("Low")) {
            for (int i = 0; i < diceValues.length; i++) {
                if (diceValues[i] <= 3) {
                    totalScore += diceValues[i];
                }
            }
        } else {
            int targetCombination = Integer.parseInt((String) countOption);
            totalScore = targetCombination * calcMaxAmountOfPossibleSets(diceValues, targetCombination);
        }

        valuePerOption.put(countOption, totalScore);
    }

    /**
     * Returns the stored value for the selected count option.
     *
     * @param countOption count option to get value for
     * @return final score for count option or -1 if count option does not exist
     */
    public int getOptionValue(CharSequence countOption) {
        if (valuePerOption.containsKey(countOption) && valuePerOption.get(countOption) != null) {
            return valuePerOption.get(countOption);
        } else {
            return -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(valuePerOption);
    }
}
