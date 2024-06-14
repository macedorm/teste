package br.com.jjconsulting.mobile.jjlib.util;

public class ArrayUtils {

    private ArrayUtils() {

    }

    /**
     * Find the index of the object in the array.
     * @return index or -1 if not found.
     */
    public static int indexOf(Object[] array, Object object) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(object)) {
                return i;
            }
        }

        return -1;
    }
}
