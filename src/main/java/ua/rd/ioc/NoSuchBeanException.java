package ua.rd.ioc;

public class NoSuchBeanException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchBeanException() {
		super("NoSuchBean");
	}
}
