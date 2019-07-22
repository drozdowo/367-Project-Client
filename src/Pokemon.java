import java.io.Serializable;
import java.util.ArrayList;


//Serializable means we can turn it into bytes and send it through the wire to be
//reconstructed on the client's end.
public class Pokemon implements Serializable {

    private int id;
    private String name;
    private String type;
    private ArrayList<PokemonMove> moves;
    private int hp;

    public Pokemon(int id, String name, String type, int HP, ArrayList<PokemonMove> moves){
        this.setId(id);
        this.setName(name);
        this.setType(type);
        this.setHp(HP);
        this.setMoves(moves);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<PokemonMove> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<PokemonMove> moves) {
        this.moves = moves;
    }


    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void printMe(){
        System.out.println("Pokemon Name: " + this.getName() + " | Pokemon Type: " + this.getType());
    }
}
