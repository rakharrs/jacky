package util;

public class Button {
    String name;
    Texture texture;
    Coord2d coords;


    public Button (String name, Texture texture, int x, int y){
        setCoords(new Coord2d(x, y));
        setName(name);
        setTexture(texture);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Coord2d getCoords() {
        return coords;
    }

    public void setCoords(Coord2d coords) {
        this.coords = coords;
    }
}
