package com.example.application.views.traveldetail;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Travel detail")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 1)
@Route("my-view")
public class TraveldetailView extends Composite<VerticalLayout> {

    public TraveldetailView() {
        Paragraph textLarge = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        textLarge.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textLarge.setWidth("100%");
        textLarge.getStyle().set("font-size", "var(--lumo-font-size-xl)");
        getContent().add(textLarge);
    }
}
