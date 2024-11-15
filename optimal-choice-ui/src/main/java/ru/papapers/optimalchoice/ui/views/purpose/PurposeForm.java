package ru.papapers.optimalchoice.ui.views.purpose;

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
import ru.papapers.optimalchoice.api.domain.PurposeDto;

@Slf4j
public class PurposeForm extends FormLayout {

    Binder<PurposeDto> binder = new BeanValidationBinder<>(PurposeDto.class);
    private PurposeDto purposeDto;

    TextField name = new TextField("Введите тип объекта. Например, если выбираете недвижимость, можно ввести \"Квартира\".");
    Button okButton = new Button("ОК");

    public PurposeForm() {
        addClassName("purpose-form");
        binder.bindInstanceFields(this);
        binder.forField(name)
                .asRequired("Должен быть хотя бы один символ")
                .bind(PurposeDto::getName, PurposeDto::setName);

        name.setWidth("100%");
        name.setClearButtonVisible(true);
        name.setSizeFull();

        okButton.setWidth("100%");
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        okButton.addClickShortcut(Key.ENTER);
        okButton.setSizeFull();
        okButton.addClickListener(event -> validateAndSave());

        add(name, okButton);
        setResponsiveSteps(new ResponsiveStep("0", 1));
    }

    private void validateAndSave() {
        try {
            binder.writeBean(purposeDto);
            fireEvent(new SaveEvent(this, purposeDto));
        } catch (ValidationException e) {
            log.error("Ошибка валидации. {}. {}", purposeDto, e.getValidationErrors());
        }
    }

    public void setPurposeDto(PurposeDto purposeDto) {
        this.purposeDto = purposeDto;
        binder.readBean(purposeDto);
    }

    // Events
    public static abstract class PurposeFormEvent extends ComponentEvent<PurposeForm> {
        private final PurposeDto purposeDto;

        protected PurposeFormEvent(PurposeForm source, PurposeDto purposeDto) {
            super(source, false);
            this.purposeDto = purposeDto;
        }

        public PurposeDto getPurposeDto() {
            return purposeDto;
        }
    }

    public static class SaveEvent extends PurposeFormEvent {
        SaveEvent(PurposeForm source, PurposeDto purposeDto) {
            super(source, purposeDto);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
