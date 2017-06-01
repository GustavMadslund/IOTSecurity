package Analysis;

public class Dimension {
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
