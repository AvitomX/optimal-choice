package ru.papapers.optimalchoice.ui.views.criterion;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import ru.papapers.optimalchoice.api.domain.CriterionDto;
import ru.papapers.optimalchoice.ui.services.CriterionService;

import java.util.Set;
import java.util.UUID;

@PageTitle("Критерии")
@Route("purpose/:purposeId/criterion")
@Menu(order = 1, icon = "line-awesome/svg/pencil-ruler-solid.svg")
@Uses(Icon.class)
@Slf4j
public class CriterionView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final Grid criterionBasicGrid = new Grid(CriterionDto.class);
    CriterionForm criterionForm;

    private final CriterionService criterionService;
    private String purposeId;

    public CriterionView(CriterionService criterionService) {
        this.criterionService = criterionService;

        criterionForm = new CriterionForm(new CriterionDto());
        criterionForm.addListener(CriterionForm.SaveEvent.class, this::save);
        criterionForm.addListener(CriterionForm.DeleteEvent.class, this::delete);

        criterionBasicGrid.setWidth("100%");
        criterionBasicGrid.getStyle().set("flex-grow", "0");
        criterionBasicGrid.asSingleSelect().addValueChangeListener(event -> editCriterion((CriterionDto) event.getValue()));

        H2 h2 = new H2();
        h2.setText("Какие параметры объекта?");
        h2.setWidth("max-content");

        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);

        Paragraph textSmall = new Paragraph();
        textSmall.setText("Например, для квартиры параметрами могут быть цена, площадь, район и т.п.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");

        Button buttonSecondary = new Button();
        buttonSecondary.setText("Далее");
        getContent().setAlignSelf(FlexComponent.Alignment.END, buttonSecondary);
        buttonSecondary.setWidth("min-content");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        getContent().add(h2);
        getContent().add(criterionForm);
        getContent().add(textSmall);
        getContent().add(criterionBasicGrid);
        getContent().add(buttonSecondary);
    }

    private void save(CriterionForm.SaveEvent event) {
        UUID uuidId = UUID.fromString(purposeId);
        criterionService.add(event.getCriterionDto(), uuidId);
        updateGrid(uuidId);
    }

    private void delete(CriterionForm.DeleteEvent event) {
        UUID uuidId = UUID.fromString(purposeId);
        criterionService.delete(event.getCriterionDto(), uuidId);
        updateGrid(uuidId);
    }

    private void editCriterion(CriterionDto criterionDto) {
        if (criterionDto != null) {
            criterionForm.setCriterionDto(criterionDto);
        }
    }

    private void updateGrid(UUID purposeId) {
        criterionBasicGrid.asSingleSelect().clear();
        Set<CriterionDto> criteria = criterionService.getAllByPurpose(purposeId);
        criterionBasicGrid.setItems(criteria);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        purposeId = event.getRouteParameters().get("purposeId")
                .orElseThrow(() -> new NullPointerException("purposeId can't be NULL"));
        updateGrid(UUID.fromString(purposeId));
    }
}
