package ru.papapers.optimalchoice.ui.views.purpose;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import lombok.extern.slf4j.Slf4j;
import ru.papapers.optimalchoice.api.domain.PurposeDto;
import ru.papapers.optimalchoice.ui.services.PurposeService;
import ru.papapers.optimalchoice.ui.views.criterion.CriterionView;

import java.util.UUID;


@Slf4j
@PageTitle("Цель")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/pencil-ruler-solid.svg")
public class PurposeView extends Composite<VerticalLayout> {

    PurposeForm purposeForm;

    private final PurposeService purposeService;

    public PurposeView(PurposeService purposeService) {
        this.purposeService = purposeService;

        purposeForm = new PurposeForm();
        purposeForm.addListener(PurposeForm.SaveEvent.class, this::savePurpose);
        PurposeDto purposeDto = new PurposeDto();
        purposeDto.setId(UUID.randomUUID());
        purposeForm.setPurposeDto(purposeDto);

        H2 h2 = new H2();
        h2.setText("Что выбираем?");
        h2.setWidth("max-content");

        getContent().addClassName(Padding.LARGE);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        getContent().add(h2);
        getContent().add(purposeForm);
    }

    private void savePurpose(PurposeForm.SaveEvent event) {
        PurposeDto purposeDto = event.getPurposeDto();
        log.info(purposeDto.toString());
        PurposeDto response = purposeService.save(purposeDto);
        purposeDto.setId(response.getId());
        purposeDto.setCriterionRelations(response.getCriterionRelations());
        purposeDto.setSubjectRelations(response.getSubjectRelations());

        this.getUI().ifPresent(ui -> ui.navigate(CriterionView.class, new RouteParameters("purposeId", purposeDto.getId().toString())));
    }
}
