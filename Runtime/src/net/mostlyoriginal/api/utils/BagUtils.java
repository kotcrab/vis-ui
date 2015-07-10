package net.mostlyoriginal.api.utils;

import com.artemis.utils.Bag;

import java.util.Arrays;

/**
 * Various bag related utilities.
 *
 * @author Daan van Yperen
 */
public class BagUtils {

    /**
     * Sort bag.
     * Entities must implement Sortable.
     *
     * Be aware that bags require a resort when you
     * remove entries.
     */
    public static void sort(Bag bag) {
	    if ( !bag.isEmpty() ) {
		    Arrays.sort(bag.getData(), 0, bag.size());
	    }
    }

}
