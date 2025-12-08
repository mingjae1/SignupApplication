package signup.controller;

import java.util.List;

import signup.constants.StatusConstants;
import signup.model.MLecture;
import signup.model.MMain;
import signup.dao.SaveDAO;
import signup.view.VRegister;

public class CRegister extends CListController {

    private VRegister vRegister;

    public CRegister(VRegister vRegister, MMain mMain, SaveDAO saveDAO) {
        super(vRegister, vRegister.getTable(), mMain, saveDAO, StatusConstants.REGISTER);
        this.vRegister = vRegister;
        this.vRegister.getCancelButton().addActionListener(this::handleDelete);
    }

    @Override
    protected void updateViewTable(List<MLecture> data) {
        this.vRegister.updateTable(data);
    }
}