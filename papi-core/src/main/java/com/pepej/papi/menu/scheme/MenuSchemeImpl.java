package com.pepej.papi.menu.scheme;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.menu.Menu;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helps to populate a menu with border items
 */
class MenuSchemeImpl implements MenuScheme {
    private static final boolean[] EMPTY_MASK = new boolean[]{false, false, false, false, false, false, false, false, false};
    private static final int[] EMPTY_SCHEME = new int[0];

    private final SchemeMapping mapping;
    private final List<boolean[]> maskRows;
    private final List<int[]> schemeRows;

    MenuSchemeImpl(@NotNull SchemeMapping mapping) {
        this.mapping = mapping;
        this.maskRows = new ArrayList<>();
        this.schemeRows = new ArrayList<>();
    }

    MenuSchemeImpl() {
        this(StandardSchemeMappings.EMPTY);
    }

    private MenuSchemeImpl(MenuScheme other) {
        this.mapping = other.getMapping().copy();
        this.maskRows = new ArrayList<>();
        for (boolean[] arr : other.getMaskRows()) {
            this.maskRows.add(Arrays.copyOf(arr, arr.length));
        }
        this.schemeRows = new ArrayList<>();
        for (int[] arr : other.geSchemeRows()) {
            this.schemeRows.add(Arrays.copyOf(arr, arr.length));
        }
    }

    @Override
    public @NotNull MenuScheme mask(@NotNull String mask) {
        char[] chars = mask.replace(" ", "").toCharArray();
        if (chars.length != 9) {
            throw new IllegalArgumentException("invalid mask: " + mask);
        }
        boolean[] ret = new boolean[9];
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '1' || c == 't' || c == 'x') {
                ret[i] = true;
            } else if (c == '0' || c == 'f' || c == 'o') {
                ret[i] = false;
            } else {
                throw new IllegalArgumentException("invalid mask character: " + c);
            }
        }
        this.maskRows.add(ret);
        return this;
    }

    @Override
    public @NotNull MenuScheme masks(String... masks) {
        for (String mask : masks) {
            mask(mask);
        }
        return this;
    }

    @Override
    public @NotNull MenuScheme maskEmpty(int lines) {
        for (int i = 0; i < lines; i++) {
            this.maskRows.add(EMPTY_MASK);
            this.schemeRows.add(EMPTY_SCHEME);
        }
        return this;
    }

    @Override
    public @NotNull MenuScheme scheme(int... schemeIds) {
        for (int schemeId : schemeIds) {
            if (!mapping.hasMappingFor(schemeId)) {
                throw new IllegalArgumentException("mapping does not contain value for id: " + schemeId);
            }
        }
        schemeRows.add(schemeIds);
        return this;
    }

    @Override
    public void apply(@NotNull Menu menu) {
        // the index of the item slot in the inventory
        int invIndex = 0;

        // iterate all of the loaded masks
        for (int i = 0; i < maskRows.size(); i++) {
            boolean[] mask = maskRows.get(i);
            int[] scheme = schemeRows.get(i);

            int schemeIndex = 0;

            // iterate the values in the mask (0 --> 8)
            for (boolean b : mask) {

                // increment the index in the menu. we're handling a new item.
                int index = invIndex++;

                // if this index is masked.
                if (b) {

                    // this is the value from the scheme map for this slot.
                    int schemeMappingId = scheme[schemeIndex++];

                    // lookup the value for this location, and apply it to the menu
                    mapping.get(schemeMappingId).ifPresent(item -> menu.setItem(index, item));
                }
            }
        }
    }

    @Override
    public @NotNull List<Integer> getMaskedIndexes() {
        List<Integer> ret = new LinkedList<>();

        // the index of the item slot in the inventory
        int invIndex = 0;

        // iterate all of the loaded masks
        for (boolean[] mask : this.maskRows) {
            // iterate the values in the mask (0 --> 8)
            for (boolean b : mask) {

                // increment the index in the menu. we're handling a new item.
                int index = invIndex++;

                // if this index is masked.
                if (b) {
                    ret.add(index);
                }
            }
        }

        return ret;
    }

    @Override
    public @NotNull MenuScheme copy() {
        return new MenuSchemeImpl(this);
    }

    @Override
    public @NotNull SchemeMapping getMapping() {
        return mapping;
    }

    @Override
    public @NotNull @UnmodifiableView List<boolean[]> getMaskRows() {
        return maskRows;
    }

    @Override
    public @NotNull @UnmodifiableView List<int[]> geSchemeRows() {
        return schemeRows;
    }
}
