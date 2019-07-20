import java.io.Serializable;


//Serializable means we can turn it into bytes and send it through the wire to be
//reconstructed on the client's end.
public class Pokemon implements Serializable {

    private int id;
    private String name;
    private String type;


    public Pokemon(int id, String name, String type){
        this.setId(id);
        this.setName(name);
        this.setType(type);
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

    public void printMe(){
        System.out.println("Pokemon Name: " + this.getName() + " | Pokemon Type: " + this.getType());
    }
}
