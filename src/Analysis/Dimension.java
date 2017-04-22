package Analysis;

public class Dimension {
    private String name;
    private double baseRating;

    public Dimension(String name, double baseRating) {
        this.name = name;
        this.baseRating = baseRating;
    }

    public String getName() {
        return name;
    }

    public double getBaseRating() {
        return baseRating;
    }
}
