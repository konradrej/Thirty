package com.konradrej.thirty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultModel implements Serializable {
    String lowOption;
    Map<CharSequence, Integer> valuePerOption = new HashMap<>();

    public ResultModel(String lowOption){
        this.lowOption = lowOption;
    }

    public void addResult(CharSequence countOption, int[] diceValues, int n){
        int totalScore = 0;
        if(((String) countOption).equals(lowOption)){
            for(int i = 0; i < n; i++){
                if(diceValues[i] <= 3){
                    totalScore += diceValues[i];
                }
            }
        }else{
            int targetCombination = Integer.parseInt((String) countOption);
            totalScore = targetCombination * calcMaxAmountOfPossibleSets(diceValues, targetCombination);
        }

        valuePerOption.put(countOption, totalScore);
    }

    public int getOptionValue(CharSequence option){
        if(valuePerOption.containsKey(option) && valuePerOption.get(option) != null){
            return valuePerOption.get(option);
        }else{
            return -1;
        }
    }

    public static int calcMaxAmountOfPossibleSets(int[] set, int target){
        List<List<Integer>> combinations = findLists(set, target, 0);
        List<List<List<Integer>>> combinedLists = combineLists(combinations, 0);

        int maxLength = 0;
        for(List<List<Integer>> list : combinedLists){
            if(list.size() > maxLength){
                maxLength = list.size();
            }
        }

        return maxLength;
    }

    public static List<List<Integer>> findLists(int[] set, int target, int lastSpot){
        List<List<Integer>> lists = new ArrayList<>();

        for(int i = lastSpot; i < set.length; i++){
            int value = set[i];

            if(value > target){
                continue;
            }

            if(value == target){
                List<Integer> list = new ArrayList<>();
                list.add(i);
                lists.add(list);
            }

            if(value < target){
                List<List<Integer>> receivedLists = findLists(set, target - value, i+1);

                for(List<Integer> receivedList : receivedLists){
                    receivedList.add(i);
                    lists.add(receivedList);
                }
            }
        }

        return lists;
    }

    public static List<List<List<Integer>>> combineLists(List<List<Integer>> inputLists, int lastSpot){
        List<List<List<Integer>>> returnList = new ArrayList<>();

        for(int i = lastSpot; i < inputLists.size(); i++){
            List<List<Integer>> addList = new ArrayList<>();
            List<Integer> workList = inputLists.get(i);

            addList.add(workList);

            for(int j = lastSpot + 1; j < inputLists.size(); j++){
                boolean isDisjoint = true;

                for(int k = 0; k < addList.size(); k++){
                    if(!Collections.disjoint(inputLists.get(j), addList.get(k))){
                        isDisjoint = false;
                    }
                }

                if(isDisjoint){
                    addList.add(inputLists.get(j));
                }
            }

            returnList.add(addList);
        }

        return returnList;
    }
}
