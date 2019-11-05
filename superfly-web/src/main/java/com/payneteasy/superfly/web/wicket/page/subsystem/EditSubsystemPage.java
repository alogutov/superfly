package com.payneteasy.superfly.web.wicket.page.subsystem;

import com.payneteasy.superfly.model.ui.smtp_server.UISmtpServerForFilter;
import com.payneteasy.superfly.model.ui.subsystem.UISubsystem;
import com.payneteasy.superfly.service.SmtpServerService;
import com.payneteasy.superfly.service.SubsystemService;
import com.payneteasy.superfly.web.wicket.component.field.LabelCheckBoxRow;
import com.payneteasy.superfly.web.wicket.component.field.LabelDropDownChoiceRow;
import com.payneteasy.superfly.web.wicket.component.field.LabelTextFieldRow;
import com.payneteasy.superfly.web.wicket.page.BasePage;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.UrlValidator;
import org.springframework.security.access.annotation.Secured;

import java.util.List;
import java.util.UUID;

@Secured("ROLE_ADMIN")
public class EditSubsystemPage extends BasePage {
    @SpringBean
    private SubsystemService  subsystemService;
    @SpringBean
    private SmtpServerService smtpServerService;

    public EditSubsystemPage(PageParameters parameters) {
        super(ListSubsystemsPage.class, parameters);

        long              subsystemId = parameters.get("id").toLong(-1L);
        final UISubsystem subsystem   = subsystemService.getSubsystem(subsystemId);

        Form<UISubsystem> form = new Form<UISubsystem>("form", new CompoundPropertyModel<>(subsystem)) {

            @Override
            protected void onSubmit() {
                subsystemService.updateSubsystem(subsystem);
                setResponsePage(ListSubsystemsPage.class);
            }

        };
        add(form);

        form.add(new LabelTextFieldRow<UISubsystem>(subsystem, "name", "subsystem.edit.name", true));
        form.add(new LabelTextFieldRow<UISubsystem>(subsystem, "title", "subsystem.edit.title", true));

        LabelTextFieldRow<String> callbackUrlRow = new LabelTextFieldRow<>(subsystem, "callbackUrl",
                "subsystem.edit.callback", true);
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        callbackUrlRow.getTextField().add(urlValidator);
        form.add(callbackUrlRow);

        form.add(new LabelCheckBoxRow("sendCallbacks", subsystem, "subsystem.edit.send-callbacks"));

        // Subsystem token fields
        final Label labelSubsystemToken = new Label("subsystemToken", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return subsystem.getSubsystemToken();
            }
        });
        labelSubsystemToken.setOutputMarkupId(true);

        form.add(new Label("subsystemTokenLabel", new ResourceModel("subsystem.edit.subsystemToken")));
        form.add(labelSubsystemToken);
        form.add(new IndicatingAjaxLink<String>("generateNewToken") {
            private static final long serialVersionUID = 1L;

            public void onClick(AjaxRequestTarget aTarget) {
                subsystem.setSubsystemToken(generateNewToken());
                aTarget.add(labelSubsystemToken);
            }
        });

        LabelTextFieldRow<String> subsystemUrlRow = new LabelTextFieldRow<>(subsystem, "subsystemUrl",
                "subsystem.edit.subsystemUrl", true);
        subsystemUrlRow.getTextField().add(urlValidator);
        form.add(subsystemUrlRow);

        LabelTextFieldRow<String> landingUrlRow = new LabelTextFieldRow<>(subsystem, "landingUrl",
                "subsystem.edit.landingUrl", true);
        landingUrlRow.getTextField().add(urlValidator);
        form.add(landingUrlRow);

        LabelTextFieldRow<String> loginFormCssUrlRow = new LabelTextFieldRow<>(subsystem, "loginFormCssUrl",
                "subsystem.edit.loginFormCssUrl");
        loginFormCssUrlRow.getTextField().add(urlValidator);
        form.add(loginFormCssUrlRow);

        form.add(new LabelCheckBoxRow("allowListUsers", subsystem, "subsystem.edit.allow-list-users"));

        IModel<List<UISmtpServerForFilter>> smtpServersModel = new LoadableDetachableModel<List<UISmtpServerForFilter>>() {
            @Override
            protected List<UISmtpServerForFilter> load() {
                return smtpServerService.getSmtpServersForFilter();
            }
        };
        form.add(new LabelDropDownChoiceRow<>("smtpServer", subsystem, "subsystem.smtpServer",
                smtpServersModel, new ChoiceRenderer<UISmtpServerForFilter>() {
            public Object getDisplayValue(UISmtpServerForFilter server) {
                return server == null ? "" : server.getName();
            }

            public String getIdValue(UISmtpServerForFilter server, int index) {
                return server == null ? "" : String.valueOf(server.getId());
            }
        }, true));

        form.add(new SubmitLink("form-submit"));
        form.add(new BookmarkablePageLink<Page>("cancel", ListSubsystemsPage.class));
    }

    @Override
    protected String getTitle() {
        return "Edit subsystem";
    }

    private String generateNewToken() {
        return UUID.randomUUID().toString();
    }
}
