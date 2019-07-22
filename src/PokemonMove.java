import java.io.Serializable;
import java.util.Random;

public class PokemonMove implements Serializable {
    private int minimumDamage;
    private int maximumDamage;
    private double critChance;
    private String type;
    private String name;

    public PokemonMove(String name, String type, int minimumDamage, int maximumDamage, double critChance){
        this.setName(name);
        this.setType(type);
        this.setMinimumDamage(minimumDamage);
        this.setMaximumDamage(maximumDamage);
        this.setCritChance(critChance);
    }

    public int getAttack(){
        Random rand = new Random();
        int damage = rand.ints(this.getMinimumDamage(), this.getMaximumDamage()).findFirst().getAsInt();
        int crit = rand.nextInt(99);
        if (crit <= (critChance*100)){
            //we crit
            damage *= 1.5; //50% more damage;
        }
        //finally check types
        return damage;
    }

    public int getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(int minimumDamage) {
        this.minimumDamage = minimumDamage;
    }

    public int getMaximumDamage() {
        return maximumDamage;
    }

    public void setMaximumDamage(int maximumDamage) {
        this.maximumDamage = maximumDamage;
    }

    public double getCritChance() {
        return critChance;
    }

    public void setCritChance(double critChance) {
        this.critChance = critChance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
