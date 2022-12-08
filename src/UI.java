package util;

import java.util.ArrayList;

public class UI {
    ArrayList<Button> buttons;

    public UI(ArrayList<Button> Buttons){
        setButtons(Buttons);
    }
    public UI(){
        setButtons(new ArrayList<>());

    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<Button> buttons) {
        this.buttons = buttons;
    }
}
