package ru.papapers.views.purpose;

import com.vaadin.flow.component.Composite;
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

@PageTitle("Purpose")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/pencil-ruler-solid.svg")
public class PurposeView extends Composite<VerticalLayout> {

    public PurposeView() {
        H2 h2 = new H2();
        TextField textField = new TextField();
        Button buttonPrimary = new Button();
        getContent().addClassName(Padding.LARGE);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h2.setText("Что выбираем?");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");
        textField.setLabel("Введите тип объекта. Например, если выбираете недвижимость, можно ввести \"Квартира\".");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, textField);
        textField.setWidth("100%");
        buttonPrimary.setText("ОК");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary);
        buttonPrimary.setWidth("100%");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(h2);
        getContent().add(textField);
        getContent().add(buttonPrimary);
    }
}
