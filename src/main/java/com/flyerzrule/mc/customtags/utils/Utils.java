package com.flyerzrule.mc.customtags.utils;

import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.customtags.models.Tag;

public class Utils {
    public static List<Tag> getDifferenceTags(List<Tag> list1, List<Tag> list2) {
        List<Tag> uniqueToList1 = new ArrayList<>(list1);
        List<Tag> uniqueToList2 = new ArrayList<>(list2);

        uniqueToList1.removeAll(list2);
        uniqueToList2.removeAll(list1);

        List<Tag> result = new ArrayList<>(uniqueToList1);
        result.addAll(uniqueToList2);

        return result;
    }
}
