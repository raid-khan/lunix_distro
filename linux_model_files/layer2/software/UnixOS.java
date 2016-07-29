package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer2.computer.Computer;

public class UnixOS extends OperatingSystem {

    private Data varSlashWww = new Data("varSlashWww",getFileSystem(),false);
    private Data etc = new Data("etc",getFileSystem(),false);

    public UnixOS(String name, Computer superAsset, double probabilityOfCWE119) {
        super(name, superAsset);
        getFileSystem().addBody(varSlashWww);
        getFileSystem().addBody(etc);
    }
}
