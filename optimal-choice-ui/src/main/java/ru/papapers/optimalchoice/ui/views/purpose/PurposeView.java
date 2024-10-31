package ru.papapers.optimalchoice.ui.views.purpose;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Цель")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/pencil-ruler-solid.svg")
public class PurposeView extends Composite<VerticalLayout> {

    public PurposeView() {
        H2 h2 = new H2();
        h2.setText("Что выбираем?");
        h2.setWidth("max-content");

        TextField purpose = new TextField();
        purpose.setLabel("Введите тип объекта. Например, если выбираете недвижимость, можно ввести \"Квартира\".");
        purpose.setWidth("100%");

        Button okButton = new Button();
        okButton.setText("ОК");
        okButton.setWidth("100%");
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        okButton.addClickShortcut(Key.ENTER);

        getContent().addClassName(Padding.LARGE);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, purpose);
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, okButton);
        getContent().add(h2);
        getContent().add(purpose);
        getContent().add(okButton);
    }
}
