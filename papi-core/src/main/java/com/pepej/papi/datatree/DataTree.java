package com.pepej.papi.datatree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.ConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.stream.Stream;

/**
 * An easier way of parsing in-memory data structures.
 *
 * <p>Given the following example data:</p>
 * <pre>
 * {
 *   "some-object": {
 *     "some-nested-object": {
 *       "a-string": "some special string",
 *       "a-boolean": true,
 *       "some-numbers": [4, 5, 7]
 *        }
 *   }
 * }
 * </pre>
 *
 * <p>Various elements could be parsed as:</p>
 * <p></p>
 * <ul>
 *     <li><code>DataTree.from(root).resolve("some-object", "some-nested-object", "some-numbers", 2).asInt()</code>
 *     would result in the value <code>5</code>.</li>
 *     <li><code>DataTree.from(root).resolve("some-object", "some-nested-object").map(Map.Entry::getKey).toArray(String[]::new)</code>
 *     would result in <code>["a-string", "a-boolean", "some-numbers"]</code></li>
 * </ul>
 *
 * <p>Methods always return a value, throwing an exception if a value isn't present or appropriate.</p>
 */
public interface DataTree {

    /**
     * Creates a new {@link DataTree} from a {@link JsonElement}.
     *
     * @param element the element
     * @return a tree
     */
    @NonNull
    static GsonDataTree from(@NonNull JsonElement element) {
        return new GsonDataTree(element);
    }

    /**
     * Creates a new {@link DataTree} from a {@link ConfigurationNode}.
     *
     * @param node the node
     * @return a tree
     */
    @NonNull
    static ConfigurateDataTree from(@NonNull ConfigurationNode node) {
        return new ConfigurateDataTree(node);
    }

    /**
     * Resolves the given path.
     *
     * <p>Paths can be made up of {@link String} or {@link Integer} components. Strings are used to
     * resolve a member of an object, and integers resolve a member of an array.</p>
     *
     * @param path the path
     * @return the resultant tree node
     */
    @NonNull
    DataTree resolve(@NonNull Object... path);

    /**
     * Gets a stream of the member nodes, as if this tree was a {@link JsonObject}.
     *
     * @return the members
     */
    @NonNull
    Stream<? extends Map.Entry<String, ? extends DataTree>> asObject();

    /**
     * Gets a stream of the member nodes, as if this tree was a {@link JsonArray}.
     *
     * @return the members
     */
    @NonNull
    Stream<? extends DataTree> asArray();

    /**
     * Gets an indexed stream of the member nodes, as if this tree was a {@link JsonArray}.
     *
     * @return the members
     */
    @NonNull
    Stream<? extends Map.Entry<Integer, ? extends DataTree>> asIndexedArray();

    /**
     * Returns a {@link String} representation of this node.
     *
     * @return this as a string
     */
    @NonNull
    String asString();

    /**
     * Returns a {@link Number} representation of this node.
     *
     * @return this as a number
     */
    @NonNull
    Number asNumber();

    /**
     * Returns an {@link Integer} representation of this node.
     *
     * @return this as an integer
     */
    int asInt();

    /**
     * Returns a {@link Double} representation of this node.
     *
     * @return this as a double
     */
    double asDouble();

    /**
     * Returns a {@link Boolean} representation of this node.
     *
     * @return this as a boolean
     */
    boolean asBoolean();

}