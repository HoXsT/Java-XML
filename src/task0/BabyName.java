package task0;

public class BabyName implements Comparable<BabyName> {
    private String name;
    private String gender;
    private String ethnicity;
    private int count;
    private int rating;

    public BabyName(String name, String gender, String ethnicity, int count, int rating) {
        this.name = name;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.count = count;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getEthnicity() { return ethnicity; }
    public int getCount() { return count; }
    public int getRating() { return rating; }

    // Сортування по збільшенню номеру в рейтингу (1, 2, 3...)
    @Override
    public int compareTo(BabyName o) {
        return Integer.compare(this.rating, o.rating);
    }
}