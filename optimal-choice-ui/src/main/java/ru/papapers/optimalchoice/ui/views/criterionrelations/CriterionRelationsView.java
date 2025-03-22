package ru.papapers.optimalchoice.ui.views.criterionrelations;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import ru.papapers.optimalchoice.api.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.api.domain.Estimation;
import ru.papapers.optimalchoice.ui.services.CriterionRelationService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@PageTitle("Сравнение критериев")
@Route("purpose/:purposeId/criterion-relations")
@Menu(order = 2, icon = "line-awesome/svg/pencil-ruler-solid.svg")
public class CriterionRelationsView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    private final CriterionRelationService criterionRelationService;
    private final VerticalLayout verticalLayout;

    private String purposeId;

    public CriterionRelationsView(CriterionRelationService criterionRelationService) {
        this.criterionRelationService = criterionRelationService;

        H2 h2 = new H2();
        h2.setText("Сравнение параметров");
        h2.setWidth("max-content");
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);

        verticalLayout = new VerticalLayout();
        verticalLayout.getThemeList().add("spacing-xs");

        Button nextViewButton = new Button();
        nextViewButton.setText("Далее");
        getContent().setAlignSelf(FlexComponent.Alignment.END, nextViewButton);
        nextViewButton.setWidth("min-content");

        getContent().add(h2);
        getContent().add(getTextArea());
        getContent().add(verticalLayout);
        getContent().add(nextViewButton);
    }

    private HorizontalLayout getRelationLayoutRow(CriterionRelationDto relation) {
        HorizontalLayout relationLayoutRow = new HorizontalLayout();
        relationLayoutRow.setPadding(true);
        relationLayoutRow.setWidth("100%");
        relationLayoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        relationLayoutRow.setAlignItems(FlexComponent.Alignment.END);
        relationLayoutRow.add(getRelationRadioButtonGroup(relation.getCriterion().getName(),
                relation.getComparingCriterion().getName()), getEstimationSelect());
        relationLayoutRow.getStyle().setBorder("0.5px dashed gray");
        relationLayoutRow.setMargin(false);

        return relationLayoutRow;
    }

    private RadioButtonGroup<String> getRelationRadioButtonGroup(String criterionName, String comparingCriterionName) {
        RadioButtonGroup<String> relationRadioButtonGroup = new RadioButtonGroup<>();
        relationRadioButtonGroup.setItems(criterionName, comparingCriterionName);
        relationRadioButtonGroup.setWidth("100%");

        return relationRadioButtonGroup;
    }

    private Select<String> getEstimationSelect() {
        Select<String> select = new Select<>();
        select.setLabel("Оценка");
        select.setItems(Arrays.stream(Estimation.values())
                .map(Estimation::getDirectValue)
                .map(BigDecimal::toString)
                .collect(Collectors.toList()));
        select.setValue(Estimation.ONE.getDirectValue().toString());
        select.setWidth("100px");

        return select;
    }

    private TextArea getTextArea() {
        TextArea readonlyArea = new TextArea();
        readonlyArea.setReadOnly(true);
        readonlyArea.setValue("Необходимо сравнить параметры друг с другом по шкале от 1 до 9, где:\n" +
                "1 балл: критерии одинаково важны;\n" +
                "5 баллов: один критерий заметно превосходит другой;\n" +
                "9 баллов: один из критериев обладает значительным преимуществом над другим.");
        readonlyArea.setWidth("100%");
        return readonlyArea;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        purposeId = event.getRouteParameters().get("purposeId")
                .orElseThrow(() -> new NullPointerException("purposeId can't be NULL"));
        updateRelations(UUID.fromString(purposeId));
    }

    private void updateRelations(UUID purposeId) {
        Set<CriterionRelationDto> relations = criterionRelationService.getCriterionRelationByPurposeId(purposeId);
        for(CriterionRelationDto relationDto : relations) {
            HorizontalLayout relationLayoutRow = getRelationLayoutRow(relationDto);
            verticalLayout.add(relationLayoutRow);
        }
    }
}
