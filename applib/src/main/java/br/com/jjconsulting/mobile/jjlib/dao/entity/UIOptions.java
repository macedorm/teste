package br.com.jjconsulting.mobile.jjlib.dao.entity;

public class UIOptions {
    private UIGrid grid;
    private UIForm form;
    private GridToolBarActions toolBarActions;
    private GridActions gridActions;

    public UIOptions()
    {
        toolBarActions = new GridToolBarActions();
        gridActions = new GridActions();
        grid = new UIGrid();
        form = new UIForm();
    }


    public UIGrid getGrid() {
        return grid;
    }

    public void setGrid(UIGrid grid) {
        this.grid = grid;
    }

    public UIForm getForm() {
        return form;
    }

    public void setForm(UIForm form) {
        this.form = form;
    }

    public GridToolBarActions getToolBarActions() {
        return toolBarActions;
    }

    public void setToolBarActions(GridToolBarActions toolBarActions) {
        this.toolBarActions = toolBarActions;
    }

    public GridActions getGridActions() {
        return gridActions;
    }

    public void setGridActions(GridActions gridActions) {
        this.gridActions = gridActions;
    }
}