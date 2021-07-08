package manaki.plugin.skybattle.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class WorldTemplate {

    private final String name;
    private final long seed;
    private final World.Environment environment;
    private final WorldType type;
    private final String generator;

    public WorldTemplate(String name, long seed, World.Environment environment, WorldType type, String generator) {
        this.name = name;
        this.seed = seed;
        this.environment = environment;
        this.type = type;
        this.generator = generator;
    }

    public WorldTemplate(String name) {
        this.name = name;
        this.seed = 0;
        this.environment = null;
        this.type = null;
        this.generator = null;
    }

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public WorldType getType() {
        return type;
    }

    public String getGenerator() {
        return generator;
    }

    public WorldCreator asWorldCreator(String name) {
        WorldCreator creator = new WorldCreator(name);
        if (this.type != null) {
            creator.type(this.type);
        }
        if (this.environment != null) {
            creator.environment(this.environment);
        }
        if (this.seed != 0L) {
            creator.seed(this.seed);
        }

        if (this.generator != null && !this.generator.trim().isEmpty()) {
            creator.generator(this.generator);
        }

        return creator;
    }

}
