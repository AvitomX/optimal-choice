package ru.papapers.optimalchoice.ui.views.criterion;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import ru.papapers.optimalchoice.api.domain.CriterionDto;

@Slf4j
public class CriterionForm extends FormLayout {

    Binder<CriterionDto> binder = new BeanValidationBinder<>(CriterionDto.class);
    private CriterionDto criterionDto;

    TextField name = new TextField("Добавьте параметры (критерии) объекта.");
    Button addButton = new Button("Добавить");
    Button deleteButton = new Button("Удалить");

    public CriterionForm(CriterionDto criterionDto) {
        this.criterionDto = criterionDto;

        addClassName("criterion-form");
        binder.bindInstanceFields(this);
        binder.forField(name)
                .asRequired("Должен быть хотя бы один символ")
                .bind(CriterionDto::getName, CriterionDto::setName);

        name.setWidth("100%");
        name.setClearButtonVisible(true);
        name.setSizeFull();

        addButton.setWidth("100%");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickShortcut(Key.ENTER);
        addButton.setSizeFull();
        addButton.addClickListener(event -> validateAndSave());

        deleteButton.setWidth("100%");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickShortcut(Key.BACKSPACE);
        deleteButton.setSizeFull();
        deleteButton.addClickListener(event -> delete());

        add(name, addButton, deleteButton);
        setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("500px", 2));
        setColspan(name, 2);
    }

    private void delete() {
        if (criterionDto != null) {
            fireEvent(new DeleteEvent(this, criterionDto));
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(criterionDto);
            fireEvent(new SaveEvent(this, criterionDto));
        } catch (ValidationException e) {
            log.error("Ошибка валидации. {}. {}", criterionDto, e.getValidationErrors());
        }
    }

    public void setCriterionDto(CriterionDto criterionDto) {
        this.criterionDto = criterionDto;
        binder.readBean(criterionDto);
    }

    // Events
    public static abstract class CriterionFormEvent extends ComponentEvent<CriterionForm> {
        private final CriterionDto criterionDto;

        protected CriterionFormEvent(CriterionForm source, CriterionDto criterionDto) {
            super(source, false);
            this.criterionDto = criterionDto;
        }

        public CriterionDto getCriterionDto() {
            return criterionDto;
        }
    }

    public static class SaveEvent extends CriterionFormEvent {
        SaveEvent(CriterionForm source, CriterionDto criterionDto) {
            super(source, criterionDto);
        }
    }

    public static class DeleteEvent extends CriterionFormEvent {
        DeleteEvent(CriterionForm source, CriterionDto criterionDto) {
            super(source, criterionDto);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
