package fr.antyss77.lootkit.core;

import java.util.Objects;
import java.util.UUID;

/**
 * The identifier of one <em>individual</em> item — this rifle, the one with 12
 * rounds left, not "a rifle".
 *
 * <p>Do not confuse it with {@link ItemDefinition#id()}, which names a kind of
 * item and is what players type in commands. Instance ids are generated, never
 * authored, and are what you use to track, trade or log a single object.
 *
 * <p>Generated with {@link UUID#randomUUID()}: no central counter, no collision
 * risk when two servers or two save files are merged. The {@link #shortForm()}
 * is the first eight characters, short enough to print in a log line or paste
 * into an admin command.
 */
public record InstanceId(UUID uuid) implements Comparable<InstanceId> {

    public InstanceId {
        Objects.requireNonNull(uuid, "uuid");
    }

    public static InstanceId random() {
        return new InstanceId(UUID.randomUUID());
    }

    /** Rebuilds an id from a saved string; throws if it is not a valid UUID. */
    public static InstanceId parse(String text) {
        return new InstanceId(UUID.fromString(text));
    }

    /** First eight characters — ambiguous in theory, fine for logs and admin tools. */
    public String shortForm() {
        return uuid.toString().substring(0, 8);
    }

    @Override
    public int compareTo(InstanceId other) {
        return uuid.compareTo(other.uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
