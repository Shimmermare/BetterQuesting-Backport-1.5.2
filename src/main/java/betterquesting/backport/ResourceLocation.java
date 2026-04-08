package betterquesting.backport;

public class ResourceLocation {
    private final String resourceDomain;
    private final String resourcePath;

    private String cachedTexturePath;

    public ResourceLocation(String namespace, String path) {
        if (path == null) {
            throw new NullPointerException("ID cannot be null");
        }

        if (namespace != null && !namespace.isEmpty()) {
            this.resourceDomain = namespace;
        } else {
            this.resourceDomain = "minecraft";
        }

        this.resourcePath = path;
    }

    public ResourceLocation(String namespacedPath) {
        String namespace = "minecraft";
        String id = namespacedPath;
        int colonIndex = id.indexOf(':');

        if (colonIndex >= 0) {
            id = namespacedPath.substring(colonIndex + 1);

            if (colonIndex > 1) {
                namespace = namespacedPath.substring(0, colonIndex);
            }
        }

        this.resourceDomain = namespace.toLowerCase();
        this.resourcePath = id;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public String getResourceDomain() {
        return this.resourceDomain;
    }

    public String getTexturePath() {
        if (cachedTexturePath == null) {
            cachedTexturePath = "/mods/" + resourceDomain + "/" + resourcePath;
        }
        return cachedTexturePath;
    }

    public String toString() {
        return this.resourceDomain + ":" + this.resourcePath;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof ResourceLocation)) {
            return false;
        } else {
            ResourceLocation var2 = (ResourceLocation) other;
            return this.resourceDomain.equals(var2.resourceDomain) && this.resourcePath.equals(var2.resourcePath);
        }
    }

    public int hashCode() {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
