package org.xenoserver.control;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Create a virtual block device.
 */
public class CommandVbdCreate extends Command {
    /** Virtual disk to map to. */
    private VirtualDisk vd;
    /** Domain to create VBD for. */
    private int domain_id;
    /** VBD number to use. */
    private int vbd_num;
    /** Access mode to grant. */
    private Mode mode;

    /**
     * Constructor for CommandVbdCreate.
     * @param vd VirtualDisk to map to.
     * @param domain_id Domain to map for.
     * @param vbd_num VBD number within domain.
     * @param mode Access mode to grant.
     */
    public CommandVbdCreate(
        VirtualDisk vd,
        int domain_id,
        int vbd_num,
        Mode mode) {
        this.vd = vd;
        this.domain_id = domain_id;
        this.vbd_num = vbd_num;
        this.mode = mode;
    }

    /**
     * @see org.xenoserver.control.Command#execute()
     */
    public String execute() throws CommandFailedException {
        VirtualBlockDevice vbd;

        vbd =
            VirtualDiskManager.IT.createVirtualBlockDevice(
                vd,
                domain_id,
                vbd_num,
                mode);
        String command = vd.dumpForXen(vbd);

        try {
            FileWriter fw = new FileWriter("/proc/xeno/dom0/vhd");
            fw.write(command);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            throw new CommandFailedException("Could not write VBD details to /proc/xeno/dom0/vhd", e);
        }

        return "Created virtual block device "
            + vbd_num
            + " for domain "
            + domain_id;
    }
}
