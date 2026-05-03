package org.example.studentmanagement.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class GenericListUtils {
    private GenericListUtils() {
    }

    public static <T> List<T> sortedCopy(List<T> source, Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(source);
        copy.sort(comparator);
        return copy;
    }
}
