package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.BasicAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.FilterAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.InsertAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.InternalAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.LegendAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.RefreshAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.SqlCommandAction;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.UrlRedirectAction;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class GridToolBarActions {

    private InsertAction insertAction;
    private LegendAction legendAction;
    private RefreshAction refreshAction;
    private FilterAction filterAction;
    private List<SqlCommandAction> commandActions;
    private List<UrlRedirectAction> urlRedirectActions;
    private List<InternalAction> internalActions;

    public GridToolBarActions()
    {
        this.insertAction = new InsertAction();
        this.legendAction = new LegendAction();
        this.refreshAction = new RefreshAction();
        this.filterAction = new FilterAction();
        this.commandActions = new ArrayList<>();
        this.urlRedirectActions = new ArrayList<>();
        this.internalActions = new ArrayList<>();
    }


    public void add(BasicAction action) {
        if (action instanceof SqlCommandAction)
            add((SqlCommandAction) action);
        else if (action instanceof UrlRedirectAction)
            add((UrlRedirectAction) action);
        else if (action instanceof InternalAction)
            add((InternalAction) action);
        else
            throw new IllegalArgumentException("Invalid Action");
    }

    public void add(SqlCommandAction action) {
        validateAction(action);
        commandActions.add(action);
    }

    public void add(UrlRedirectAction action) {
        validateAction(action);
        urlRedirectActions.add(action);
    }

    public void add(InternalAction action) {
        validateAction(action);
        internalActions.add(action);
    }

    public void remove(SqlCommandAction action) {
        validateAction(action);
        commandActions.remove(action);
    }

    public void remove(UrlRedirectAction action) {
        validateAction(action);
        urlRedirectActions.remove(action);
    }

    public void remove(InternalAction action) {
        validateAction(action);
        internalActions.remove(action);
    }

    public void remove(BasicAction action) {
        if (action instanceof SqlCommandAction) {
            remove((SqlCommandAction) action);
        } else if (action instanceof UrlRedirectAction) {
            remove((UrlRedirectAction) action);
        } else if (action instanceof InternalAction) {
            remove((InternalAction) action);
        } else {
            throw new IllegalArgumentException("Invalid Action");
        }
    }

    public void remove(String actionName) {
        BasicAction action = get(actionName);
        remove(action);
    }

    private void validateAction(BasicAction action) {
        if (action == null)
            throw new IllegalArgumentException();

        if (TextUtils.isNullOrEmpty(action.getName()))
            throw new IllegalArgumentException("Property name action is not valid");
    }


    public BasicAction get(String name) {
        for (BasicAction action : getAll()) {
            if (action.getName().equals(name))
                return action;
        }
        return null;
    }


    public List<BasicAction> getAll()
    {
        List<BasicAction> listAction = new ArrayList<>();

        if (insertAction != null)
            listAction.add(insertAction);

        if (legendAction != null)
            listAction.add(legendAction);

        if (refreshAction != null)
            listAction.add(refreshAction);

        if (filterAction != null)
            listAction.add(filterAction);

        if (commandActions != null && commandActions.size() > 0)
            listAction.addAll(commandActions);

        if (urlRedirectActions != null && urlRedirectActions.size() > 0)
            listAction.addAll(urlRedirectActions);

        if (internalActions != null && internalActions.size() > 0)
            listAction.addAll(internalActions);

        return listAction;
    }

    public int size() {
        return getAll().size();
    }

}
