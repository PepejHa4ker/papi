package com.pepej.papi.menu.scheme;

import com.google.common.collect.Range;
import com.pepej.papi.item.ItemStackBuilder;
import org.bukkit.Material;

/**
 * Contains a number of default {@link SchemeMapping}s.
 */
public final class StandardSchemeMappings {

    private static final Range<Integer> COLORED_MATERIAL_RANGE = Range.closed(0, 15);

    public static final SchemeMapping STAINED_GLASS = forColoredMaterial(Material.STAINED_GLASS_PANE);
    public static final SchemeMapping STAINED_GLASS_BLOCK = forColoredMaterial(Material.STAINED_GLASS);
    public static final SchemeMapping HARDENED_CLAY = forColoredMaterial(Material.STAINED_CLAY);
    public static final SchemeMapping WOOL = forColoredMaterial(Material.WOOL);
    public static final SchemeMapping EMPTY = new EmptySchemeMapping();

    private static SchemeMapping forColoredMaterial(Material material) {
        return FunctionalSchemeMapping.of(
                data -> ItemStackBuilder.of(material).name("&f").data(data).build(null),
                COLORED_MATERIAL_RANGE
        );
    }

    private StandardSchemeMappings() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
