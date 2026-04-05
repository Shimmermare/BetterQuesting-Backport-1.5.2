package betterquesting.backport;

import net.minecraft.entity.player.EntityPlayer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Because it would be too many changes to replace player UUIDs with string usernames, and thus risking bugs,
 * here is a stable String to UUID5 mapper.
 */
public final class ProfileMapper {
    private static final byte[] BQ_NAMESPACE = createNamespace();

    private static final Map<String, UUID> nameToUuid = new ConcurrentHashMap<String, UUID>();

    private ProfileMapper() {
    }

    public static UUID getUuid(EntityPlayer player) {
        return getUuid(player.username);
    }

    public static UUID getUuid(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Namespace and name must not be null");
        }
        UUID uuid = nameToUuid.get(username);
        if (uuid == null) {
            uuid = generateUuidV5(username);
            nameToUuid.put(username, uuid);
        }
        return uuid;
    }

    private static byte[] createNamespace() {
        UUID uuid = UUID.fromString("3781ffbf-5785-43a1-b2a3-6b4b4b9b1412");
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    private static UUID generateUuidV5(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(BQ_NAMESPACE);

            try {
                md.update(name.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding not supported", e);
            }

            byte[] hash = md.digest();

            // Set version 5: 0101 (SHA-1)
            hash[6] = (byte) ((hash[6] & 0x0f) | 0x50);
            // Set variant: 10 (RFC 4122)
            hash[8] = (byte) ((hash[8] & 0x3f) | 0x80);

            // UUID from first 16 bytes
            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++) {
                msb = (msb << 8) | (hash[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                lsb = (lsb << 8) | (hash[i] & 0xff);
            }

            return new UUID(msb, lsb);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not supported", e);
        }
    }
}
