package com.zad8_bd;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class CheckComboBoxItem<T> {
    private CheckBox checkbox;
    T item;
    private boolean checked;
    CheckComboBoxItem(T item, boolean checked) {
        this.item = item;
        this.checked = checked;
    }
    @Override
    public String toString() {
        return "CheckComboBoxItem<T> { " + item + ", " + checked + " }";
    }
    public boolean checked() {
        return checked;
    }
    public void setChecked(boolean b) {
        this.checked = b;
        if (checkbox != null) {
            checkbox.setSelected(b);
        }
    }
    public void setCheckboxDisable(boolean b) {
        if (checkbox != null) {
            checkbox.setDisable(b);
        }
    }
    public void setCheckbox(CheckBox b) {
        this.checkbox = b;
        this.checkbox.setSelected(checked);
    }
}

public class CheckComboBox<T> extends VBox {
    public Function<T, Void> onCheckPress = null;
    ArrayList<CheckComboBoxItem<T>> items = new ArrayList<>();
    boolean visible = false;
    Button stateBtn = new Button("");

    CheckComboBox(List<T> l) {
        for (T t : l) {
            items.add(new CheckComboBoxItem<>(t, false));
        }
        stateBtn.setOnMouseClicked(_ -> {
            visible = !visible;
            getChildren().clear();
            getChildren().add(stateBtn);
            if (visible) {
                var checkboxes = makeCheckBoxes();
                getChildren().addAll(checkboxes);
            }
            refreshStateBtn();
            onCheckPress.apply(null);
        });
        refreshStateBtn();
        getChildren().add(stateBtn);
    }

    private CheckComboBoxItem<T> getItem(T t) {
        for (CheckComboBoxItem<T> item : items) {
            if (item.item.equals(t)) {
                return item;
            }
        }
        throw new RuntimeException("UNREACHABLE");
    }

    public void setSelected(T t) {
        getItem(t).setChecked(true);
    }

    private void refreshStateBtn() {
        stateBtn.setText(visible ? "^" : "⌵");
    }

    public ArrayList<T> getAllSelected() {
        return items.stream()
                .filter(CheckComboBoxItem::checked)
                .map(i -> i.item)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<CheckBox> makeCheckBoxes() {
        ArrayList<CheckBox> c = new ArrayList<>();
        for (var item : items) {
            var box = new CheckBox();
            item.setCheckbox(box);
            box.setOnMouseClicked(_ -> {
                item.setChecked(!item.checked());
                if (onCheckPress != null) {
                    onCheckPress.apply(item.item);
                }
            });
            box.setText(item.item.toString());
            c.add(box);
        }
        return c;
    }

    ArrayList<CheckComboBoxItem<T>> getItems() {
        return items;
    }
}
