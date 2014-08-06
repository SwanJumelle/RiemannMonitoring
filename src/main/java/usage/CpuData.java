package usage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:21:35
 */
public class CpuData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4851789306230004495L;

	/**
     * @param coreId
     * @param guest
     * @param idle
     * @param iowait
     * @param irq
     * @param nice
     * @param softirq
     * @param steal
     * @param sysmode
     * @param user
     */
    public CpuData(int coreId, long user, long nice, long sysmode, long idle,
            long iowait, long irq, int softirq, long steal, long guest) {
        this.coreId = coreId;
        this.user = user;
        this.nice = nice;
        this.sysmode = sysmode;
        this.idle = idle;
        this.iowait = iowait;
        this.irq = irq;
        this.softirq = softirq;
        this.steal = steal;
        this.guest = guest;
    }
    
    public CpuData(ArrayList<String> cpuInfo){
        this.user = Integer.parseInt((cpuInfo.get(0)));
        this.nice = Long.parseLong(cpuInfo.get(1));
        this.sysmode = Long.parseLong(cpuInfo.get(2));
        this.idle = Long.parseLong(cpuInfo.get(3));
        this.iowait = Long.parseLong(cpuInfo.get(4));
        this.irq = Long.parseLong(cpuInfo.get(5));
        this.softirq = Integer.parseInt((cpuInfo.get(6)));
        this.steal = Long.parseLong(cpuInfo.get(7));
        this.guest = Long.parseLong(cpuInfo.get(8));
    }
    
    public double totalUsage(){
    	double num = (user+sysmode)*100;
    	return num/(user+sysmode+idle);
    }

    @Override
    /**
     * CORE_ID, USER, NICE, SYSMODE, IDLE, IOWAIT, IRQ, SOFTIRQ, STEAL, GUEST.
     */
    public String toString() {
        return String.valueOf(this.coreId + ", ")
                + String.valueOf(this.user + ", ")
                + String.valueOf(this.nice + ", ")
                + String.valueOf(this.sysmode + ", ")
                + String.valueOf(this.idle + ", ")
                + String.valueOf(this.iowait + ", ")
                + String.valueOf(this.irq + ", ")
                + String.valueOf(this.softirq + ", ")
                + String.valueOf(this.steal + ", ")
                + String.valueOf(this.guest);
    }
    /**
     * Which core is being measured.
     */
    private int coreId;
    /**
     * Which is time spent running a virtual CPU for guest operating systems
     * under the control of the linux kernel.
     */
    private long guest;
    /**
     * Idle task.
     */
    private long idle;
    /**
     * I/O wait - time waiting for I/O to complete.
     */
    private long iowait;
    /**
     * Time servicing interrupts.
     */
    private long irq;
    /**
     * User mode with low priority.
     */
    private long nice;
    /**
     * Time serving softirqs.
     */
    private int softirq;
    /**
     * Stolen time, which is the time spent in other operating systems when
     * running in a virtualized environment.
     */
    private long steal;
    /**
     * System mode.
     */
    private long sysmode;
    /**
     * User mode.
     */
    private long user;

    @Override
    public void finalize() throws Throwable {
    }

    /**
     * @return the coreId
     */
    public int getCoreId() {
        return coreId;
    }

    /**
     * @return the guest
     */
    public long getGuest() {
        return guest;
    }

    /**
     * @return the idle
     */
    public long getIdle() {
        return idle;
    }

    /**
     * @return the iowait
     */
    public long getIowait() {
        return iowait;
    }

    /**
     * @return the irq
     */
    public long getIrq() {
        return irq;
    }

    /**
     * @return the nice
     */
    public long getNice() {
        return nice;
    }

    /**
     * @return the softirq
     */
    public int getSoftirq() {
        return softirq;
    }

    /**
     * @return the steal
     */
    public long getSteal() {
        return steal;
    }

    /**
     * @return the sysmode
     */
    public long getSysmode() {
        return sysmode;
    }

    /**
     * @return the user
     */
    public long getUser() {
        return user;
    }
}// end CpuData_t