package fr.has.evamed.connector.mapper;

import java.util.UUID;

/**
 * Generic ID conversion helpers.
 * Keep this class focused on pure functions to stay reusable across entities.
 */
public final class IdConverters {

    private IdConverters() {
        // utility class
    }

    /**
     * Deterministic UUID derived from a long identifier.
     * This is useful when the DB stores numeric IDs but the API exposes UUIDs.
     */
    public static UUID uuidFromLong(long value) {
        return new UUID(0L, value);
    }

    /**
     * Convert a UUID to a string
     */
    public static UUID uuidFromString(String value) {
        return UUID.fromString(value);
    }

}
