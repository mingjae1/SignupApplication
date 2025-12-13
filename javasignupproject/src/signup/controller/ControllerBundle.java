package signup.controller;

public class ControllerBundle {
    public final CSearch search;
    public final CRegister register;
    public final CPreRegister preRegister;
    public final CSchedule schedule;
    public final CAdmin admin;

    public ControllerBundle(CSearch search, CRegister register, CPreRegister preRegister, CSchedule schedule, CAdmin admin) {
        this.search = search;
        this.register = register;
        this.preRegister = preRegister;
        this.schedule = schedule;
        this.admin = admin;
    }
}