package Analysis;

public class Dimension {
    // The rating scale for impact/probability: [0; SCALE]
    public static final double SCALE = 3;

    private String name;
    private double baseImpact;
    private double baseProbability;

    public Dimension(String name, double baseImpact, double baseProbability) {
        this.name = name;
        this.baseImpact = baseImpact;
        this.baseProbability = baseProbability;
    }

    public String getName() {
        return name;
    }

    public double getBaseImpact() {
        return baseImpact;
    }

    public double getBaseProbability() {
        return baseProbability;
    }
}
