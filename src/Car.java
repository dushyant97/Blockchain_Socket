import java.util.Date;

public class Car implements java.io.Serializable{
    String name;
    long timestamp;

    public Car(){
        name = "tushar";
        timestamp = new Date().getTime();
    }

    public Car(String name, long timestamp){
        this.name = name;
        this.timestamp = timestamp;
    }

    public void properties(){
        System.out.println("Peoperty function called");
        System.out.println("The attribute is: " + name + timestamp);
    }

    @Override
    public String toString() {
        return name + " " + timestamp;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
