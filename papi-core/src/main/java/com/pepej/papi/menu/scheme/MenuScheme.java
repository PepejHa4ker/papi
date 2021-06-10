package com.pepej.papi.menu.scheme;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.menu.Menu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface MenuScheme {

    @NotNull
    static MenuScheme create(@NotNull final SchemeMapping mapping) {
        return new MenuSchemeImpl(mapping);
    }

    @NotNull
    static MenuScheme create() {
        return new MenuSchemeImpl();
    }

    @NotNull
    MenuScheme mask(@NotNull String mask);

    @NotNull
    MenuScheme masks(@NotNull String... masks);

    @NotNull
    MenuScheme maskEmpty(int lines);

    @NotNull
    MenuScheme scheme(int... schemeIds);

    void apply(@NotNull Menu menu);

    default ImmutableList<Integer> getMaskedIndexesImmutable() {
        return ImmutableList.copyOf(getMaskedIndexes());
    }

    default MenuPopulatorImpl newPopulator(Menu menu) {
        return new MenuPopulatorImpl(menu, this);
    }

    @NotNull
    List<Integer> getMaskedIndexes();

    @NotNull
    MenuScheme copy();

    @NotNull
    SchemeMapping getMapping();

    @NotNull
    @UnmodifiableView
    List<boolean[]> getMaskRows();

    @NotNull
    @UnmodifiableView
    List<int[]> geSchemeRows();



}
