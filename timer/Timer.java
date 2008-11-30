package timer;

/** 
 * The Timer class allows the execution of a specific action
 * when a timeout occurs. Once the timer is
 * set, it must be cleared via the reset() method, or the
 * timeout() method is called. Once 'max_tries' calls to timeout() 
 * have been made the terminate() method is called.
 * <p>
 * The timeout length is customizable by changing the 'm_length'
 * through the constructor. The 'm_length' represents the length
 * of the timer in milliseconds.
 *
 * @author David Reilly specialized by Laurent Vanbever
 */
public abstract class Timer extends Thread
{
	/** Rate at which timer is checked */
	protected int m_rate = 100;

	/** Number of tries **/
	protected int cur_tries;

	/** Maximum tries before giving up **/
	protected int max_tries;

	/** Length of timeout */
	private int m_length;

	/** Time elapsed */
	private int m_elapsed;

	private boolean finished = false;

    public Timer(){
        m_length = 1000;
        m_elapsed = 0;
        cur_tries = 0;
        this.max_tries = 10;
    }
	/**
	 * Creates a timer of a specified length
	 * @param	length	Length of time before timeout occurs
	 */
	public Timer(int length, int max_tries){
		// Assign to member variable
		m_length = length;

		// Set time elapsed
		m_elapsed = 0;

		// Set current tries 
		cur_tries = 0;

		// Set maximum tries before giving up
		this.max_tries = max_tries;
	}
    public synchronized void initialize(int length,int max_tries){
        m_length = length;
        this.max_tries = max_tries;
    }
	/** Stop the timer */
	public synchronized void stopTimer() 
	{
		finished = true;
	}

	/** Resets the timer back to zero */
	public synchronized void reset()
	{
		m_elapsed = 0;
		cur_tries = 0;
	}

	/** Performs timer specific code */
	public void run()
	{
		// Keep looping
		while(!finished)
		{
			// Put the timer to sleep
			try
			{ 
				Thread.sleep(m_rate);
			}
			catch (InterruptedException ioe) 
			{
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				m_elapsed += m_rate;

				// Check to see if the time has been exceeded
				if (m_elapsed > m_length)
				{
					if(!finished) {
						if(max_tries < 0 || cur_tries < max_tries) {
							// Trigger a timeout
							cur_tries ++;
							m_elapsed = 0;
							timeout();
						} else {
							// Maximum number of tries reached
							terminate();
						}
					}
				}
			}
		}
	}

	// Override this to provide custom functionality while a timeout occurs.
	public abstract void timeout();

	// Override this to provide custom functionality while the maximum number of tries has been reached.
	public abstract void terminate();
}