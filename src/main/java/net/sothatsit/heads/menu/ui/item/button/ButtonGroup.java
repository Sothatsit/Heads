package net.sothatsit.heads.menu.ui.item.button;

import net.sothatsit.heads.util.Checks;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup {

    private final List<SelectableButton> buttons = new ArrayList<>();

    public void addButton(SelectableButton button) {
        Checks.ensureNonNull(button, "button");

        buttons.add(button);
    }

    public int getFirstSelectedIndex() {
        for(int index = 0; index < buttons.size(); index++) {
            if(buttons.get(index).isSelected())
                return index;
        }
        return -1;
    }

    public void select(int index) {
        if(index >= 0 && index < buttons.size()) {
            buttons.get(index).setSelected(true);
        } else {
            unselectAll();
        }
    }

    public void unselectAll() {
        for(SelectableButton button : buttons) {
            button.setSelected(false);
        }
    }

}
