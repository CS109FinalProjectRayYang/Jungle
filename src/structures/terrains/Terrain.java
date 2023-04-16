package structures.terrains;

/**
 * 地形父类
 * <p></p>
 * {@code @Author} : RayOvO
 */
public class Terrain {
    private String name;
    private int team;
    private int id;
    public Terrain(String name, int id, int team) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getTeam() {
        return team;
    }
}
