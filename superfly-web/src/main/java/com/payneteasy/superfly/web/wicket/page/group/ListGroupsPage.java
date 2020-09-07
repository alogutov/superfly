package com.payneteasy.superfly.web.wicket.page.group;

import com.payneteasy.superfly.model.RoutineResult;
import com.payneteasy.superfly.model.ui.group.UIGroupForList;
import com.payneteasy.superfly.model.ui.subsystem.UISubsystemForFilter;
import com.payneteasy.superfly.service.GroupService;
import com.payneteasy.superfly.service.SubsystemService;
import com.payneteasy.superfly.web.wicket.component.ConfirmPanel;
import com.payneteasy.superfly.web.wicket.component.PagingDataView;
import com.payneteasy.superfly.web.wicket.component.SubsystemChoiceRenderer;
import com.payneteasy.superfly.web.wicket.component.paging.SuperflyPagingNavigator;
import com.payneteasy.superfly.web.wicket.model.StickyFilters;
import com.payneteasy.superfly.web.wicket.page.BasePage;
import com.payneteasy.superfly.web.wicket.page.group.wizard.GroupPropertiesPage;
import com.payneteasy.superfly.web.wicket.repeater.IndexedSortableDataProvider;
import com.payneteasy.superfly.web.wicket.utils.PageParametersBuilder;
import com.payneteasy.superfly.web.wicket.utils.WicketComponentHelper;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.access.annotation.Secured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Secured("ROLE_ADMIN")
public class ListGroupsPage extends BasePage {
    @SpringBean
    private GroupService groupService;
    @SpringBean
    private SubsystemService ssysService;

    @SuppressWarnings({ "unchecked", "serial" })
    public ListGroupsPage() {
        super(ListGroupsPage.class);

        // CONFIRM PANEL
        add(new EmptyPanel("confirmPanel"));

        // FILTER
        final StickyFilters stickyFilters = getSession().getStickyFilters();
        Form<GroupFilter> filtersForm = new Form<GroupFilter>("filters-form");
        add(filtersForm);
        DropDownChoice<UISubsystemForFilter> subsystemDropdown = new DropDownChoice<UISubsystemForFilter>(
                "subsystem-filter"
                , new PropertyModel<UISubsystemForFilter>(stickyFilters, "subsystem")
                , ssysService.getSubsystemsForFilter()
                , new SubsystemChoiceRenderer()
        );
        subsystemDropdown.setNullValid(true);
        filtersForm.add(subsystemDropdown);

        final List<Long> subsystemIds = new ArrayList<Long>();
        if (stickyFilters.getSubsystem() != null) subsystemIds.add(stickyFilters.getSubsystem().getId());

        // SORTABLE DATA PROVIDER
        String[] fieldName = { "groupId", "groupName", "groupSubsystemId", "groupSubsystem"};
        final SortableDataProvider<UIGroupForList, String> groupDataProvider = new IndexedSortableDataProvider<UIGroupForList>(fieldName) {

            public Iterator<? extends UIGroupForList> iterator(long first, long count) {
                UISubsystemForFilter subsystem = stickyFilters.getSubsystem();
                List<Long> subsystemId = new ArrayList<Long>();
                if (subsystem == null) {
                    List<UIGroupForList> list = groupService.getGroupsForSubsystems(first, count, getSortFieldIndex(), isAscending(), null, subsystem == null ? null : null);
                    return list.iterator();
                } else {
                    subsystemId.add(subsystem.getId());
                    List<UIGroupForList> list = groupService.getGroupsForSubsystems(first, count, getSortFieldIndex(), isAscending(), null, subsystem == null ? null : subsystemId);
                    return list.iterator();
                }
            }

            public long size() {
                UISubsystemForFilter subsystem = stickyFilters.getSubsystem();
                List<Long> subsystemId = new ArrayList<Long>();
                if(subsystem==null){
                    return groupService.getGroupsCount(null, subsystem == null ? null : null);
                }else{
                    subsystemId.add(subsystem.getId());
                    return groupService.getGroupsCount(null, subsystem == null ? null : subsystemId);
                }
            }

        };

//        DATAVIEW
        final CheckGroup<UIGroupForList> checkGroup = new CheckGroup<UIGroupForList>("group", new ArrayList<UIGroupForList>());

        final DataView<UIGroupForList> groupDataView = new PagingDataView<UIGroupForList>("list-group",groupDataProvider){
            @Override
            protected void populateItem(Item<UIGroupForList> item) {
                final UIGroupForList group = item.getModelObject();
                item.add(new Check<UIGroupForList>("selected", item.getModel(), checkGroup));

                item.add(new Label("group-name",group.getName()));
                item.add(new Label("group-ssys",group.getSubsystemName()));
                item.add(new Link("group-delete"){

                    @Override
                    public void onClick() {
                        List<Long> groupToDelete = new ArrayList<Long>();
                        groupToDelete.add(group.getId());
                        doDelete(groupToDelete);

                    }
                });

//                ACTIONS
                item.add(new BookmarkablePageLink("group-edit", EditGroupPage.class, PageParametersBuilder.fromPair("gid", group.getId())));
                item.add(new BookmarkablePageLink("group-actions", ChangeGroupActionsPage.class, PageParametersBuilder.fromPair("gid",group.getId())));
                item.add(new BookmarkablePageLink("group-clone", CloneGroupPage.class, PageParametersBuilder.fromPair("sid",group.getId())));

                WicketComponentHelper.clickTableRow(item, ViewGroupPage.class, PageParametersBuilder.fromPair("gid", group.getId()), this);
            }

        };

        Form form = new Form("form") {

            @Override
            protected void onSubmit() {
                List<Long> gIds = new ArrayList<Long>();
                for(UIGroupForList e : checkGroup.getModelObject())gIds.add(e.getId());
                doDelete(gIds);
            }

        };

        checkGroup.add(groupDataView);
        checkGroup.add(new OrderByLink("order-by-groupName", "groupName", groupDataProvider));
        checkGroup.add(new OrderByLink("order-by-subsystemName", "groupSubsystem", groupDataProvider));
        //checkGroup.add(new PagingNavigator("paging-navigator", groupDataView));
        checkGroup.add(new SuperflyPagingNavigator("paging-navigator", groupDataView));

        checkGroup.add(new CheckGroupSelector("groupselector"));
        checkGroup.add(new Button("add-group"){
            @Override
            public void onSubmit() {
                setResponsePage(GroupPropertiesPage.class);
            }

        }.setDefaultFormProcessing(false));

        form.add(checkGroup);
        add(form);
    }


    /**
     * Deletes listed groups from DB
     * @param groupIds - ID's list of groups to delete
     */
    private void doDelete(final List<Long> groupIds){

        if (groupIds.size() == 0)    return;

        getPage().get("confirmPanel").replaceWith(
            new ConfirmPanel("confirmPanel",
                    "You are about to delete "+ groupIds.size()
                            + " group(s) permanently?") {
                private static final long serialVersionUID = 1L;

                public void onConfirm() {
                    boolean someSuccess = false;
                    for (Long ui : groupIds) {
                        RoutineResult result = groupService.deleteGroup(ui);
                        if (result.isOk()) {
                            someSuccess = true;
                        }
                    }
                    if (someSuccess) {
                        info("Deleted groups; please be aware that some sessions could be invalidated");
                    }
                    setResponsePage(ListGroupsPage.class);
                }
            });
    }

    @Override
    protected String getTitle() {
        return "Groups";
    }

    private class GroupFilter implements Serializable {
        private static final long serialVersionUID = 1L;
    }

}