package ru.papapers.optimalchoice.ui.views.subjects;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.papapers.optimalchoice.ui.data.SamplePerson;
import ru.papapers.optimalchoice.ui.services.SamplePersonService;

@PageTitle("Subjects")
@Route("subjects")
@Menu(order = 3, icon = "line-awesome/svg/pencil-ruler-solid.svg")
@Uses(Icon.class)
public class SubjectsView extends Composite<VerticalLayout> {

    public SubjectsView() {
        H2 h2 = new H2();
        TextField textField = new TextField();
        Button buttonPrimary = new Button();
        Grid basicGrid = new Grid(SamplePerson.class);
        Button buttonSecondary = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        h2.setText("Какие объекты сравниваем?");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");
        textField.setLabel(
                "Добавьте наименования объектов, которые планируете сравнивать. Например, \"Невский, 31\", \"Ленина, 19/17\"");
        textField.setWidth("100%");
        buttonPrimary.setText("Добавить");
        buttonPrimary.setWidth("100%");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        basicGrid.setWidth("100%");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid);
        buttonSecondary.setText("Далее");
        getContent().setAlignSelf(FlexComponent.Alignment.END, buttonSecondary);
        buttonSecondary.setWidth("min-content");
        getContent().add(h2);
        getContent().add(textField);
        getContent().add(buttonPrimary);
        getContent().add(basicGrid);
        getContent().add(buttonSecondary);
    }

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    @Autowired()
    private SamplePersonService samplePersonService;
}
