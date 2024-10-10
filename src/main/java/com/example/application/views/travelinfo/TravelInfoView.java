package com.example.application.views.travelinfo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Travel Info")
@Menu(icon = "line-awesome/svg/user.svg", order = 0)
@Route("")
public class TravelInfoView extends Composite<VerticalLayout> {

    public TravelInfoView() {
        TextField textField = new TextField();
        ComboBox comboBox = new ComboBox();
        ComboBox comboBox2 = new ComboBox();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        TextField textField4 = new TextField();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        textField.setLabel("travelers");
        textField.setWidth("100%");
        comboBox.setLabel("pet");
        comboBox.setWidth("100%");
        setComboBoxSampleData(comboBox);
        comboBox2.setLabel("Children");
        comboBox2.setWidth("100%");
        setComboBoxSampleData(comboBox2);
        textField2.setLabel("depature from");
        textField2.setWidth("100%");
        textField3.setLabel("duration days");
        textField3.setWidth("100%");
        textField4.setLabel("where");
        textField4.setWidth("100%");
        getContent().add(textField);
        getContent().add(comboBox);
        getContent().add(comboBox2);
        getContent().add(textField2);
        getContent().add(textField3);
        getContent().add(textField4);
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
    }
}
