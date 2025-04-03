package edu.caltech.cs2.lab06;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;



public class DecisionTree {
    private final DecisionTreeNode root;

    public DecisionTree(DecisionTreeNode root) {

        this.root = root;
    }

    public String predict(Dataset.Datapoint point) {
        DecisionTreeNode curr = root;
        while (!curr.isLeaf()) {
            Map<String, DecisionTreeNode> child = ((AttributeNode) curr).children;
            String attr = ((AttributeNode) curr).attribute;
            String value = point.attributes.get(attr);
            curr = child.get(value);
        }

        return ((OutcomeNode) curr).outcome;
    }

    public static DecisionTree id3(Dataset dataset, List<String> attributes) {
        DecisionTree tree = new DecisionTree(id3helper(dataset, attributes));
        return tree;
    }

    public static DecisionTreeNode id3helper(Dataset dataset, List<String> attributes) {
        String str = dataset.pointsHaveSameOutcome();
        OutcomeNode child;
        if (!str.isEmpty()) {
            return new OutcomeNode(str);
        } if (attributes.isEmpty()) {
            return new OutcomeNode(dataset.getMostCommonOutcome());
        }

        String a = dataset.getAttributeWithMinEntropy(attributes);
        List<String> newList = dataset.getFeaturesForAttribute(a);
        Map<String, DecisionTreeNode> newMap = new HashMap<>();

        for (String feature: newList) {
            Dataset newSet = dataset.getPointsWithFeature(feature);

            if (newSet.isEmpty()) {
                child = new OutcomeNode(dataset.getMostCommonOutcome());
                newMap.put(feature, child);
            } else {
                List<String> newAttributes = new ArrayList<>(attributes);
                newAttributes.remove(a);
                newMap.put(feature, id3helper(newSet, newAttributes));
            }
        }

        return new AttributeNode(a, newMap);
    }
}
