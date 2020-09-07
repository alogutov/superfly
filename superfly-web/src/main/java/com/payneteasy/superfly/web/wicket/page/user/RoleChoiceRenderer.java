package com.payneteasy.superfly.web.wicket.page.user;

import com.payneteasy.superfly.model.ui.role.UIRoleForCheckbox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class RoleChoiceRenderer extends ChoiceRenderer<UIRoleForCheckbox> {

    public Object getDisplayValue(UIRoleForCheckbox object) {
    return (object!=null) ? object.getRoleName(): "-- Please select --";
    }

    public String getIdValue(UIRoleForCheckbox object, int arg1) {
        return object!=null ? String.valueOf(object.getId()): null;
    }

}
