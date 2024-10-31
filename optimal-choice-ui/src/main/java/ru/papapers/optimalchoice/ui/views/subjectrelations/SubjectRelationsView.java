package ru.papapers.optimalchoice.ui.views.subjectrelations;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Subject Relations")
@Route("subject-relations")
@Menu(order = 4, icon = "line-awesome/svg/pencil-ruler-solid.svg")
public class SubjectRelationsView extends Composite<VerticalLayout> {

    public SubjectRelationsView() {
        H2 h2 = new H2();
        Paragraph textMedium = new Paragraph();
        Paragraph textMedium2 = new Paragraph();
        Paragraph textMedium3 = new Paragraph();
        Paragraph textMedium4 = new Paragraph();
        TabSheet tabSheet = new TabSheet();
        Button buttonTertiary = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h2.setText("Сравнение объектов по параметрам");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");
        textMedium.setText(
                "Необходимо сравнить объекты друг с другом по параметрам, внесенным ранее. Оценка будет происходить по шкале от 1 до 9, где:");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium2.setText("1 балл: объекты одинаково важны;");
        textMedium2.setWidth("100%");
        textMedium2.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium3.setText("5 баллов: один объект заметно превосходит другой;");
        textMedium3.setWidth("100%");
        textMedium3.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textMedium4.setText("9 баллов: один из объект обладает значительным преимуществом над другим.");
        textMedium4.setWidth("100%");
        textMedium4.getStyle().set("font-size", "var(--lumo-font-size-m)");
        tabSheet.setWidth("100%");
        setTabSheetSampleData(tabSheet);
        buttonTertiary.setText("Далее");
        getContent().setAlignSelf(FlexComponent.Alignment.END, buttonTertiary);
        buttonTertiary.setWidth("min-content");
        buttonTertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getContent().add(h2);
        getContent().add(textMedium);
        getContent().add(textMedium2);
        getContent().add(textMedium3);
        getContent().add(textMedium4);
        getContent().add(tabSheet);
        getContent().add(buttonTertiary);
    }

    private void setTabSheetSampleData(TabSheet tabSheet) {
        tabSheet.add("Dashboard", new Div(new Text("This is the Dashboard tab content")));
        tabSheet.add("Payment", new Div(new Text("This is the Payment tab content")));
        tabSheet.add("Shipping", new Div(new Text("This is the Shipping tab content")));
    }
}
